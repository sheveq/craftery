package szeweq.craftery.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent
) = BasicTextField(
    value,
    onValueChange,
    modifier.background(background, MaterialTheme.shapes.small).padding(4.dp),
    singleLine = true,
    textStyle = TextStyle(MaterialTheme.colors.onSurface),
    cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
)

@Composable
fun ThreeLinesItem(item: Triple<String, String, String>, second: String, third: String) {
    Text(item.first, fontWeight = FontWeight.Bold)
    Text("$second: " + item.second)
    Text("$third: " + item.third)
}

/**
 * Returns a simple text component that only uses [text] and [fontSize] parameters.
 * It saves a lot of bytecode size if used inside scoped components (rows, columns, buttons, etc.).
 */
fun ComposeScopeText(text: String, fontSize: TextUnit = TextUnit.Unspecified): @Composable Any.() -> Unit = {
    Text(text, fontSize = fontSize)
}

/**
 * Returns a simple text component that only uses [text] and [fontSize] parameters.
 * It saves a lot of bytecode size if used inside another component that does not use any scope.
 */
fun ComposeText(text: String, fontSize: TextUnit = TextUnit.Unspecified): @Composable () -> Unit = {
    Text(text, fontSize = fontSize)
}

@Composable
fun TextH5(text: String, modifier: Modifier = Modifier) {
    val textColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    BasicText(text, modifier, style = MaterialTheme.typography.h5 + TextStyle(color = textColor))
}
