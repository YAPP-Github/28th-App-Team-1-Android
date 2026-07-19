package com.dminus14.catalog.controls.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ksp.toTypeName

/**
 * `@CatalogControls`가 붙은 함수가 코드 생성 계약을 만족하는지 검증하고 [AdapterModel]로 변환한다.
 *
 * 함수 형태, 가시성, `@Composable`, 반환 타입과 매개변수 타입을 한 번에 검증한다. 같은 package의
 * overload와 생성 예정 이름 충돌도 여기서 차단하여 KotlinPoet이 모호하거나 컴파일할 수 없는 코드를
 * 만들지 않게 한다. 오류는 가능한 한 문제가 있는 KSP symbol에 연결해 개발자가 선언 위치에서 바로
 * 원인을 확인할 수 있도록 한다.
 *
 * @property logger KSP 컴파일 진단을 소스 symbol에 기록하는 logger
 * @property parameterClassifier 매개변수의 지원 여부와 Control 종류를 판별하는 분류기
 */
internal class AdapterDeclarationValidator(
    private val logger: KSPLogger,
    private val parameterClassifier: ParameterClassifier = ParameterClassifier(),
) {
    /**
     * [function]과 같은 package의 선언을 [resolver]로 함께 검사한다.
     *
     * 모든 조건을 만족하면 생성기가 사용할 [AdapterModel]을 반환하고, 하나라도 위반하면 관련 오류를
     * 모두 기록한 뒤 `null`을 반환한다. 여러 오류를 한 번에 보여 주기 위해 첫 실패에서 즉시 중단하지
     * 않는다.
     */
    @OptIn(KspExperimental::class)
    fun validate(
        function: KSFunctionDeclaration,
        resolver: Resolver,
    ): AdapterModel? {
        val packageName = function.packageName.asString()
        val simpleName = function.simpleName.asString()
        var valid = validateFunctionShape(function)
        if (!validateGeneratedNames(function, resolver, packageName, simpleName)) valid = false
        val parameters =
            validateParameters(function)
                ?: run {
                    valid = false
                    emptyList()
                }
        val originatingFile = function.containingFile
        if (originatingFile == null) {
            logger.error("@CatalogControls adapter must be declared in a source file.", function)
            valid = false
        }

        if (!valid || originatingFile == null) return null

        return AdapterModel(
            packageName = packageName,
            simpleName = simpleName,
            parameters = parameters,
            originatingFile = originatingFile,
        )
    }

    private fun validateFunctionShape(function: KSFunctionDeclaration): Boolean {
        val conditions =
            listOf(
                (function.parentDeclaration == null && function.qualifiedName != null) to
                    "@CatalogControls is only supported on non-local top-level functions.",
                (Modifier.INTERNAL in function.modifiers) to
                    "@CatalogControls adapter must have internal visibility.",
                function.hasComposableAnnotation() to
                    "@CatalogControls adapter must also be annotated with @Composable.",
                (
                    function.returnType
                        ?.resolve()
                        ?.declaration
                        ?.qualifiedName
                        ?.asString() ==
                        "kotlin.Unit"
                ) to "@CatalogControls adapter must return Unit.",
                (function.extensionReceiver == null) to
                    "Extension functions are not supported for @CatalogControls adapters.",
                function.typeParameters.isEmpty() to
                    "Generic functions are not supported for @CatalogControls adapters.",
                function.parameters.none { it.isVararg } to
                    "Vararg parameters are not supported for @CatalogControls adapters.",
                function.parameters.isNotEmpty() to
                    "@CatalogControls adapter must declare at least one control parameter.",
            )

        var valid = true
        conditions.filterNot { it.first }.forEach { (_, message) ->
            logger.error(message, function)
            valid = false
        }
        return valid
    }

    private fun KSFunctionDeclaration.hasComposableAnnotation(): Boolean =
        annotations.any { annotation ->
            annotation.annotationType
                .resolve()
                .declaration.qualifiedName
                ?.asString() ==
                CatalogControlsNames.COMPOSABLE_ANNOTATION
        }

    @OptIn(KspExperimental::class)
    private fun validateGeneratedNames(
        function: KSFunctionDeclaration,
        resolver: Resolver,
        packageName: String,
        simpleName: String,
    ): Boolean {
        val declarations = resolver.getDeclarationsFromPackage(packageName).toList()
        var valid = true
        if (declarations.filterIsInstance<KSFunctionDeclaration>().count {
                it.simpleName.asString() == simpleName
            } != 1
        ) {
            logger.error(
                "Overloaded @CatalogControls adapters are not supported: $packageName.$simpleName",
                function,
            )
            valid = false
        }

        val declarationNames = declarations.map { it.simpleName.asString() }.toSet()
        listOf("${simpleName}Args", "${simpleName}Controls").forEach { generatedName ->
            if (generatedName in declarationNames) {
                logger.error(
                    "Generated Catalog Controls name conflicts with an existing declaration: " +
                        "$packageName.$generatedName",
                    function,
                )
                valid = false
            }
        }
        return valid
    }

    private fun validateParameters(function: KSFunctionDeclaration): List<ParameterModel>? {
        var valid = true
        val parameters =
            function.parameters.mapNotNull { parameter ->
                val parameterName = parameter.name?.asString().orEmpty()
                val resolvedType = parameter.type.resolve()
                when (val classification = parameterClassifier.classify(resolvedType)) {
                    is ParameterClassification.Supported -> {
                        ParameterModel(
                            name = parameterName,
                            typeName = resolvedType.toTypeName(),
                            kind = classification.kind,
                        )
                    }

                    is ParameterClassification.Unsupported -> {
                        val message =
                            classification.reason ?: unsupportedTypeMessage(classification.typeName)
                        logger.error("Parameter '$parameterName': $message", parameter)
                        valid = false
                        null
                    }
                }
            }
        return parameters.takeIf { valid }
    }

    private fun unsupportedTypeMessage(typeName: String): String =
        "Unsupported control parameter type: $typeName. " +
            "@CatalogControls currently supports String, Boolean, numeric types, and enum classes."
}
