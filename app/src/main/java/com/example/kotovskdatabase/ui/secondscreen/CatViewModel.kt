package com.example.kotovskdatabase.ui.secondscreen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.domain.usecase.SaveCatUseCase
import com.example.kotovskdatabase.domain.usecase.UpdateCatUseCase
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.room.RepositoryImpl
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.firstscreen.ChooseBD
import com.example.kotovskdatabase.ui.mapper.UICatToDomain
import com.example.kotovskdatabase.ui.model.UICat
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.API_BD_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_AGE_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_BREED_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_KEY
import com.example.kotovskdatabase.ui.secondscreen.CatViewModel.CatViewModel.CAT_NAME_KEY
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatViewModel(
    private val state: SavedStateHandle,
) : ViewModel() {

    private val uiCat:UICat? = state.get<UICat>(CAT_KEY)

    private fun chooseRepository() = if (state.get<String>(API_BD_KEY) == ChooseBD.FROM_ROOM.name) {
        Log.d("init second", "ROOM")
        RepositoryImpl.get()
    } else {
        Log.d("init second", "COURSE")
        CursorDataBase.get()
    }

    var catName = state.get<String>(CAT_NAME_KEY) ?: uiCat?.name ?: ""
        set(value) {
            field = value
            state.set(CAT_NAME_KEY, value)
        }

    var catBreed = state.get<String>(CAT_BREED_KEY) ?: uiCat?.breed ?: ""
        set(value) {
            field = value
            state.set(CAT_BREED_KEY, value)
        }

    var catAge = state.get<String>(CAT_AGE_KEY) ?: uiCat?.age ?: ""
        set(value) {
            field = value
            state.set(CAT_AGE_KEY, value)
        }


    fun onSaveClick() {
        if (catName.isBlank() || catBreed.isBlank() || catAge.toString().isBlank()) {
            showInvalidInputMessage()
            return
        }
        if (uiCat == null) {
            val newCat = UICat(id = 0, name = catName, breed = catBreed, age = catAge.toString().toInt())
            createCat(newCat)
        }
        else{
            val updateCat = uiCat.copy(name = catName, breed = catBreed, age = catAge.toString().toInt())
            updateCat(updateCat)
        }
    }

    private val addEditCatEventChannel = Channel<AddEditCatEvent>()
    val addEditCatEvent = addEditCatEventChannel.receiveAsFlow()

    private fun createCat(newUICat: UICat) = viewModelScope.launch {
        val catDomain = UICatToDomain.map(newUICat)
        SaveCatUseCase(chooseRepository()).execute(catDomain)
        addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateCat(updateUICat: UICat) = viewModelScope.launch {
        val catDomain = UICatToDomain.map(updateUICat)
        UpdateCatUseCase(chooseRepository()).execute(catDomain)
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