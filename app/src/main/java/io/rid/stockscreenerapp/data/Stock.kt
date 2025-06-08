package io.rid.stockscreenerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Stock(
    @PrimaryKey
    val symbol: String,
    val name: String,
    var isStarred: Boolean = false
)