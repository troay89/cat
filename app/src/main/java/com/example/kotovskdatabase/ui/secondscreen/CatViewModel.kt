package com.example.kotovskdatabase.ui.secondscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.repositiry.room.RepositoryImpl
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.firstscreen.ChooseBD
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.API_BD_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_AGE_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_BREED_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_NAME_KEY
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatViewModel(
    private val state: SavedStateHandle,
) : ViewModel() {

    val cat: Cat? = state.get<Cat>(CAT_KEY)

    private fun chooseRepository() = if (state.get<String>(API_BD_KEY) == ChooseBD.FROM_ROOM.name) {
        Log.d("init second", "ROOM")
        RepositoryImpl.get()
    } else {
        Log.d("init second", "COURSE")
        CursorDataBase.get()
    }

    var catName = state.get<String>(CAT_NAME_KEY) ?: cat?.name ?: ""
        set(value) {
            field = value
            state.set(CAT_NAME_KEY, value)
        }

    var catBreed = state.get<String>(CAT_BREED_KEY) ?: cat?.breed ?: ""
        set(value) {
            field = value
            state.set(CAT_BREED_KEY, value)
        }

    var catAge = state.get<String>(CAT_AGE_KEY) ?: cat?.age ?: ""
        set(value) {
            field = value
            state.set(CAT_AGE_KEY, value)
        }


    fun onSaveClick() {
        if (catName.isBlank() || catBreed.isBlank() || catAge.toString().isBlank()) {
            showInvalidInputMessage()
            return
        }
        if (cat == null) {
            val newCat = Cat(name = catName, breed = catBreed, age = catAge.toString().toInt())
            createCat(newCat)
        }
        else{
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

    private fun showInvalidInputMessage() = viewModelScope.launch {
        addEditCatEventChannel.send(AddEditCatEvent.ShowInvalidInputMessage)
    }

    sealed class AddEditCatEvent {
        object ShowInvalidInputMessage : AddEditCatEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditCatEvent()
    }

    private object CatViewModel {
        const val CAT_KEY = "cat"
        const val API_BD_KEY = "ApiBd"
        const val CAT_NAME_KEY = "catName"
        const val CAT_BREED_KEY = "catBreed"
        const val CAT_AGE_KEY = "catAge"
    }
}