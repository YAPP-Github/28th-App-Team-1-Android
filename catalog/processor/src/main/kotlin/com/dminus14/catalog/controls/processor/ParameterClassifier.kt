package com.dminus14.catalog.controls.processor

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import com.squareup.kotlinpoet.ksp.toTypeName

/** 매개변수 타입이 초기 Controls 지원 범위에 포함되는지 판별한 결과다. */
internal sealed interface ParameterClassification {
    /** 지원 타입의 구체 Control 생성 정책을 제공한다. */
    data class Supported(
        val kind: ParameterKind,
    ) : ParameterClassification

    /**
     * 지원하지 않는 타입과 선택적인 상세 사유를 제공한다.
     *
     * @property typeName 진단에 표시할 원본 타입 표현
     * @property reason nullable, generic, empty enum처럼 별도 설명이 필요한 경우의 상세 사유
     */
    data class Unsupported(
        val typeName: String,
        val reason: String? = null,
    ) : ParameterClassification
}

/**
 * KSP [KSType]을 String, Boolean, 숫자 또는 enum Control 정책으로 분류한다.
 *
 * nullable과 type argument가 있는 타입은 먼저 거부한다. enum은 실제 선언의 항목을 읽어 비어 있지
 * 않은지 확인하며, 그 밖의 class, callback, collection과 Compose 전용 타입은 지원하지 않는 결과로
 * 반환해 호출자가 명확한 컴파일 진단을 만들 수 있게 한다.
 */
internal class ParameterClassifier {
    /** [type]의 지원 여부와 생성할 [ParameterKind]를 반환한다. */
    fun classify(type: KSType): ParameterClassification =
        when {
            type.nullability == Nullability.NULLABLE -> {
                ParameterClassification.Unsupported(
                    typeName = type.toString(),
                    reason = "Nullable types are not supported.",
                )
            }

            type.arguments.isNotEmpty() -> {
                ParameterClassification.Unsupported(
                    typeName = type.toString(),
                    reason = "Generic types are not supported.",
                )
            }

            else -> {
                classifyNonNullableType(type)
            }
        }

    private fun classifyNonNullableType(type: KSType): ParameterClassification {
        val renderedType = type.toString()
        val declaration = type.declaration
        val qualifiedName = declaration.qualifiedName?.asString().orEmpty()

        return when (qualifiedName) {
            "kotlin.String" -> {
                ParameterClassification.Supported(ParameterKind.StringKind)
            }

            "kotlin.Boolean" -> {
                ParameterClassification.Supported(ParameterKind.BooleanKind)
            }

            else -> {
                val numericType = NumericType.fromQualifiedName(qualifiedName)
                when {
                    numericType != null -> {
                        ParameterClassification.Supported(ParameterKind.NumberKind(numericType))
                    }

                    declaration is KSClassDeclaration &&
                        declaration.classKind == ClassKind.ENUM_CLASS -> {
                        classifyEnum(declaration)
                    }

                    else -> {
                        ParameterClassification.Unsupported(typeName = renderedType)
                    }
                }
            }
        }
    }

    private fun classifyEnum(declaration: KSClassDeclaration): ParameterClassification {
        val entries =
            declaration.declarations
                .filterIsInstance<KSClassDeclaration>()
                .filter { it.classKind == ClassKind.ENUM_ENTRY }
                .map { it.simpleName.asString() }
                .toList()

        return if (entries.isEmpty()) {
            ParameterClassification.Unsupported(
                typeName =
                    declaration.qualifiedName?.asString() ?: declaration.simpleName.asString(),
                reason =
                    "Empty enum is not supported for Catalog Controls: " +
                        declaration.simpleName.asString(),
            )
        } else {
            ParameterClassification.Supported(
                ParameterKind.EnumKind(entries = entries),
            )
        }
    }
}
