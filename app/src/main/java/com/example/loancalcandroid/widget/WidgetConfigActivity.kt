package com.example.loancalcandroid.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.theme.LoanCalcAndroidTheme
import ru.kredit.calculator.data.model.Loan

class WidgetConfigActivity : ComponentActivity() {
    private var widgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultConfirmed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setResult(RESULT_CANCELED, resultIntent())
        enableEdgeToEdge()
        setContent {
            LoanCalcAndroidTheme {
                WidgetConfigScreen(
                    onLoanSelected = ::onLoanSelected,
                )
            }
        }
    }

    override fun onDestroy() {
        if (!resultConfirmed) {
            WidgetUpdater.updateWidget(this, widgetId)
        }
        super.onDestroy()
    }

    private fun onLoanSelected(loanId: Long) {
        ru.kredit.calculator.data.preferences.WidgetPreferences(this).setLoanId(widgetId, loanId)
        WidgetUpdater.updateWidget(this, widgetId)
        resultConfirmed = true
        setResult(RESULT_OK, resultIntent())
        finish()
    }

    private fun resultIntent(): Intent {
        return Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetConfigScreen(
    onLoanSelected: (Long) -> Unit,
    viewModel: WidgetConfigViewModel = viewModel(),
) {
    val loans by viewModel.loans.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.widget_config_title)) },
            )
        },
    ) { innerPadding ->
        if (loans.isEmpty()) {
            Text(
                text = stringResource(R.string.widget_config_no_loans),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                items(loans, key = { it.id }) { loan ->
                    WidgetLoanRow(
                        loan = loan,
                        onClick = { onLoanSelected(loan.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun WidgetLoanRow(
    loan: Loan,
    onClick: () -> Unit,
) {
    Text(
        text = loan.title.orEmpty(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}
