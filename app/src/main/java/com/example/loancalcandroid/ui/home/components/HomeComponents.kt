package com.example.loancalcandroid.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.AutoShrinkText
import com.example.loancalcandroid.util.copyMetricValueToClipboard
import com.example.loancalcandroid.ui.theme.LoanBlueDark
import com.example.loancalcandroid.ui.theme.LoanBlueEnd
import com.example.loancalcandroid.ui.theme.LoanBlueStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onSettingsClick: () -> Unit,
    onAddLoanClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.home_title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings),
                )
            }
        },
        actions = {
            IconButton(onClick = onAddLoanClick) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = LoanBlueDark,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_loan),
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
fun LoanGradientCard(
    modifier: Modifier = Modifier,
    gradientStart: Color = LoanBlueStart,
    gradientEnd: Color = LoanBlueEnd,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(168.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(gradientStart, gradientEnd),
                ),
            )
            .padding(20.dp),
    ) {
        content()
    }
}

@Composable
fun LoanCardStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    shrinkLabel: Boolean = false,
) {
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        color = Color.White.copy(alpha = 0.85f),
    )
    val valueStyle = MaterialTheme.typography.titleMedium.copy(
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
    )
    val context = LocalContext.current

    Column(modifier = modifier) {
        if (shrinkLabel) {
            AutoShrinkText(
                text = label,
                style = labelStyle,
                minFontSize = 8.sp,
                maxLines = 1,
            )
        } else {
            AutoShrinkText(
                text = label,
                style = labelStyle,
                minFontSize = 9.sp,
                maxLines = 1,
            )
        }
        AutoShrinkText(
            text = value,
            modifier = Modifier.clickable { copyMetricValueToClipboard(context, value) },
            style = valueStyle,
            minFontSize = 11.sp,
            maxLines = 1,
        )
    }
}

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == currentPage) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) LoanBlueDark else Color.LightGray,
                    ),
            )
        }
    }
}

@Composable
fun SectionDividerSpacer() {
    Spacer(modifier = Modifier.height(8.dp))
}
