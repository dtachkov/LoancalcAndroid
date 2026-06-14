package com.example.loancalcandroid.ui.analytics.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.theme.LoanGreen
import com.example.loancalcandroid.ui.theme.LoanRed
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import com.example.loancalcandroid.util.Formatters
import ru.kredit.calculator.data.calculation.AnalyticsTimelinePoint
import ru.kredit.calculator.data.calculation.DebtByLoanSlice
import ru.kredit.calculator.data.calculation.LoanInterestComparison
import ru.kredit.calculator.data.calculation.RepaymentProgressTotals
import ru.kredit.calculator.data.calculation.YearlyDebtLoad
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.luminance
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private val AnalyticsSliceColors = listOf(
    Color(0xFFFFC107),
    Color(0xFF4CAF50),
    Color(0xFFE91E63),
    Color(0xFFFF9800),
    Color(0xFF2196F3),
    Color(0xFF9C27B0),
    Color(0xFF00BCD4),
    Color(0xFF795548),
)

private val TimelineDebtColor = Color(0xFF2196F3)
private val TimelineInterestColor = Color(0xFFE53935)
private val ComparisonPrincipalColor = Color(0xFFE91E63)
private val ComparisonInterestColor = Color(0xFFFF9800)

@Composable
fun DebtDonutChart(
    slices: List<DebtByLoanSlice>,
    totalDebt: Double,
    modifier: Modifier = Modifier,
) {
    if (slices.isEmpty()) return

    val colors = remember(slices) {
        slices.mapIndexed { index, _ -> AnalyticsSliceColors[index % AnalyticsSliceColors.size] }
    }
    val textMeasurer = rememberTextMeasurer()
    val total = slices.sumOf { it.debt }.takeIf { it > 0.0 } ?: return

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartSize = min(size.width, size.height)
                val strokeWidth = chartSize * 0.16f
                val diameter = chartSize - strokeWidth
                val center = Offset(size.width / 2f, size.height / 2f)
                val topLeft = Offset(center.x - diameter / 2f, center.y - diameter / 2f)
                val ringRadius = diameter / 2f
                var startAngle = -90f

                slices.forEachIndexed { index, slice ->
                    val sweep = (slice.debt / total * 360f).toFloat().coerceAtLeast(0.1f)
                    val color = colors[index]

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
                    )

                    val sharePercent = (slice.debt / total * 100.0).roundToInt()
                    if (sweep >= 18f) {
                        val midAngleDeg = startAngle + sweep / 2f
                        val midAngleRad = Math.toRadians(midAngleDeg.toDouble())
                        val cosA = cos(midAngleRad).toFloat()
                        val sinA = sin(midAngleRad).toFloat()
                        val innerLabel = "$sharePercent%"
                        val innerColor = if (color.luminance() > 0.55f) Color.Black else Color.White
                        val textLayout = textMeasurer.measure(
                            text = innerLabel,
                            style = TextStyle(
                                fontSize = 10.sp,
                                color = innerColor,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(
                                center.x + cosA * ringRadius - textLayout.size.width / 2f,
                                center.y + sinA * ringRadius - textLayout.size.height / 2f,
                            ),
                        )
                    }

                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.analytics_you_owe),
                    style = MaterialTheme.typography.bodySmall,
                    color = LoanTextSecondary,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = Formatters.money(totalDebt),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            slices.forEachIndexed { index, slice ->
                val sharePercent = (slice.debt / total * 100.0).roundToInt()
                DebtDonutLegendRow(
                    color = colors[index],
                    title = slice.label,
                    amount = Formatters.money(slice.debt),
                    sharePercent = sharePercent,
                )
            }
        }
    }
}

