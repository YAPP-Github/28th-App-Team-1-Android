import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import ui.CatalogApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val root = document.getElementById("compose-root") as HTMLElement

    ComposeViewport(root) {
        CatalogApp()
    }
}
