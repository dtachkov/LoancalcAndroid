package ru.kredit.calculator.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.kredit.calculator.data.mapper.toDomain
import ru.kredit.calculator.data.mapper.toEntity
import ru.kredit.calculator.data.model.Offer

class OfferRepository(
    private val offerDao: ru.kredit.calculator.database.dao.OfferDao,
    private val ioDispatcher: CoroutineDispatcher,
) {

    fun observeOffers(): Flow<List<Offer>> {
        return offerDao.observeAll().map { offers -> offers.map { it.toDomain() } }
    }

    suspend fun getOffers(): List<Offer> = withContext(ioDispatcher) {
        offerDao.getAll().map { it.toDomain() }
    }

    suspend fun replaceOffers(offers: List<Offer>) = withContext(ioDispatcher) {
        offerDao.replaceAll(offers.map { it.toEntity() })
    }
}
