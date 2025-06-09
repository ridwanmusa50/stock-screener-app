package io.rid.stockscreenerapp.dataStore

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

open class PrefsStore @Inject constructor(@ApplicationContext context: Context) {

	companion object {
		val LAST_STARRED_STOCK_MONTHLY_STOCK_LOADED = longPreferencesKey("LAST_STARRED_STOCK_MONTHLY_STOCK_LOADED")
		private val Context.dataStore by preferencesDataStore("DATA_STORE")
	}

	private val dataStore = context.dataStore
	val lastStarredStockMonthlyStockLoaded = dataStore.data.map { it[LAST_STARRED_STOCK_MONTHLY_STOCK_LOADED] }

	suspend fun <T> writeSingleDataToDataStore(key: Preferences.Key<T>, value: T) {
		dataStore.edit { it[key] = value }
	}

	suspend fun <T> writeMultipleDataToDataStore(callback: (MutablePreferences) -> Unit) {
		dataStore.edit { callback(it) }
	}

}