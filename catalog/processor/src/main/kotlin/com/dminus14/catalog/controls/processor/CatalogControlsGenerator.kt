package com.dminus14.catalog.controls.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

/**
 * 검증이 끝난 [AdapterModel]을 KotlinPoet 기반의 Args와 Controls 소스로 변환한다.
 *
 * 생성 파일은 어댑터와 같은 package에 위치하며 `${AdapterSimpleName}Args`와
 * `${AdapterSimpleName}Controls`를 함께 포함한다. 생성된 Controls는 Material 컴포넌트를 직접
 * 조립하지 않고 `catalog.controls.runtime`의 고정된 API만 호출한다. 각 출력은 원본 [AdapterModel]
 * 의 `originatingFile`을 연결한 isolating KSP output이므로 한 어댑터의 변경이 불필요하게 다른
 * 어댑터 출력을 무효화하지 않는다.
 *
 * @property codeGenerator KSP가 관리하는 generated source 출력기
 */
internal class CatalogControlsGenerator(
    private val codeGenerator: CodeGenerator,
) {
    /**
     * [adapter]의 매개변수 순서와 타입을 보존한 Args 및 Controls 파일을 한 번 생성한다.
     *
     * 생성 전 선언 검증은 [AdapterDeclarationValidator]의 책임이므로 이 함수는 유효한 모델만 받는다고
     * 가정한다.
     */
    fun generate(adapter: AdapterModel) {
        val argsType = buildArgsType(adapter)
        val controlsFunction = buildControlsFunction(adapter)

        FileSpec
            .builder(adapter.packageName, "${adapter.simpleName}CatalogControls")
            .addImport("androidx.compose.runtime", "getValue", "setValue")
            .addType(argsType)
            .addFunction(controlsFunction)
            .build()
            .writeTo(
                codeGenerator = codeGenerator,
                aggregating = false,
                originatingKSFiles = listOf(adapter.originatingFile),
            )
    }

    private fun buildArgsType(adapter: AdapterModel): TypeSpec {
        val constructor = FunSpec.constructorBuilder()
        val typeBuilder =
            TypeSpec
                .classBuilder(adapter.argsName)
                .addModifiers(KModifier.INTERNAL, KModifier.DATA)

        adapter.parameters.forEach { parameter ->
            constructor.addParameter(
                ParameterSpec.builder(parameter.name, parameter.typeName).build(),
            )
            typeBuilder.addProperty(
                PropertySpec
                    .builder(parameter.name, parameter.typeName)
                    .initializer("%N", parameter.name)
                    .build(),
            )
        }

        return typeBuilder
            .primaryConstructor(constructor.build())
            .build()
    }

    private fun buildControlsFunction(adapter: AdapterModel): FunSpec {
        val argsType = com.squareup.kotlinpoet.ClassName(adapter.packageName, adapter.argsName)
        val function =
            FunSpec
                .builder(adapter.controlsName)
                .addAnnotation(CatalogControlsNames.composable)
                .addModifiers(KModifier.INTERNAL)
                .addParameter("initialArgs", argsType)

        function.addStatement(
            "var args by %M(initialArgs) { %M(initialArgs) }",
            CatalogControlsNames.remember,
            CatalogControlsNames.mutableStateOf,
        )

        val numericParameters =
            adapter.parameters.mapIndexedNotNull { index, parameter ->
                if (parameter.kind is ParameterKind.NumberKind) index to parameter else null
            }
        numericParameters.forEach { (index, parameter) ->
            function.addStatement(
                "var numberInput%L by %M(initialArgs) { %M(initialArgs.%N.toString()) }",
                index,
                CatalogControlsNames.remember,
                CatalogControlsNames.mutableStateOf,
                parameter.name,
            )
            function.addStatement(
                "var numberError%L by %M(initialArgs) { %M<String?>(null) }",
                index,
                CatalogControlsNames.remember,
                CatalogControlsNames.mutableStateOf,
            )
        }

        addInitialArgsValidation(function, adapter)
        function.addCode(buildControlledStoryLayout(adapter))

        return function.build()
    }

    private fun addInitialArgsValidation(
        function: FunSpec.Builder,
        adapter: AdapterModel,
    ) {
        val finiteParameters =
            adapter.parameters.filter { parameter ->
                val numberKind = parameter.kind as? ParameterKind.NumberKind
                numberKind?.type?.requiresFiniteInitialValue == true
            }
        if (finiteParameters.isEmpty()) return

        val invalidValues = CodeBlock.builder().add("listOfNotNull(\n").indent()
        finiteParameters.forEach { parameter ->
            invalidValues.add(
                "if (initialArgs.%N.isFinite()) null else %S + initialArgs.%N,\n",
                parameter.name,
                "${parameter.name}=",
                parameter.name,
            )
        }
        invalidValues.unindent().add(")")

        function.addStatement("val invalidInitialArgs = %L", invalidValues.build())
        function.beginControlFlow("if (invalidInitialArgs.isNotEmpty())")
        function.addCode(
            CodeBlock
                .builder()
                .add("%M(\n", CatalogControlsNames.controlledStoryLayout)
                .indent()
                .add("preview = { %M() },\n", CatalogControlsNames.previewUnavailable)
                .add("controls = {\n")
                .indent()
                .add(
                    "%M(\n",
                    CatalogControlsNames.controlsError,
                ).indent()
                .add(
                    "message = %S + invalidInitialArgs.joinToString(),\n",
                    "Invalid initial arguments: ",
                ).unindent()
                .add(")\n")
                .unindent()
                .add("},\n")
                .unindent()
                .add(")\n")
                .build(),
        )
        function.addStatement("return")
        function.endControlFlow()
    }

    private fun buildControlledStoryLayout(adapter: AdapterModel): CodeBlock =
        CodeBlock
            .builder()
            .add("%M(\n", CatalogControlsNames.controlledStoryLayout)
            .indent()
            .add("preview = {\n")
            .indent()
            .addAdapterCall(adapter)
            .unindent()
            .add("},\n")
            .add("controls = {\n")
            .indent()
            .addControls(adapter)
            .unindent()
            .add("},\n")
            .unindent()
            .add(")\n")
            .build()

    private fun CodeBlock.Builder.addAdapterCall(adapter: AdapterModel): CodeBlock.Builder {
        add("%N(\n", adapter.simpleName)
        indent()
        adapter.parameters.forEach { parameter ->
            add("%N = args.%N,\n", parameter.name, parameter.name)
        }
        unindent()
        add(")\n")
        return this
    }
}