@Composable
private fun DebtDonutLegendRow(
    color: Color,
    title: String,
    amount: String,
    sharePercent: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color),
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "$sharePercent%",
            style = MaterialTheme.typography.bodyMedium,
            color = LoanTextSecondary,
            modifier = Modifier.widthIn(min = 40.dp),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun LoanInterestComparisonChart(
    comparison: LoanInterestComparison,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val values = remember(comparison) {
        listOf(comparison.totalLoanAmount, comparison.totalOverpay)
    }
    val barColors = listOf(ComparisonPrincipalColor, ComparisonInterestColor)
    val bottomLabels = listOf(
        stringResource(R.string.analytics_total_loans_amount),
        stringResource(R.string.analytics_total_interest_accrued),
    )
    val valueLabelStyle = TextStyle(fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    val bottomLabelStyle = MaterialTheme.typography.bodySmall.copy(color = LoanTextSecondary, textAlign = TextAlign.Center)
    val maxValue = remember(values) { values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0 }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) {
            val barCount = values.size
            val sectionWidth = size.width / barCount
            val barWidth = sectionWidth * 0.42f
            val topLabelArea = 36f
            val bottomGap = 8f
            val chartHeight = (size.height - topLabelArea - bottomGap).coerceAtLeast(1f)
            val baseY = topLabelArea + chartHeight

            drawLine(
                color = Color(0xFFBDBDBD),
                start = Offset(0f, baseY),
                end = Offset(size.width, baseY),
                strokeWidth = 1f,
            )

            values.forEachIndexed { index, value ->
                val centerX = sectionWidth * index + sectionWidth / 2f
                val barHeight = (value / maxValue * chartHeight).toFloat().coerceAtLeast(1f)
                val barLeft = centerX - barWidth / 2f

                drawRect(
                    color = barColors[index],
                    topLeft = Offset(barLeft, baseY - barHeight),
                    size = Size(barWidth, barHeight),
                )

                val valueLabel = Formatters.moneyWithoutDecimal(value)
                val valueLayout = textMeasurer.measure(text = valueLabel, style = valueLabelStyle)
                drawText(
                    textLayoutResult = valueLayout,
                    topLeft = Offset(
                        centerX - valueLayout.size.width / 2f,
                        baseY - barHeight - valueLayout.size.height - 6f,
                    ),
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            bottomLabels.forEach { label ->
                Text(
                    text = label,
                    style = bottomLabelStyle,
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                    overflow = TextOverflow.Visible,
                )
            }
        }
    }
}

@Composable
fun DebtInterestTimelineChart(
    points: List<AnalyticsTimelinePoint>,
    yAxisMax: Double,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) return

    val debtValues = remember(points) { points.map { it.remainingDebt } }
    val interestValues = remember(points) { points.map { it.cumulativeInterest } }
    val axisLabels = remember(points) { points.map { it.axisLabel } }
    val chartMaxY = remember(yAxisMax, points) {
        val fallback = max(
            debtValues.maxOrNull() ?: 0.0,
            interestValues.maxOrNull() ?: 0.0,
        )
        if (yAxisMax > 0.0) yAxisMax else fallback * 1.1
    }
    val labelIndices = remember(points.size) {
        if (points.size <= 1) {
            listOf(0)
        } else {
            val labelCount = minOf(12, points.size)
            (0 until labelCount).map { index ->
                (index * (points.size - 1)) / (labelCount - 1)
            }
        }
    }
    val textMeasurer = rememberTextMeasurer()
    val gridColor = Color(0xFFBDBDBD)
    val xLabelStyle = TextStyle(fontSize = 9.sp, color = LoanTextSecondary)
    val maxXLabelWidth = remember(axisLabels, labelIndices) {
        labelIndices.maxOfOrNull { index ->
            val label = axisLabels.getOrElse(index) { "" }
            if (label.isEmpty()) {
                0f
            } else {
                textMeasurer.measure(text = label, style = xLabelStyle).size.width.toFloat()
            }
        } ?: 0f
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 8.dp),
        ) {
            val horizontalLines = 5
            val yLabelStyle = TextStyle(fontSize = 9.sp, color = LoanTextSecondary)
            val yLabelWidths = (0..horizontalLines).map { lineIndex ->
                val fraction = lineIndex / horizontalLines.toFloat()
                textMeasurer.measure(
                    text = Formatters.moneyWithoutDecimal(chartMaxY * fraction),
                    style = yLabelStyle,
                ).size.width
            }
            val leftPadding = yLabelWidths.maxOrNull()?.plus(12f) ?: 52f
            val rightPadding = 8f
            val topPadding = 12f
            val xLabelGap = 14f
            val bottomPadding = xLabelGap + maxXLabelWidth + 16f
            val chartWidth = (size.width - leftPadding - rightPadding).coerceAtLeast(1f)
            val chartHeight = (size.height - topPadding - bottomPadding).coerceAtLeast(1f)
            val pointCount = points.size
            val lastIndex = (pointCount - 1).coerceAtLeast(1)

            fun xAt(index: Int): Float {
                return leftPadding + chartWidth * index / lastIndex
            }

            fun yAt(value: Double): Float {
                val fraction = (value / chartMaxY).toFloat().coerceIn(0f, 1f)
                return topPadding + chartHeight * (1f - fraction)
            }

            repeat(horizontalLines + 1) { lineIndex ->
                val fraction = lineIndex / horizontalLines.toFloat()
                val y = topPadding + chartHeight * (1f - fraction)
                drawLine(
                    color = gridColor,
                    start = Offset(leftPadding, y),
                    end = Offset(leftPadding + chartWidth, y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
                )
                val layout = textMeasurer.measure(
                    text = Formatters.moneyWithoutDecimal(chartMaxY * fraction),
                    style = yLabelStyle,
                )
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        leftPadding - layout.size.width - 8f,
                        y - layout.size.height / 2f,
                    ),
                )
            }

            fun drawSeries(values: List<Double>, color: Color) {
                if (values.isEmpty()) return
                val path = Path()
                values.forEachIndexed { index, value ->
                    val point = Offset(xAt(index), yAt(value))
                    if (index == 0) {
                        path.moveTo(point.x, point.y)
                    } else {
                        path.lineTo(point.x, point.y)
                    }
                }
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round),
                )
            }

            drawSeries(debtValues, TimelineDebtColor)
            drawSeries(interestValues, TimelineInterestColor)

            val axisLineY = topPadding + chartHeight

            drawLine(
                color = Color.DarkGray,
                start = Offset(leftPadding, axisLineY),
                end = Offset(leftPadding + chartWidth, axisLineY),
                strokeWidth = 1f,
            )

            labelIndices.forEach { index ->
                val label = axisLabels.getOrElse(index) { "" }
                if (label.isEmpty()) return@forEach
                val labelWidth = textMeasurer.measure(text = label, style = xLabelStyle).size.width.toFloat()
                val labelAnchorX = xAt(index)
                val labelAnchorY = axisLineY + xLabelGap + labelWidth / 2f
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.save()
                    canvas.nativeCanvas.rotate(90f, labelAnchorX, labelAnchorY)
                    canvas.nativeCanvas.drawText(
                        label,
                        labelAnchorX,
                        labelAnchorY,
                        android.graphics.Paint().apply {
                            textSize = 9.sp.toPx()
                            color = LoanTextSecondary.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                        },
                    )
                    canvas.nativeCanvas.restore()
                }
            }
        }

        AnalyticsLegend(
            items = listOf(
                TimelineDebtColor to stringResource(R.string.analytics_remaining_debt),
                TimelineInterestColor to stringResource(R.string.analytics_paid_interest),
            ),
        )
    }
}

