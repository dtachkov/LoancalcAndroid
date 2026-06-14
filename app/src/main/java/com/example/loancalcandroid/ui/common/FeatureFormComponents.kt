package com.example.loancalcandroid.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import java.util.Date

private val FeatureSegmentSelected = Color(0xFF83D7E9)
private val FeatureSegmentUnselected = Color(0xFFE9E9E9)
val FeatureTableHeaderColor = Color(0xFFE8F7FA)
val FeatureTableSelectedColor = Color(0xFFB3E5FC)

@Composable
fun FeatureMoneyInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fieldWidth: Dp = 140.dp,
    error: String? = null,
    readOnly: Boolean = false,
) {
    FeatureInputRow(
        label = label,
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Decimal,
        modifier = modifier,
        fieldWidth = fieldWidth,
        error = error,
        readOnly = readOnly,
    )
}

@Composable
fun FeatureInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    fieldWidth: Dp = 140.dp,
    error: String? = null,
    readOnly: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.widthIn(min = fieldWidth, max = fieldWidth),
                singleLine = true,
                readOnly = readOnly,
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.End),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                decorationBox = { innerTextField ->
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd,
                        ) {
                            innerTextField()
                        }
                        HorizontalDivider(
                            color = if (error != null) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                        )
                    }
                },
            )
        }
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
fun FeatureDateRow(
    label: String,
    value: Date?,
    formattedValue: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = formattedValue,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 150.dp),
        )
    }
    HorizontalDivider()
}

@Composable
fun FeatureTypeSegmentedControl(
    decreaseAmountLabel: String,
    decreaseTermLabel: String,
    decreaseAmount: Boolean,
    onSelectAmount: () -> Unit,
    onSelectTerm: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        FeatureSegment(
            text = decreaseAmountLabel,
            selected = decreaseAmount,
            onClick = onSelectAmount,
            modifier = Modifier.weight(1f),
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(2.dp))
        FeatureSegment(
            text = decreaseTermLabel,
            selected = !decreaseAmount,
            onClick = onSelectTerm,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FeatureSegment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) FeatureSegmentSelected else FeatureSegmentUnselected)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

@Composable
fun FeatureCalculationProgress(
    progress: Int,
    max: Int,
    statusText: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (max > 0) {
            LinearProgressIndicator(
                progress = { progress.toFloat() / max.toFloat() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = LoanTextSecondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
fun FeatureResultTable(
    headers: List<String>,
    rows: List<List<String>>,
    selectedRowIndex: Int? = null,
    modifier: Modifier = Modifier,
    firstColumnWidth: Dp? = null,
    firstColumnWeight: Float = 1.2f,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FeatureTableHeaderColor)
                .padding(horizontal = 8.dp, vertical = 10.dp),
        ) {
            headers.forEachIndexed { index, header ->
                Text(
                    text = header,
                    modifier = firstColumnModifier(
                        index = index,
                        firstColumnWidth = firstColumnWidth,
                        firstColumnWeight = firstColumnWeight,
                        fillWidth = true,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start,
                )
            }
        }
        rows.forEachIndexed { rowIndex, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (rowIndex == selectedRowIndex) FeatureTableSelectedColor else Color.Transparent)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            ) {
                row.forEachIndexed { cellIndex, cell ->
                    Text(
                        text = cell,
                        modifier = firstColumnModifier(
                            index = cellIndex,
                            firstColumnWidth = firstColumnWidth,
                            firstColumnWeight = firstColumnWeight,
                            fillWidth = true,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                    )
                }
            }
            HorizontalDivider()
        }
    }
}

private fun RowScope.firstColumnModifier(
    index: Int,
    firstColumnWidth: Dp?,
    firstColumnWeight: Float,
    fillWidth: Boolean,
): Modifier {
    return when {
        index == 0 && firstColumnWidth != null -> Modifier.width(firstColumnWidth)
        index == 0 -> Modifier
            .weight(firstColumnWeight)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
        else -> Modifier
            .weight(1f)
            .then(if (fillWidth) Modifier.fillMaxWidth() else Modifier)
    }
}
