package com.example.kotovskdatabase.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotovskdatabase.App
import com.example.kotovskdatabase.domain.usecase.DeleteCatUseCase
import com.example.kotovskdatabase.domain.usecase.GetListCatsUseCase
import com.example.kotovskdatabase.ui.firstscreen.CatListViewModel

//class ViewModelFactory(
//    private val app: App
//) : ViewModelProvider.Factory{
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        val viewModel = when (modelClass) {
//            CatListViewModel::class.java -> {
//                CatListViewModel(app.preferencesManager )
//            }
//
//            else -> throw IllegalStateException("Неизвестный класс модели")
//        }
//        return viewModel as T
//    }
//}
//
//fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)