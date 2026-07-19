package com.dminus14.catalog.controls.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * KSP가 서비스 로더를 통해 Catalog Controls processor를 생성할 때 사용하는 진입점이다.
 *
 * 이 provider의 정규 이름은 `META-INF/services`에 등록되어 있다. 일반 애플리케이션 코드에서 직접
 * 생성하지 않으며, Gradle의 `kspWasmJs` configuration이 processor artifact를 로드하면 KSP가
 * [create]를 호출한다.
 */
class CatalogControlsSymbolProcessorProvider : SymbolProcessorProvider {
    /** KSP 환경의 출력기와 logger를 주입해 새 processor instance를 만든다. */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        CatalogControlsSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
}
