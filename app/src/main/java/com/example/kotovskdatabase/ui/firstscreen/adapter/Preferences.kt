package com.example.kotovskdatabase.ui.firstscreen.adapter

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class Preferences(
    private val context: Context
) {

    private val sharedPreferences by lazy { context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE) }

    val orderFlow: Flow<Int> get() = sharedPreferences.getIntFlow(ORDER_KEY, DEFAULT_ORDER)

    var order: Int
        get() = sharedPreferences.getInt(ORDER_KEY, DEFAULT_ORDER)
        set(value) = sharedPreferences.edit { putInt(ORDER_KEY, value) }


    companion object {
        private const val PREFERENCES_NAME = "app_preferences"
        private const val ORDER_KEY = "order"
        private const val DEFAULT_ORDER = 0
    }
}

@ExperimentalCoroutinesApi
fun SharedPreferences.getIntFlow(key: String, defaultValue: Int = 0) = callbackFlow<Int> {

    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> offer(getInt(key, defaultValue)) }

    registerOnSharedPreferenceChangeListener(listener)

    runCatching { offer(getInt(key, defaultValue)) }

    awaitClose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}
