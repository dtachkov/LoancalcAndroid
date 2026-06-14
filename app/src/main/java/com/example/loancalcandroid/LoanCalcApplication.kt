package com.example.loancalcandroid

import android.app.Application
import ru.kredit.calculator.data.LoanCalcData
import ru.kredit.calculator.database.DatabaseContract
import ru.kredit.calculator.database.DatabasePathResolver
import java.io.File

class LoanCalcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LoanCalcData.initialize(
            context = this,
            buildType = BuildConfig.BUILD_TYPE,
        )
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
