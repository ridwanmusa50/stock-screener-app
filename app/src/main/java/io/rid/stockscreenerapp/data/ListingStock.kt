package io.rid.stockscreenerapp.data

data class ListingStock(
    val symbol: String,
    val name: String,
    val exchange: String,
    val assetType: String,
    val ipoDate: String?,
    val delistingDate: String?,
    val status: String
)
