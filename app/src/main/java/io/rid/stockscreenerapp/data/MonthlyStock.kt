package io.rid.stockscreenerapp.data

import com.google.gson.annotations.SerializedName

data class MonthlyStock(
    @SerializedName("Meta Data") val metaData: MetaData = MetaData(),
    @SerializedName("Monthly Time Series") val monthlyTimeSeries: Map<String, TimeSeriesData> = emptyMap()
)

data class MetaData(
    @SerializedName("1. Information") val information: String = "",
    @SerializedName("2. Symbol") val symbol: String = "",
    @SerializedName("3. Last Refreshed") val lastRefreshed: String = ""
)

data class TimeSeriesData(
    @SerializedName("1. open") val open: String = "",
    @SerializedName("2. high") val high: String = "",
    @SerializedName("3. low") val low: String = "",
    @SerializedName("4. close") val close: String = "",
    @SerializedName("5. volume") val volume: String = ""
)