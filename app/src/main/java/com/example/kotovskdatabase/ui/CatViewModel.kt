package com.example.kotovskdatabase.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.entity.Cat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatViewModel(
    private val state: SavedStateHandle,
    private val repository: Repository,
) : ViewModel() {

    val cat: Cat? = state.get<Cat>("cat")

    var catName = cat?.name ?: ""

    var catBreed =  cat?.breed ?: ""

    var catAge =  cat?.age ?: ""


    private val addEditCatEventChannel = Channel<AddEditCatEvent>()
    val addEditCatEvent = addEditCatEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (cat == null) {
            val newCat = Cat(name = catName, breed = catBreed, age = catAge)
            createCat(newCat)
        }
    }

    private fun createCat(newCat: Cat) = viewModelScope.launch{
        repository.save(newCat)
        addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    sealed class AddEditCatEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditCatEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditCatEvent()
    }
}