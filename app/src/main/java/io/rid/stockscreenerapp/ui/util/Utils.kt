package io.rid.stockscreenerapp.ui.util

import android.util.Log
import com.opencsv.bean.CsvToBeanBuilder
import io.rid.stockscreenerapp.data.ListingStock

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