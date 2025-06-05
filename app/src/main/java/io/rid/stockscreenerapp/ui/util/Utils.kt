package io.rid.stockscreenerapp.ui.util

import android.content.Context
import android.util.Log
import com.opencsv.bean.CsvToBeanBuilder
import io.rid.stockscreenerapp.data.ListingStock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

suspend fun Context.saveCsv(responseBody: ResponseBody, directory: File, fileName: String): File? {
    return withContext(Dispatchers.IO) {
        try {
            if (!directory.exists()) directory.mkdirs()
            val file = File(directory, fileName)
            responseBody.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: IOException) {
            Log.e("CSV_SAVE", "Error saving CSV file", e)
            null
        }
    }
}


fun parseStockCsv(content: String): List<ListingStock> {
    val reader = content.reader()
    val csvToBean = CsvToBeanBuilder<ListingStock>(reader)
        .withType(ListingStock::class.java)
        .withIgnoreLeadingWhiteSpace(true)
        .build()

    val stocks = csvToBean.parse()
    csvToBean.capturedExceptions.forEach { exception ->
        Log.e("CSV_PARSE", "Parsing error: ${exception.message}", exception)
    }
    return stocks
}