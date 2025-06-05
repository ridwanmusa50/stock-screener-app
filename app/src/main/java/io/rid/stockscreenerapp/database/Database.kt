package io.rid.stockscreenerapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.rid.stockscreenerapp.data.Stock

@Database(version = 1, entities = [Stock::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun dao(): Dao

}