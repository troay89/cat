package com.example.kotovskdatabase.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.repositiry.Repository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatListViewModel : ViewModel() {

    private val repository = Repository.get()

//    https://habr.com/ru/post/529944/
    private val catEventChannel = Channel<CatEvent>()
    val catEvent: Flow<CatListViewModel.CatEvent> = catEventChannel.receiveAsFlow()

    private val catFlow = repository.getAll()

    fun onAddNewCatClick() = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToCatFragment)
    }

    sealed class CatEvent {
        object  NavigateToCatFragment: CatEvent()
    }

    val cats = catFlow.asLiveData()
}