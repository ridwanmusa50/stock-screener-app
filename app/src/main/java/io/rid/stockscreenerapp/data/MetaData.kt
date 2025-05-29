package io.rid.stockscreenerapp.data

import com.google.gson.annotations.SerializedName

data class MetaData(
    @SerializedName("1. Information") val information: String,
    @SerializedName("2. Symbol") val symbol: String,
    @SerializedName("3. Last Refreshed") val lastRefreshed: String
)
