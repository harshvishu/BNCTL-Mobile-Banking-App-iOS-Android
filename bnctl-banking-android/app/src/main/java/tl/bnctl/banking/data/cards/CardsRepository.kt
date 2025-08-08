package tl.bnctl.banking.data.cards

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.StatementsDataHolder
import tl.bnctl.banking.data.cards.model.Card
import tl.bnctl.banking.data.cards.model.CardProduct
import tl.bnctl.banking.data.cards.model.CreditCardStatement
import java.io.InputStream

class CardsRepository(private val dataSource: CardsDataSource) {

    var cards: List<Card>? = null
        private set

    var cardProducts: Result<List<CardProduct>>? = null
        private set

    init {
        cards = null
        cardProducts = null
    }


    suspend fun cardFetch(): Result<List<Card>> {
        val result = dataSource.cardFetch()
        if (result is Result.Success) {
            cards = result.data
        }
        return result
    }

    suspend fun fetchCardProducts(): Result<List<CardProduct>> {
        if (cardProducts == null) {
            cardProducts = dataSource.fetchCardProducts()
        }
        return cardProducts!!
    }

    suspend fun fetchCreditCardStatement(
        cardNumber: String,
        fromDate: String,
        toDate: String
    ): Result<List<CreditCardStatement>> {
        return dataSource.fetchCreditCardStatements(cardNumber, fromDate, toDate)
    }

    suspend fun fetchCardStatement(
        cardNumber: String,
        fromDate: String,
        toDate: String
    ): Result<List<StatementsDataHolder>> {
        return dataSource.fetchCardStatement(cardNumber, fromDate, toDate)
    }

    suspend fun downloadCreditCardStatement(
        fileName: String,
    ): Result<InputStream> {
        return dataSource.downloadCreditCardStatement(fileName)
    }
}