package com.example.loancalcandroid

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.DatabasePathResolver
import java.io.File

class LoanCalcApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        val data = LoanCalcData.initialize(
            context = this,
            buildType = BuildConfig.BUILD_TYPE,
        )
        applicationScope.launch {
            data.offerRepository.refreshOffers(data.settingsPreferences.getLanguageCode())
        }
    }

    override fun getDatabasePath(name: String): File {
        if (name == DatabaseContract.DATABASE_NAME) {
            val externalDatabaseDir = DatabasePathResolver.getExternalDatabaseDir(this)
            if (externalDatabaseDir != null) {
                return File(externalDatabaseDir, name)
            }
        }
        return super.getDatabasePath(name)
    }
}
