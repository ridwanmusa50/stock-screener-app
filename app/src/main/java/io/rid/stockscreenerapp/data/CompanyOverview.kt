package io.rid.stockscreenerapp.data

import com.google.gson.annotations.SerializedName

data class CompanyOverview(
    @SerializedName("net_assets") val netAssets: String = "",
    @SerializedName("net_expense_ratio") val netExpenseRatio: String = "",
    @SerializedName("portfolio_turnover") val portfolioTurnover: String = "",
    @SerializedName("dividend_yield") val dividendYield: String = "",
    @SerializedName("inception_date") val inceptionDate: String = "",
    @SerializedName("leveraged") val leveraged: String = "",
    @SerializedName("sectors") val sectors: List<Sector> = emptyList(),
    @SerializedName("holdings") val holdings: List<Holding> = emptyList()
)

data class Sector(
    @SerializedName("sector") val sector: String,
    @SerializedName("weight") val weight: String
)

data class Holding(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("description") val description: String,
    @SerializedName("weight") val weight: String
)
