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

enum class ChooseBD { BY_ROOM, BY_CURSOR}

//data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

class PreferencesManager(
    private val context: Context
) {

    private val sharedPreferences:SharedPreferences by lazy { context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) }

//    val orderFlow: Flow<FilterPreferences> get() = sharedPreferences.getFilterPreferencesFlow()

//    @ExperimentalCoroutinesApi
//    val orderFlow2: Flow<String> get() = sharedPreferences.getIntFlow(SORT_KEY)



    fun updateKeySortOrder(sortOrder: SortOrder) {
        sharedPreferences.edit(true){
            Log.d("updateSortOrder", sortOrder.name)
            putString(SORT_KEY, sortOrder.name)
        }
    }

    fun updateKeyBD(chooseBD: ChooseBD) {
        sharedPreferences.edit(true){
            Log.d("updateSortOrder", chooseBD.name)
            putString(API_BD_KEY, chooseBD.name)
        }
    }

    fun getKeySort ():String {
        val let: String = sharedPreferences.getString(SORT_KEY, SortOrder.BY_DATE.name)!!
        Log.d("getKey", let)
        return let
    }

    fun getKeyBD ():String {
        val let: String = sharedPreferences.getString(API_BD_KEY, ChooseBD.BY_ROOM.name)!!
        Log.d("getKeyBD", let)
        return let
    }
}

private object PreferencesKeys{
    const val SORT_KEY = "sort_order"
    const val API_BD_KEY = "API_BD_KEY"
    const val PREFERENCES_NAME = "app_preferences"

}

//@ExperimentalCoroutinesApi
//fun SharedPreferences.getIntFlow(key: String, defaultValue: String = SortOrder.BY_DATE.name) = callbackFlow<String> {
//
//    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> getString(key, defaultValue)?.let {
//        offer(
//            it
//        )
//    } }
//
//    registerOnSharedPreferenceChangeListener(listener)
//
//    runCatching { getString(key, defaultValue)?.let { offer(it) } }
//
//    awaitClose {
//        unregisterOnSharedPreferenceChangeListener(listener)
//    }
//}
