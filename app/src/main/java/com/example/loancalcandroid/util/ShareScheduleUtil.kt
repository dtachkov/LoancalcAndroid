package com.example.loancalcandroid.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.FileProvider
import com.example.loancalcandroid.BuildConfig
import com.example.loancalcandroid.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.data.calculation.ExtraTypeUtils
import ru.kredit.calculator.data.calculation.PaymentSummary
import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanType
import java.io.File
import java.io.IOException

object ShareScheduleUtil {

    suspend fun share(context: Context, loanId: Long) = withContext(Dispatchers.IO) {
        val data = LoanCalcData.get()
        val loan = data.loanRepository.getLoan(loanId)
            ?: throw IllegalStateException(context.getString(R.string.share_schedule_loan_not_found))

        val extras = data.extraRepository.getExtras(loanId)
        val calculation = data.loanCalculator.calculate(loan, extras)

        val html = buildHtml(context, loan, extras, calculation.payments)
        val file = saveHtml(context, loan.id, html)
            ?: throw IOException(context.getString(R.string.share_schedule_save_failed))

        withContext(Dispatchers.Main) {
            startShareActivity(context, loan, file)
        }
    }

    private fun startShareActivity(context: Context, loan: Loan, filePath: String) {
        val file = File(filePath)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                "${context.getString(R.string.share_mail_title)} ${loan.title.orEmpty()}",
            )
            val fileUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                file,
            )
            putExtra(Intent.EXTRA_STREAM, fileUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val resInfoList = context.packageManager.queryIntentActivities(
                this,
                PackageManager.MATCH_DEFAULT_ONLY,
            )
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(
                    packageName,
                    fileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
        }
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.menu_share)))
    }

    private fun saveHtml(context: Context, loanId: Long, html: String): String? {
        val folder = File(context.cacheDir, "share")
        if (!folder.exists() && !folder.mkdirs()) {
            return null
        }
        return try {
            val file = File(folder, "$loanId.html")
            file.writeText(html, Charsets.UTF_8)
            file.absolutePath
        } catch (_: IOException) {
            null
        }
    }

    private fun buildHtml(
        context: Context,
        loan: Loan,
        extras: List<Extra>,
        payments: List<PaymentSummary>,
    ): String {
        val extrasTable = buildExtrasTable(context, extras)
        val scheduleTable = buildScheduleTable(context, payments)
        val loanType = when (loan.type) {
            LoanType.ANNUITY -> context.getString(R.string.loan_type_annuity)
            LoanType.GRADE -> context.getString(R.string.loan_type_graded)
        }

        return buildString {
            append("<html><head><meta charset='utf-8'><title>")
            append(context.getString(R.string.share_mail_title))
            append(' ')
            append(loan.title.orEmpty())
            append("</title></head><body>")
            append("<p>").append(context.getString(R.string.share_mail_text1)).append("</p>")
            append("<p>").append(context.getString(R.string.share_mail_text2)).append("</p><br/><br/>")
            append("<p>")
            append(context.getString(R.string.share_label_loan_amount))
            append(Formatters.moneyFixed(loan.amount))
            append("</p>")
            append("<p>")
            append(context.getString(R.string.share_label_loan_rate))
            append(Formatters.schedulePercent(loan.rate.toDouble()))
            append("</p>")
            append("<p>")
            append(context.getString(R.string.share_label_loan_term))
            append(loan.term)
            append("</p>")
            append("<p>")
            append(context.getString(R.string.loan_type))
            append(": ")
            append(loanType)
            append("</p>")
            append("<p>")
            append(context.getString(R.string.share_label_first_payment_date))
            append(": ")
            append(Formatters.date(loan.firstPaymentDate))
            append("</p>")
            if (loan.dateOfIssue != null) {
                append("<p>")
                append(context.getString(R.string.loan_issue_date))
                append(": ")
                append(Formatters.date(loan.dateOfIssue))
                append("</p><br/>")
            }
            append("<p>").append(context.getString(R.string.share_mail_extras_title)).append("</p><br/><br/>")
            append(extrasTable)
            append("<br/><p>").append(context.getString(R.string.share_mail_schedule_title)).append("</p><br/><br/>")
            append(scheduleTable)
            append("<br/><br/><p>")
            append(context.getString(R.string.share_mail_footer))
            append(" <a href='")
            append(context.getString(R.string.share_mail_program_link))
            append("'>")
            append(context.getString(R.string.share_mail_program_name))
            append("</a></p>")
            append("</body></html>")
        }
    }

    private fun buildExtrasTable(context: Context, extras: List<Extra>): String {
        return buildString {
            append("<table border=1 cellpadding=8><tr><td>№</td><td>")
            append(context.getString(R.string.extra_type))
            append("</td><td>")
            append(context.getString(R.string.extra_date))
            append("</td><td>")
            append(context.getString(R.string.extra_document_number))
            append("</td><td>")
            append(context.getString(R.string.share_col_amount_or_rate))
            append("</td></tr>")

            extras.forEachIndexed { index, extra ->
                val amount = if (extra.type == ExtraType.CHANGE_RATE) {
                    Formatters.schedulePercent(extra.amount.toDouble())
                } else {
                    Formatters.moneyFixed(extra.amount)
                }
                append("<tr><td>")
                append(index + 1)
                append("</td><td>")
                append(ExtraTypeUtils.label(extra.type))
                append("</td><td>")
                append(Formatters.date(extra.date))
                append("</td><td>")
                append(extra.documentNumber.orEmpty())
                append("</td><td>")
                append(amount)
                append("</td></tr>")
            }
            append("</table>")
        }
    }

    private fun buildScheduleTable(context: Context, payments: List<PaymentSummary>): String {
        return buildString {
            append("<table border=1 cellpadding=8><tr><td>№</td><td>")
            append(context.getString(R.string.schedule_col_date))
            append("</td><td>")
            append(context.getString(R.string.schedule_col_payment))
            append("</td><td>")
            append(context.getString(R.string.schedule_col_principal))
            append("</td><td>")
            append(context.getString(R.string.schedule_col_interest))
            append("</td><td>")
            append(context.getString(R.string.schedule_col_balance))
            append("</td><td>")
            append(context.getString(R.string.schedule_col_extras))
            append("</td></tr>")

            payments.forEachIndexed { index, payment ->
                append("<tr><td>")
                append(index + 1)
                append("</td><td>")
                append(Formatters.date(payment.date))
                append("</td><td>")
                append(Formatters.moneyFixed(payment.total))
                append("</td><td>")
                append(Formatters.moneyFixed(payment.principal))
                append("</td><td>")
                append(Formatters.moneyFixed(payment.interest))
                append("</td><td>")
                append(Formatters.moneyFixed(payment.endBalance))
                append("</td><td>")
                append(formatPaymentExtras(payment))
                append("</td></tr>")
            }
            append("</table>")
        }
    }

    private fun formatPaymentExtras(payment: PaymentSummary): String {
        val parts = mutableListOf<String>()
        if (payment.rateExtra > 0.001) {
            parts += Formatters.schedulePercent(payment.rateExtra)
        }
        if (payment.extras > 0.001) {
            parts += Formatters.moneyFixed(payment.extras)
        }
        return parts.joinToString(", ")
    }
}
