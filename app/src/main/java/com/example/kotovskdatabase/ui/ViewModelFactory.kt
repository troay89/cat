package com.example.kotovskdatabase.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotovskdatabase.App
import com.example.kotovskdatabase.ui.firstscreen.CatListViewModel
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel
import java.lang.IllegalStateException

class ViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = when (modelClass) {
            CatViewModel::class.java -> {
                CatViewModel(app.state
//                    , app.repository
                )
            }
            CatListViewModel::class.java -> {
                CatListViewModel(app.repository, app.preferences)
            }

            else -> throw IllegalStateException("Неизвестный класс модели")
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)