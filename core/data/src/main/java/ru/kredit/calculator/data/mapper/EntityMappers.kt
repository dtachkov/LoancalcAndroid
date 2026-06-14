package ru.kredit.calculator.data.mapper

import ru.kredit.calculator.data.model.Extra
import ru.kredit.calculator.data.model.ExtraType
import ru.kredit.calculator.data.model.Loan
import ru.kredit.calculator.data.model.LoanDetails
import ru.kredit.calculator.data.model.LoanType
import ru.kredit.calculator.data.model.Offer
import ru.kredit.calculator.data.util.DateFormats
import ru.kredit.calculator.database.entity.ExtraEntity
import ru.kredit.calculator.database.entity.LoanDetailsEntity
import ru.kredit.calculator.database.entity.LoanEntity
import ru.kredit.calculator.database.entity.OfferEntity

private fun Long.toEntityId(): Long? = takeIf { it != 0L }

fun LoanEntity.toDomain(): Loan {
    return Loan(
        id = id ?: 0,
        creationDate = DateFormats.parseDate(creationDate) ?: DateFormats.clearTime(java.util.Date()),
        title = title,
        amount = amount ?: 0f,
        rate = rate ?: 0f,
        term = term ?: 0,
        type = type?.let(LoanType::fromInt) ?: LoanType.ANNUITY,
        firstPaymentDate = DateFormats.parseDate(firstPaymentDate),
        monthlyPayment = monthlyPayment ?: 0f,
        dateOfIssue = DateFormats.parseDate(dateOfIssue),
        considerDaysOff = considerDaysOff == 1,
        payOnLastDayOfMonth = payOnLastDayOfMonth == 1,
        applyExtrasImmediately = applyExtrasImmediately == 1,
        calculateExtrasByBalanceLikeSberbank = calculateExtrasByTermLikeSberbank != 0,
        ignorePassedPeriodsAfterRateChange = ignorePassedPeriodsAfterRateChange == 1,
        extraDayInMonth = extraDayInMonth == 1,
        isForecastActive = isForecastActive == 1,
        forecastMonthlyPayment = forecastMonthlyPay ?: 0f,
        forecastDaysBefore = forecastDaysBefore ?: 0,
        forecastStartDate = DateFormats.parseDate(forecastStartDate),
        forecastExtraType = forecastExtraType?.let(ExtraType::fromInt)
            ?: ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
    )
}

fun Loan.toEntity(): LoanEntity {
    return LoanEntity(
        id = id.toEntityId(),
        creationDate = DateFormats.formatDate(creationDate),
        title = title,
        amount = amount,
        rate = rate,
        term = term,
        type = type.toInt(),
        firstPaymentDate = DateFormats.formatDate(firstPaymentDate),
        dateOfIssue = DateFormats.formatDate(dateOfIssue),
        monthlyPayment = monthlyPayment,
        considerDaysOff = if (considerDaysOff) 1 else 0,
        payOnLastDayOfMonth = if (payOnLastDayOfMonth) 1 else 0,
        applyExtrasImmediately = if (applyExtrasImmediately) 1 else 0,
        calculateExtrasByTermLikeSberbank = if (calculateExtrasByBalanceLikeSberbank) 1 else 0,
        ignorePassedPeriodsAfterRateChange = if (ignorePassedPeriodsAfterRateChange) 1 else 0,
        extraDayInMonth = if (extraDayInMonth) 1 else 0,
        isForecastActive = if (isForecastActive) 1 else 0,
        forecastMonthlyPay = if (isForecastActive) forecastMonthlyPayment else null,
        forecastDaysBefore = if (isForecastActive) forecastDaysBefore else null,
        forecastStartDate = if (isForecastActive) DateFormats.formatDate(forecastStartDate) else null,
        forecastExtraType = if (isForecastActive) forecastExtraType.toInt() else null,
    )
}

fun LoanDetailsEntity.toDomain(): LoanDetails {
    return LoanDetails(
        bankName = bankName,
        accountNumber = accountNumber,
        uic = uic,
        correspondentAccount = correspondentAccount,
        paymentComment = paymentComment,
    )
}

fun LoanDetails.toEntity(loanId: Long): LoanDetailsEntity {
    return LoanDetailsEntity(
        loanId = loanId,
        bankName = bankName,
        accountNumber = accountNumber,
        uic = uic,
        correspondentAccount = correspondentAccount,
        paymentComment = paymentComment?.takeIf { it.isNotBlank() },
    )
}

fun ExtraEntity.toDomain(): Extra {
    return Extra(
        id = id ?: 0,
        amount = amount ?: 0f,
        documentNumber = documentNumber,
        type = type?.let(ExtraType::fromInt) ?: ExtraType.PAYMENT_FOR_DECREASE_LOAN_AMOUNT,
        date = DateFormats.parseDate(date),
        loanId = loanId ?: 0,
    )
}

fun Extra.toEntity(): ExtraEntity {
    return ExtraEntity(
        id = id.toEntityId(),
        amount = amount,
        documentNumber = documentNumber,
        type = type.toInt(),
        date = DateFormats.formatDate(date),
        loanId = loanId,
    )
}

fun OfferEntity.toDomain(): Offer {
    val rateType = Offer.decodeRateType(rateType)
    return Offer(
        id = id ?: 0,
        name = name,
        organizationName = orgName,
        documents = docs,
        requirements = requirements,
        extraPaymentRules = extraPaymentRules,
        limit = amountLimit?.toDouble() ?: 0.0,
        term = term ?: 0,
        link = link,
        logoImage = logoImage,
        logoColor = logoColor,
        rate = Offer.decodeRate(rateType, rate),
    )
}

fun Offer.toEntity(): OfferEntity {
    val rateType = Offer.rateTypeOf(rate)
    return OfferEntity(
        id = id.toEntityId(),
        name = name,
        orgName = organizationName,
        docs = documents,
        requirements = requirements,
        extraPaymentRules = extraPaymentRules,
        amountLimit = limit.toFloat(),
        term = term,
        link = link,
        logoImage = logoImage,
        logoColor = logoColor,
        rateType = Offer.encodeRateType(rateType),
        rate = Offer.encodeRate(rate),
    )
}
