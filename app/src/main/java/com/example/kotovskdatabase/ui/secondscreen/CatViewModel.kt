package com.example.kotovskdatabase.ui.secondscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.firstscreen.ChooseBD
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatViewModel(
    private val state: SavedStateHandle,
) : ViewModel() {

    val cat: Cat? = state.get<Cat>("cat")

    private fun chooseRepository() = if (state.get<String>("ApiBd") == ChooseBD.BY_ROOM.name) {
        Log.d("init second", "ROOM")
        Repository.get()
    } else {
        Log.d("init second", "COURSE")
        CursorDataBase.get()
    }

    var catName = state.get<String>("catName") ?: cat?.name ?: ""
        set(value) {
            field = value
            state.set("catName", value)
        }

    var catBreed = state.get<String>("catBreed") ?: cat?.breed ?: ""
        set(value) {
            field = value
            state.set("catBreed", value)
        }

    var catAge = state.get<String>("catAge") ?: cat?.age ?: ""
        set(value) {
            field = value
            state.set("catAge", value)
        }


    fun onSaveClick() {
        if (catName.isBlank() || catBreed.isBlank() || catAge.toString().isBlank()) {
            showInvalidInputMessage("не все поля заполнены")
            return
        }
        if (cat == null) {
            val newCat = Cat(name = catName, breed = catBreed, age = catAge.toString().toInt())
            createCat(newCat)
        }
        if(cat != null){
            val updateCat = cat.copy(name = catName, breed = catBreed, age = catAge.toString().toInt())
            updateCat(updateCat)
        }
    }


    private val addEditCatEventChannel = Channel<AddEditCatEvent>()
    val addEditCatEvent = addEditCatEventChannel.receiveAsFlow()

    private fun createCat(newCat: Cat) = viewModelScope.launch {
        chooseRepository().save(newCat)
        addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateCat(updateCat: Cat) = viewModelScope.launch {
        chooseRepository().update(updateCat)
        addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditCatEventChannel.send(AddEditCatEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditCatEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditCatEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditCatEvent()
    }
}