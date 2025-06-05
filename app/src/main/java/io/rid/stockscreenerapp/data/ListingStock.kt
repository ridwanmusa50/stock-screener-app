package io.rid.stockscreenerapp.data

import com.opencsv.bean.CsvBindByName

data class ListingStock(
    @CsvBindByName(column = "symbol") val symbol: String = "",
    @CsvBindByName(column = "name") val name: String = "",
    @CsvBindByName(column = "exchange")  val exchange: String = "",
    @CsvBindByName(column = "assetType")  val assetType: String = "",
    @CsvBindByName(column = "ipoDate") val ipoDate: String? = null,
    @CsvBindByName(column = "delistingDate")  val delistingDate: String? = null,
    @CsvBindByName(column = "status") val status: String = "",
    val isStarred: Boolean = false
)