package com.example.loancalcandroid.ui.common

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun AutoShrinkText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign = TextAlign.Start,
    minFontSize: TextUnit = 9.sp,
    maxLines: Int = 1,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        var fontSize by remember(text, maxWidth) { mutableStateOf(style.fontSize) }
        var readyToDraw by remember(text, maxWidth) { mutableStateOf(false) }

        Text(
            text = text,
            modifier = Modifier.drawWithContent { if (readyToDraw) drawContent() },
            color = style.color,
            fontSize = fontSize,
            fontStyle = style.fontStyle,
            fontWeight = style.fontWeight,
            fontFamily = style.fontFamily,
            letterSpacing = style.letterSpacing,
            textDecoration = style.textDecoration,
            textAlign = textAlign,
            lineHeight = style.lineHeight,
            maxLines = maxLines,
            overflow = TextOverflow.Clip,
            softWrap = false,
            onTextLayout = { result ->
                if (result.didOverflowWidth && fontSize.value > minFontSize.value) {
                    fontSize = (fontSize.value - 0.5f).sp
                } else {
                    readyToDraw = true
                }
            },
        )
    }
}
