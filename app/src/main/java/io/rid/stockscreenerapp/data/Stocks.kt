package io.rid.stockscreenerapp.data

data class Stocks(
    val symbol: String,
    val name: String,
    val isStarred: Boolean = false
)