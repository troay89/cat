package com.example.kotovskdatabase.ui.firstscreen

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.kotovskdatabase.ui.firstscreen.PreferencesKeys.API_BD_KEY
import com.example.kotovskdatabase.ui.firstscreen.PreferencesKeys.PREFERENCES_NAME
import com.example.kotovskdatabase.ui.firstscreen.PreferencesKeys.SORT_KEY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


enum class SortOrder { BY_NAME, BY_AGE, BY_DATE }

enum class ChooseBD { BY_ROOM, BY_CURSOR }

data class FilterPreferences(val sortOrder: SortOrder, val chooseBD: ChooseBD)

class PreferencesManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    @ExperimentalCoroutinesApi
    val orderFlow2: Flow<FilterPreferences>
        get() = sharedPreferences.getStringFlow(SORT_KEY)


    fun updateSortOrder(sortOrder: SortOrder) {
        sharedPreferences.edit(true) {
            putString(SORT_KEY, sortOrder.name)
        }
    }

    fun updateKeyBD(chooseBD: ChooseBD) {
        sharedPreferences.edit(true) {
            putString(API_BD_KEY, chooseBD.name)
        }
    }

    fun getKeyBD() = sharedPreferences.getString(API_BD_KEY, ChooseBD.BY_ROOM.name)!!


    fun getKeySort() = sharedPreferences.getString(SORT_KEY, ChooseBD.BY_ROOM.name)!!


}

private object PreferencesKeys {
    const val SORT_KEY = "sort_order"
    const val PREFERENCES_NAME = "app_preferences"
    const val API_BD_KEY = "API_BD_KEY"
}

lateinit var sort: SortOrder
lateinit var api: ChooseBD

@ExperimentalCoroutinesApi
fun SharedPreferences.getStringFlow(key: String, defaultValue: String = SortOrder.BY_DATE.name) =
    callbackFlow<FilterPreferences> {

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            getString(key, defaultValue)?.let {

                if (key == SORT_KEY){
                    sort = SortOrder.valueOf(it)
                }else {
                    Log.d("getStringFlow1", it)
                    api = ChooseBD.valueOf(it)
                }

                val filterPreferences = FilterPreferences(sort, api)
                offer(filterPreferences)
            }
        }

        registerOnSharedPreferenceChangeListener(listener)

        runCatching {
            getString(key, defaultValue)?.let {
                if (key == SORT_KEY){
                    sort = SortOrder.valueOf(it)
                }else {
                    api = ChooseBD.valueOf(it)
                }
                val filterPreferences = FilterPreferences(sort, api)
                offer(filterPreferences)
            }
        }

        awaitClose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
