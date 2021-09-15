package com.example.kotovskdatabase.ui.secondscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.domain.Repository
import com.example.kotovskdatabase.domain.usecase.SaveCatUseCase
import com.example.kotovskdatabase.domain.usecase.UpdateCatUseCase
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.firstscreen.ChooseBD
import com.example.kotovskdatabase.ui.mapper.UICatToDomain
import com.example.kotovskdatabase.ui.model.UICat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatViewModel(
    private val repositoryRoom: Repository
) : ViewModel() {

    lateinit var repository: Repository

    fun chooseRepository(apiBD: String) {
        repository = if (apiBD == ChooseBD.FROM_ROOM.name) {
            Log.d("init second", "ROOM")
            repositoryRoom
        }  else {
            Log.d("init second", "COURSE")
            repositoryRoom
        }
    }


    fun onSaveClick(catName: String, catBreed: String, catAge: String) {
        if (check(catName, catBreed, catAge)) {
            showInvalidInputMessage()
            return
        }
        val newCat =
            UICat(id = 0, name = catName, breed = catBreed, age = catAge.toInt())
        createCat(newCat)
    }

    fun onUpdateClick(uiCat: UICat, catName: String, catBreed: String, catAge: String) {
        if (check(catName, catBreed, catAge)) {
            showInvalidInputMessage()
            return
        }
        val updateCat =
            uiCat.copy(name = catName, breed = catBreed, age = catAge.toString().toInt())
        updateCat(updateCat)
    }

    private fun check(catName: String, catBreed: String, catAge: String) =
        (catName.isBlank() || catBreed.isBlank() || catAge.isBlank())



    private val addEditCatEventChannel = Channel<AddEditCatEvent>()
    val addEditCatEvent = addEditCatEventChannel.receiveAsFlow()

    private fun createCat(newUICat: UICat) = viewModelScope.launch {
        val catDomain = UICatToDomain.map(newUICat)
        SaveCatUseCase(repository).execute(catDomain)
        addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateCat(updateUICat: UICat) = viewModelScope.launch {
        val catDomain = UICatToDomain.map(updateUICat)
        UpdateCatUseCase(repository).execute(catDomain)
        addEditCatEventChannel.send(AddEditCatEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage() = viewModelScope.launch {
        addEditCatEventChannel.send(AddEditCatEvent.ShowInvalidInputMessage)
    }

    sealed class AddEditCatEvent {
        object ShowInvalidInputMessage : AddEditCatEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditCatEvent()
    }
}