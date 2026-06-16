package com.example.loancalcandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.loancalcandroid.billing.RuStorePayHelper
import com.example.loancalcandroid.navigation.LoanCalcNavGraph
import com.example.loancalcandroid.notification.NotificationActions
import com.example.loancalcandroid.ui.theme.LoanCalcAndroidTheme
import ru.kredit.calculator.data.LoanCalcData
import java.util.Date

class MainActivity : ComponentActivity() {
    private val pendingScheduleLoanId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RuStorePayHelper.proceedPaymentIntent(this, intent)
        handleNotificationIntent(intent)
        enableEdgeToEdge()
        setContent {
            LoanCalcAndroidTheme {
                LoanCalcNavGraph(
                    modifier = Modifier.fillMaxSize(),
                    pendingScheduleLoanId = pendingScheduleLoanId.value,
                    onPendingScheduleHandled = { pendingScheduleLoanId.value = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        RuStorePayHelper.proceedPaymentIntent(this, intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action != NotificationActions.SHOW_SCHEDULE) return
        val loanId = intent.getLongExtra(NotificationActions.EXTRA_LOAN_ID, 0L)
        val paymentDateMillis = intent.getLongExtra(NotificationActions.EXTRA_PAYMENT_DATE_MILLIS, 0L)
        if (loanId != 0L && paymentDateMillis != 0L) {
            LoanCalcData.get().shownNotificationsPreferences.setNotified(
                loanId = loanId,
                paymentDate = Date(paymentDateMillis),
            )
        }
        if (loanId != 0L) {
            pendingScheduleLoanId.value = loanId
        }
    }
}
