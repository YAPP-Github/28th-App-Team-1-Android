package com.dminus14.catalog.controls.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate

/**
 * KSP round마다 `@CatalogControls` symbol을 찾아 검증하고 generated source를 출력한다.
 *
 * `inDepth = true` 조회를 사용해 KSP 기본 조회에서 빠지는 local 함수도 발견하고 명시적인 사용 오류를
 * 보고한다. 아직 type resolution이 끝나지 않은 symbol은 다음 round로 지연하며, 이미 처리한 어댑터는
 * [processedAdapters]로 추적해 같은 파일을 중복 생성하지 않는다.
 *
 * @param codeGenerator generated Kotlin source를 기록할 KSP 출력기
 * @param logger 잘못된 어댑터 선언을 소스 위치에 보고할 logger
 */
internal class CatalogControlsSymbolProcessor(
    codeGenerator: CodeGenerator,
    logger: KSPLogger,
) : SymbolProcessor {
    private val validator = AdapterDeclarationValidator(logger)
    private val generator = CatalogControlsGenerator(codeGenerator)
    private val processedAdapters = mutableSetOf<String>()

    /**
     * 현재 round에서 해석 가능한 어댑터를 생성하고, 아직 유효성 검사가 끝나지 않은 symbol을 반환한다.
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deferred = mutableListOf<KSAnnotated>()

        resolver
            .getSymbolsWithAnnotation(
                annotationName = CatalogControlsNames.CATALOG_CONTROLS_ANNOTATION,
                inDepth = true,
            ).forEach { symbol ->
                val function = symbol as? KSFunctionDeclaration
                if (function == null) return@forEach

                if (!function.validate()) {
                    deferred += function
                    return@forEach
                }

                val adapterId =
                    function.qualifiedName?.asString()
                        ?: "${function.packageName.asString()}.${function.simpleName.asString()}"
                if (adapterId in processedAdapters) return@forEach

                val adapter = validator.validate(function, resolver) ?: return@forEach
                generator.generate(adapter)
                processedAdapters += adapterId
            }

        return deferred
    }
}
