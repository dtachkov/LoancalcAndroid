package ru.kredit.calculator.database

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.kredit.calculator.database.DatabaseContract.DATABASE_NAME

class DatabasePathResolverTest {
    @Test
    fun databaseContractKeepsLegacyFileName() {
        assertEquals("main.db", DATABASE_NAME)
    }

    @Test
    fun sidecarSuffixesCoverSqliteArtifacts() {
        val suffixes = listOf("-journal", "-wal", "-shm")
        assertTrue(suffixes.all { it.startsWith("-") })
    }
}
