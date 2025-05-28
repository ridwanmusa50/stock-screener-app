package io.rid.stockscreenerapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SafeNavigation {
	val json = Json { ignoreUnknownKeys = true }

	// Serialization
	inline fun <reified T> encode(value: T): String = json.encodeToString(value)
	inline fun <reified T> decode(raw: String): T = json.decodeFromString(raw)

	// SavedStateHandle extensions
	object SavedState {
		inline fun <reified T> consume(handle: SavedStateHandle, key: String): T? {
			return handle.get<String>(key)?.let { value ->
				handle.remove<String>(key)
				decode(value)
			}
		}

		inline fun <reified T> set(handle: SavedStateHandle, key: String, value: T) {
			handle[key] = encode(value)
		}
	}

	// NavController extensions
	object Nav {
		@Composable
		inline fun <reified T> consume(navController: NavController, key: String): T? {
			val result = remember { mutableStateOf<T?>(null) }

			LaunchedEffect(key) {
				result.value = navController.currentBackStackEntry
					?.savedStateHandle
					?.let { SavedState.consume(it, key) }
			}

			return result.value
		}

		inline fun <reified T> set(navController: NavController, key: String, value: T) {
			navController.previousBackStackEntry
				?.savedStateHandle
				?.let { SavedState.set(it, key, value) }
		}
	}
}