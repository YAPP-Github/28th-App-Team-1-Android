package com.dminus14.catalog.controls.processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import com.tschuchort.compiletesting.kspSourcesDir
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class CatalogControlsProcessorTest {
    @Test
    fun `지원 매개변수에서 컴파일 가능한 Args와 Controls를 생성한다`() {
        val compilation =
            compilation(
                SourceFile.kotlin(
                    "SupportedAdapter.kt",
                    """
                    package fixtures

                    import androidx.compose.runtime.Composable
                    import com.dminus14.catalog.controls.CatalogControls

                    enum class Style { Primary, Secondary }

                    @CatalogControls
                    @Composable
                    internal fun SupportedAdapter(
                        text: String,
                        enabled: Boolean,
                        byteValue: Byte,
                        shortValue: Short,
                        intValue: Int,
                        longValue: Long,
                        floatValue: Float,
                        doubleValue: Double,
                        style: Style,
                    ) = Unit
                    """,
                ),
            )

        val result = compilation.compile()

        assertEquals(result.messages, KotlinCompilation.ExitCode.OK, result.exitCode)
        val generated = compilation.generatedSource("SupportedAdapterCatalogControls.kt")
        assertTrue(generated.contains("internal data class SupportedAdapterArgs"))
        assertTrue(generated.contains("val text: String"))
        assertTrue(generated.contains("val byteValue: Byte"))
        assertTrue(generated.contains("val style: Style"))
        assertTrue(generated.contains("text = args.text"))
        assertTrue(generated.contains("parseCatalogByte(newValue)"))
        assertTrue(generated.contains("parseCatalogDouble(newValue)"))
        assertTrue(generated.contains("options = Style.entries.toList()"))
        assertTrue(generated.contains("initialArgs.floatValue.isFinite()"))
        assertTrue(generated.contains("CatalogPreviewUnavailable()"))
        assertTrue(generated.contains("CatalogControlsError("))
    }

    @Test
    fun `서로 다른 패키지의 같은 어댑터 이름을 독립적으로 생성한다`() {
        val compilation =
            compilation(
                SourceFile.kotlin(
                    "FirstAdapter.kt",
                    validAdapterSource(packageName = "first"),
                ),
                SourceFile.kotlin(
                    "SecondAdapter.kt",
                    validAdapterSource(packageName = "second"),
                ),
            )

        val result = compilation.compile()

        assertEquals(result.messages, KotlinCompilation.ExitCode.OK, result.exitCode)
        val generatedFiles =
            compilation.kspSourcesDir
                .walkTopDown()
                .filter { it.name == "SharedAdapterCatalogControls.kt" }
                .toList()
        assertEquals(2, generatedFiles.size)
        assertTrue(generatedFiles.any { it.readText().contains("package first") })
        assertTrue(generatedFiles.any { it.readText().contains("package second") })
    }

    @Test
    fun `지원하지 않는 선언에 구체적인 진단을 제공한다`() {
        val compilation =
            compilation(
                SourceFile.kotlin(
                    "InvalidAdapters.kt",
                    """
                    package invalid

                    import androidx.compose.runtime.Composable
                    import com.dminus14.catalog.controls.CatalogControls

                    class Unsupported

                    @CatalogControls
                    @Composable
                    private fun PrivateAdapter(value: String) = Unit

                    @CatalogControls
                    internal fun MissingComposable(value: String) = Unit

                    @CatalogControls
                    @Composable
                    internal fun UnsupportedAdapter(value: Unsupported) = Unit

                    @CatalogControls
                    @Composable
                    internal fun NullableAdapter(value: String?) = Unit
                    """,
                ),
            )

        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("must have internal visibility"))
        assertTrue(result.messages.contains("must also be annotated with @Composable"))
        assertTrue(
            result.messages.contains("Parameter 'value': Unsupported control parameter type"),
        )
        assertTrue(result.messages.contains("Nullable types are not supported"))
    }

    @Test
    fun `빈 enum과 오버로드와 생성 이름 충돌을 진단한다`() {
        val compilation =
            compilation(
                SourceFile.kotlin(
                    "ConflictingAdapters.kt",
                    """
                    package conflict

                    import androidx.compose.runtime.Composable
                    import com.dminus14.catalog.controls.CatalogControls

                    enum class Empty

                    @CatalogControls
                    @Composable
                    internal fun EmptyEnumAdapter(value: Empty) = Unit

                    @CatalogControls
                    @Composable
                    internal fun OverloadedAdapter(value: String) = Unit

                    internal fun OverloadedAdapter(value: Boolean) = Unit

                    internal class CollisionAdapterArgs

                    @CatalogControls
                    @Composable
                    internal fun CollisionAdapter(value: String) = Unit
                    """,
                ),
            )

        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(result.messages.contains("Empty enum is not supported"))
        assertTrue(
            result.messages.contains("Overloaded @CatalogControls adapters are not supported"),
        )
        assertTrue(result.messages.contains("CollisionAdapterArgs"))
    }

    @Test
    fun `지원하지 않는 함수 형태를 구체적으로 진단한다`() {
        val compilation =
            compilation(
                SourceFile.kotlin(
                    "InvalidFunctionShapes.kt",
                    """
                    package shapes

                    import androidx.compose.runtime.Composable
                    import com.dminus14.catalog.controls.CatalogControls

                    internal class Holder {
                        @CatalogControls
                        @Composable
                        internal fun MemberAdapter(value: String) = Unit
                    }

                    @CatalogControls
                    @Composable
                    internal fun String.ExtensionAdapter(value: String) = Unit

                    @CatalogControls
                    @Composable
                    internal fun <T> GenericAdapter(value: String) = Unit

                    @CatalogControls
                    @Composable
                    internal fun VarargAdapter(vararg value: String) = Unit

                    @CatalogControls
                    @Composable
                    internal fun NonUnitAdapter(value: String): Boolean = value.isNotEmpty()

                    internal fun declareLocalAdapter() {
                        @CatalogControls
                        @Composable
                        fun LocalAdapter(value: String) = Unit
                    }
                    """,
                ),
            )

        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(
            result.messages.contains(
                "@CatalogControls is only supported on non-local top-level functions",
            ),
        )
        assertTrue(result.messages.contains("Extension functions are not supported"))
        assertTrue(result.messages.contains("Generic functions are not supported"))
        assertTrue(result.messages.contains("Vararg parameters are not supported"))
        assertTrue(result.messages.contains("must return Unit"))
    }

    @Test
    fun `지역 어댑터에 최상위 함수 진단을 제공한다`() {
        val compilation =
            compilation(
                SourceFile.kotlin(
                    "LocalAdapter.kt",
                    """
                    package local

                    import androidx.compose.runtime.Composable
                    import com.dminus14.catalog.controls.CatalogControls

                    internal fun declareLocalAdapter() {
                        @CatalogControls
                        @Composable
                        fun LocalAdapter(value: String) = Unit
                    }
                    """,
                ),
            )

        val result = compilation.compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue(
            result.messages.contains(
                "@CatalogControls is only supported on non-local top-level functions",
            ),
        )
    }

    private fun compilation(vararg sources: SourceFile): KotlinCompilation =
        KotlinCompilation().apply {
            this.sources = runtimeStubs + sources
            configureKsp {
                symbolProcessorProviders += CatalogControlsSymbolProcessorProvider()
            }
            inheritClassPath = true
            messageOutputStream = System.out
        }

    private fun KotlinCompilation.generatedSource(name: String): String =
        kspSourcesDir
            .walkTopDown()
            .first { it.name == name }
            .readText()

    private fun validAdapterSource(packageName: String): String =
        """
        package $packageName

        import androidx.compose.runtime.Composable
        import com.dminus14.catalog.controls.CatalogControls

        @CatalogControls
        @Composable
        internal fun SharedAdapter(value: String) = Unit
        """

    private val runtimeStubs =
        listOf(
            SourceFile.kotlin(
                "ComposeRuntime.kt",
                """
                package androidx.compose.runtime

                import kotlin.reflect.KProperty

                @Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
                annotation class Composable

                class MutableState<T>(var value: T)

                fun <T> mutableStateOf(value: T): MutableState<T> = MutableState(value)
                fun <T> remember(key: Any?, calculation: () -> T): T = calculation()

                operator fun <T> MutableState<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value
                operator fun <T> MutableState<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
                    this.value = value
                }
                """,
            ),
            SourceFile.kotlin(
                "CatalogControlsRuntime.kt",
                """
                package catalog.controls.runtime

                import androidx.compose.runtime.Composable

                data class CatalogNumberParseResult<T>(val value: T?, val errorMessage: String?)

                @Composable
                fun CatalogControlledStoryLayout(
                    preview: @Composable () -> Unit,
                    controls: @Composable () -> Unit,
                ) {
                    preview()
                    controls()
                }

                @Composable
                fun CatalogTextControl(name: String, value: String, onValueChange: (String) -> Unit) = Unit

                @Composable
                fun CatalogBooleanControl(name: String, value: Boolean, onValueChange: (Boolean) -> Unit) = Unit

                @Composable
                fun CatalogNumberControl(
                    name: String,
                    rawValue: String,
                    errorMessage: String?,
                    onValueChange: (String) -> Unit,
                ) = Unit

                @Composable
                fun <T : Enum<T>> CatalogEnumControl(
                    name: String,
                    value: T,
                    options: List<T>,
                    onValueChange: (T) -> Unit,
                ) = Unit

                @Composable
                fun CatalogControlsError(message: String) = Unit

                @Composable
                fun CatalogPreviewUnavailable() = Unit

                fun parseCatalogByte(rawValue: String) = CatalogNumberParseResult(rawValue.toByteOrNull(), null)
                fun parseCatalogShort(rawValue: String) = CatalogNumberParseResult(rawValue.toShortOrNull(), null)
                fun parseCatalogInt(rawValue: String) = CatalogNumberParseResult(rawValue.toIntOrNull(), null)
                fun parseCatalogLong(rawValue: String) = CatalogNumberParseResult(rawValue.toLongOrNull(), null)
                fun parseCatalogFloat(rawValue: String) = CatalogNumberParseResult(rawValue.toFloatOrNull(), null)
                fun parseCatalogDouble(rawValue: String) = CatalogNumberParseResult(rawValue.toDoubleOrNull(), null)
                """,
            ),
        )
}
