package io.rid.stockscreenerapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun saveStringList(value: String?): ArrayList<String?> {
        return Gson().fromJson(value, object : TypeToken<ArrayList<String?>>() {}.type)
    }

    @TypeConverter
    fun getStringList(list: ArrayList<String?>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun saveIntList(list: List<Int>): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun getIntList(list: String): List<Int> {
        return Gson().fromJson(list, object : TypeToken<List<Int>>() {}.type)
    }

}