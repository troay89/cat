package com.example.kotovskdatabase.ui.firstscreen

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.kotovskdatabase.ui.firstscreen.PreferencesKeys.PREFERENCES_NAME
import com.example.kotovskdatabase.ui.firstscreen.PreferencesKeys.SORT_KEY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

//private const val TAG = "PreferencesManager"

enum class SortOrder { BY_NAME, BY_AGE, BY_DATE }

//data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

class PreferencesManager(
    private val context: Context
) {

    private val sharedPreferences:SharedPreferences by lazy { context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) }

//    val orderFlow: Flow<FilterPreferences> get() = sharedPreferences.getFilterPreferencesFlow()

    @ExperimentalCoroutinesApi
    val orderFlow2: Flow<String> get() = sharedPreferences.getIntFlow(SORT_KEY)



    fun updateSortOrder(sortOrder: SortOrder) {
        sharedPreferences.edit(true){
            putString(SORT_KEY, sortOrder.name)
        }
    }

}

private object PreferencesKeys{
    const val SORT_KEY = "sort_order"
    const val PREFERENCES_NAME = "app_preferences"

}

@ExperimentalCoroutinesApi
fun SharedPreferences.getIntFlow(key: String, defaultValue: String = SortOrder.BY_DATE.name) = callbackFlow<String> {

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> getString(key, defaultValue)?.let {
        offer(
            it
        )
    } }

    registerOnSharedPreferenceChangeListener(listener)

    runCatching { getString(key, defaultValue)?.let { offer(it) } }

    awaitClose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}

//@ExperimentalCoroutinesApi
//fun SharedPreferences.getFilterPreferencesFlow(filterPreferences: FilterPreferences) = callbackFlow<FilterPreferences> {
//
//    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> offer(getInt(key, defaultValue)) }
//
//    registerOnSharedPreferenceChangeListener(listener)
//
////    runCatching { offer(getInt(key, defaultValue)) }
//
//    awaitClose {
//        unregisterOnSharedPreferenceChangeListener(listener)
//    }
//}