package ru.kredit.calculator.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import ru.kredit.calculator.data.calculation.FeatureCalculators
import ru.kredit.calculator.data.calculation.LoanCalculator
import ru.kredit.calculator.data.network.OffersApiFactory
import ru.kredit.calculator.data.preferences.ChestPreferences
import ru.kredit.calculator.data.preferences.SettingsPreferences
import ru.kredit.calculator.data.preferences.ShownNotificationsPreferences
import ru.kredit.calculator.data.repository.ExtraRepository
import ru.kredit.calculator.data.repository.ImportExportRepository
import ru.kredit.calculator.data.repository.LoanRepository
import ru.kredit.calculator.data.repository.OfferRepository
import ru.kredit.calculator.database.LoancalcDatabase

class LoanCalcData private constructor(
    val database: LoancalcDatabase,
    val loanRepository: LoanRepository,
    val extraRepository: ExtraRepository,
    val offerRepository: OfferRepository,
    val importExportRepository: ImportExportRepository,
    val settingsPreferences: SettingsPreferences,
    val chestPreferences: ChestPreferences,
    val shownNotificationsPreferences: ShownNotificationsPreferences,
    val loanCalculator: LoanCalculator,
    val featureCalculators: FeatureCalculators,
) {
    companion object {
        @Volatile
        private var instance: LoanCalcData? = null

        fun initialize(
            context: Context,
            buildType: String,
        ): LoanCalcData {
            return instance ?: synchronized(this) {
                instance ?: create(context.applicationContext, buildType).also { instance = it }
            }
        }

        fun get(): LoanCalcData {
            return instance ?: error("LoanCalcData is not initialized. Call initialize() in Application.onCreate().")
        }

        private fun create(context: Context, buildType: String): LoanCalcData {
            val database = LoancalcDatabase.getInstance(context)
            val ioDispatcher = Dispatchers.IO
            val loanRepository = LoanRepository(
                loanDao = database.loanDao(),
                extraDao = database.extraDao(),
                ioDispatcher = ioDispatcher,
            )
            val extraRepository = ExtraRepository(
                extraDao = database.extraDao(),
                ioDispatcher = ioDispatcher,
            )
            val offerRepository = OfferRepository(
                offerDao = database.offerDao(),
                offersApi = OffersApiFactory.create(),
                ioDispatcher = ioDispatcher,
            )
            val importExportRepository = ImportExportRepository(
                loanRepository = loanRepository,
                extraRepository = extraRepository,
                ioDispatcher = ioDispatcher,
            )

            val loanCalculator = LoanCalculator()

            return LoanCalcData(
                database = database,
                loanRepository = loanRepository,
                extraRepository = extraRepository,
                offerRepository = offerRepository,
                importExportRepository = importExportRepository,
                settingsPreferences = SettingsPreferences(context),
                chestPreferences = ChestPreferences(context, buildType),
                shownNotificationsPreferences = ShownNotificationsPreferences(context),
                loanCalculator = loanCalculator,
                featureCalculators = FeatureCalculators(loanCalculator),
            )
        }
    }
}