@Composable
fun RepaymentProgressBlock(
    progress: RepaymentProgressTotals,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        RepaymentProgressRow(
            leftValue = Formatters.money(progress.paidPrincipal),
            rightValue = Formatters.money(progress.remainingDebt),
            leftLabel = stringResource(R.string.analytics_debt_paid),
            rightLabel = stringResource(R.string.analytics_remaining_to_pay),
            paidFraction = progress.paidPrincipal /
                (progress.paidPrincipal + progress.remainingDebt).coerceAtLeast(1.0),
        )
        RepaymentProgressRow(
            leftValue = Formatters.money(progress.paidInterest),
            rightValue = Formatters.money(progress.remainingInterest),
            leftLabel = stringResource(R.string.analytics_interest_paid),
            rightLabel = stringResource(R.string.analytics_remaining_to_pay),
            paidFraction = progress.paidInterest /
                (progress.paidInterest + progress.remainingInterest).coerceAtLeast(1.0),
        )
    }
}

@Composable
private fun RepaymentProgressRow(
    leftValue: String,
    rightValue: String,
    leftLabel: String,
    rightLabel: String,
    paidFraction: Double,
) {
    val fraction = paidFraction.toFloat().coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = leftValue, fontWeight = FontWeight.SemiBold)
            Text(text = rightValue, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(2.5.dp)),
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .weight(fraction.coerceAtLeast(0.001f))
                        .fillMaxHeight()
                        .background(LoanGreen),
                )
            }
            if (fraction < 1f) {
                Box(
                    modifier = Modifier
                        .weight((1f - fraction).coerceAtLeast(0.001f))
                        .fillMaxHeight()
                        .background(LoanRed),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = leftLabel, color = LoanGreen, style = MaterialTheme.typography.bodySmall)
            Text(text = rightLabel, color = LoanRed, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun YearlyDebtLoadChart(
    yearlyLoad: List<YearlyDebtLoad>,
    modifier: Modifier = Modifier,
) {
    if (yearlyLoad.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val maxTotal = remember(yearlyLoad) {
        yearlyLoad.maxOf { it.total }.coerceAtLeast(1.0)
    }
    val barWidthDp = 28.dp
    val barGapDp = 14.dp
    val chartWidthDp = remember(yearlyLoad.size) {
        val barsWidth = yearlyLoad.size * barWidthDp.value + (yearlyLoad.size - 1).coerceAtLeast(0) * barGapDp.value
        maxOf(barsWidth + 24f, 280f).dp
    }
    val scrollState = rememberScrollState()
    val totalLabelStyle = TextStyle(fontSize = 8.sp, color = Color.Black, fontWeight = FontWeight.Medium)
    val yearStyle = TextStyle(fontSize = 9.sp, color = LoanTextSecondary)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
        ) {
            Canvas(
                modifier = Modifier
                    .width(chartWidthDp)
                    .height(280.dp)
                    .padding(horizontal = 8.dp),
            ) {
                val barWidth = barWidthDp.toPx()
                val barGap = barGapDp.toPx()
                val topLabelArea = 44f
                val bottomYearArea = 28f
                val chartHeight = (size.height - topLabelArea - bottomYearArea).coerceAtLeast(1f)
                val baseY = topLabelArea + chartHeight

                yearlyLoad.forEachIndexed { index, yearLoad ->
                    val x = index * (barWidth + barGap)
                    val principalHeight = (yearLoad.principal / maxTotal * chartHeight).toFloat()
                    val interestHeight = (yearLoad.interest / maxTotal * chartHeight).toFloat()
                    val totalHeight = principalHeight + interestHeight

                    if (principalHeight > 0f) {
                        drawRect(
                            color = LoanGreen,
                            topLeft = Offset(x, baseY - principalHeight),
                            size = Size(barWidth, principalHeight),
                        )
                    }
                    if (interestHeight > 0f) {
                        drawRect(
                            color = LoanRed,
                            topLeft = Offset(x, baseY - totalHeight),
                            size = Size(barWidth, interestHeight),
                        )
                    }

                    if (totalHeight > 0f) {
                        val totalLabel = Formatters.moneyWithoutDecimal(yearLoad.total)
                        val totalLayout = textMeasurer.measure(text = totalLabel, style = totalLabelStyle)
                        drawText(
                            textLayoutResult = totalLayout,
                            topLeft = Offset(
                                x + barWidth / 2f - totalLayout.size.width / 2f,
                                baseY - totalHeight - totalLayout.size.height - 4f,
                            ),
                        )
                    }

                    val yearLabel = yearLoad.year.toString()
                    val yearLayout = textMeasurer.measure(text = yearLabel, style = yearStyle)
                    drawText(
                        textLayoutResult = yearLayout,
                        topLeft = Offset(
                            x + barWidth / 2f - yearLayout.size.width / 2f,
                            baseY + 8f,
                        ),
                    )
                }
            }
        }

        AnalyticsLegend(
            items = listOf(
                LoanGreen to stringResource(R.string.analytics_principal_repayment),
                LoanRed to stringResource(R.string.analytics_interest_repayment),
            ),
        )
    }
}
