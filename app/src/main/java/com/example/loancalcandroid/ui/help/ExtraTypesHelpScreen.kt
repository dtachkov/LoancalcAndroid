package com.example.loancalcandroid.ui.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.LoanCalcScaffold
import com.example.loancalcandroid.ui.theme.LoanTextSecondary
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.model.ExtraType

@Composable
fun ExtraTypesHelpScreen(onBack: () -> Unit) {
    LoanCalcScaffold(
        title = stringResource(R.string.label_extra_types_help),
        onBack = onBack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            val types = ExtraTypeUtils.earlyPaymentTypes + ExtraTypeUtils.commissionTypes
            types.forEachIndexed { index, type ->
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }
                Text(
                    text = ExtraTypeUtils.label(type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = ExtraTypeUtils.description(type),
                    style = MaterialTheme.typography.bodyMedium,
                    color = LoanTextSecondary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
