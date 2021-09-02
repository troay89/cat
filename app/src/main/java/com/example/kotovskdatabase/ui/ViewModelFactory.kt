package com.example.kotovskdatabase.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.kotovskdatabase.App
import com.example.kotovskdatabase.ui.firstscreen.CatListViewModel
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val app: App,
) : AbstractSavedStateViewModelFactory(owner, null){

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {
        val viewModel = when (modelClass) {
//            CatViewModel::class.java -> {
//                CatViewModel(app.cursorDataBase, handle)
//            }
            CatListViewModel::class.java -> {
                CatListViewModel(app.preferencesManager)
            }

            else -> throw IllegalStateException("Неизвестный класс модели")
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(this, requireContext().applicationContext as App)