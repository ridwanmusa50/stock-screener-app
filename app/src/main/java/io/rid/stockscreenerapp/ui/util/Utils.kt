package io.rid.stockscreenerapp.ui.util

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import com.opencsv.bean.CsvToBeanBuilder
import io.rid.stockscreenerapp.R
import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.data.ListingStock
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object Utils {

    fun readCsvFromRaw(context: Context, resId: Int): String {
        return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
    }

    // To extract data from CSV file
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

    fun getResponseErrMsg(
        context: Context,
        err: ApiResponse.Err,
        @StringRes defaultErrTextRes: Int? = null
    ): CharSequence {
        return when (err) {
            is ApiResponse.ServerUnavailableErr -> context.getString(R.string.server_service_unavailable)
            else -> context.getString(defaultErrTextRes ?: R.string.server_generic_server_error)
        }
    }

    fun formatWithThousandSeparator(
        value: Float,
        fractionDigits: Int = 2
    ): String {
        // Build format string dynamically, e.g. "#,##0.00" for 2 decimals
        var formatStr = "#,##0"
        if (fractionDigits > 0) {
            formatStr += "."
            repeat(fractionDigits) {
                formatStr += "0"
            }
        }

        // Create formatter with locale (for comma or dot)
        val decimalFormat = DecimalFormat(formatStr, DecimalFormatSymbols(Locale.US))
        return decimalFormat.format(value)
    }

}