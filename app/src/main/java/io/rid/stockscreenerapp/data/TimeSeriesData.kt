package io.rid.stockscreenerapp.data

import com.google.gson.annotations.SerializedName

data class TimeSeriesData(
    @SerializedName("1. open") val open: String,
    @SerializedName("2. high") val high: String,
    @SerializedName("3. low") val low: String,
    @SerializedName("4. close") val close: String,
    @SerializedName("5. volume") val volume: String
)
