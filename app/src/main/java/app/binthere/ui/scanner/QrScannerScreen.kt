package app.binthere.ui.scanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.binthere.ui.components.PlaceholderScreen
import app.binthere.ui.theme.BinthereTheme

const val QR_SCANNER_TEST_TAG = "screen.scanner"

@Composable
fun QrScannerScreen() {
    PlaceholderScreen(
        title = "Scan",
        subtitle = "Point at a bin QR to jump to its contents.",
        testTag = QR_SCANNER_TEST_TAG,
    )
}

@Preview
@Composable
private fun QrScannerScreenPreview() {
    BinthereTheme { QrScannerScreen() }
}
