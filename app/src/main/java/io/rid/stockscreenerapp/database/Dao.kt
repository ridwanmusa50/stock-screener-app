package io.rid.stockscreenerapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.rid.stockscreenerapp.data.Stock

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStocks(questions: List<Stock>)

    @Query("SELECT * FROM stock")
    suspend fun getStocks(): List<Stock>

    @Query("UPDATE stock SET currentPrice = :currentPrice, percentageChanges = :percentageChanges, " +
            "isStarred = :isStarred WHERE symbol = :symbol")
    suspend fun updateStarredStock(symbol: String, currentPrice: String?, percentageChanges: String?, isStarred: Boolean)

}