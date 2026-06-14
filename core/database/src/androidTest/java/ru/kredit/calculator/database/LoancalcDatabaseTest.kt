package ru.kredit.calculator.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.kredit.calculator.database.entity.ExtraEntity
import ru.kredit.calculator.database.entity.LoanDetailsEntity
import ru.kredit.calculator.database.entity.LoanEntity
import ru.kredit.calculator.database.entity.OfferEntity

@RunWith(AndroidJUnit4::class)
class LoancalcDatabaseTest {
    private lateinit var database: LoancalcDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LoancalcDatabase::class.java)
            .addMigrations(*DatabaseMigrations.ALL)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun databaseNameAndVersionMatchLegacyContract() {
        assertEquals("main.db", DatabaseContract.DATABASE_NAME)
        assertEquals(8, DatabaseContract.DATABASE_VERSION)
    }

    @Test
    fun loanExtraAndOfferRoundTrip() = runBlocking {
        val loanId = database.loanDao().insert(
            LoanEntity(
                creationDate = "2026-06-14",
                title = "14-06-2026/1",
                amount = 1_000_000f,
                rate = 12.5f,
                term = 36,
                type = 0,
                firstPaymentDate = "2026-07-14",
                dateOfIssue = "2026-06-14",
                monthlyPayment = 0f,
                considerDaysOff = 0,
                payOnLastDayOfMonth = 0,
                applyExtrasImmediately = 0,
                calculateExtrasByTermLikeSberbank = 1,
                ignorePassedPeriodsAfterRateChange = 0,
                extraDayInMonth = 0,
                isForecastActive = 0,
            )
        )

        val extraId = database.extraDao().insert(
            ExtraEntity(
                documentNumber = "1",
                type = 0,
                date = "2026-08-14",
                amount = 50_000f,
                loanId = loanId,
            )
        )

        database.offerDao().replaceAll(
            listOf(
                OfferEntity(
                    name = "Offer",
                    orgName = "Bank",
                    term = 60,
                    rateType = "\"FIXED\"",
                    rate = "{\"value\":11.9}",
                    logoColor = "#FFFFFF",
                    logoImage = "logo.png",
                    link = "https://example.com",
                    amountLimit = 3_000_000f,
                    requirements = "req",
                    extraPaymentRules = "rules",
                    docs = "docs",
                )
            )
        )

        database.loanDetailsDao().upsert(
            LoanDetailsEntity(
                loanId = loanId,
                bankName = "Сбербанк",
                accountNumber = "42302810000000001234",
                uic = "44552233",
                correspondentAccount = "30101810400000000225",
                paymentComment = "Ежемесячный платеж по кредиту",
            )
        )

        val savedLoan = database.loanDao().getById(loanId)
        val savedExtra = database.extraDao().getByLoanAndExtraId(loanId, extraId)
        val offers = database.offerDao().getAll()
        val savedDetails = database.loanDetailsDao().getByLoanId(loanId)

        assertNotNull(savedLoan)
        assertEquals("14-06-2026/1", savedLoan?.title)
        assertNotNull(savedExtra)
        assertEquals(loanId, savedExtra?.loanId)
        assertEquals(1, offers.size)
        assertEquals("Bank", offers.first().orgName)
        assertNotNull(savedDetails)
        assertEquals("Сбербанк", savedDetails?.bankName)
        assertEquals("42302810000000001234", savedDetails?.accountNumber)
    }
}
