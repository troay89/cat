package com.example.kotovskdatabase.ui.firstscreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.domain.model.CatDomain
import com.example.kotovskdatabase.domain.usecase.DeleteCatUseCase
import com.example.kotovskdatabase.domain.usecase.GetListCatsUseCase
import com.example.kotovskdatabase.domain.usecase.SaveCatUseCase
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.room.RepositoryImpl
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatListViewModel(private val preferencesManager: PreferencesManager, ) : ViewModel() {

    private fun chooseRepository() = if (preferencesManager.getKeyBD() == ChooseBD.FROM_ROOM.name) {
        Log.d("init first", "ROOM")
        RepositoryImpl.get()
    } else {
        Log.d("init first", "COURSE")
        CursorDataBase.get()
    }

    //    https://habr.com/ru/post/529944/
    private val catEventChannel = Channel<CatEvent>()
    val catEvent: Flow<CatEvent> = catEventChannel.receiveAsFlow()

    @ExperimentalCoroutinesApi
    private val preferencesFlow: Flow<FilterPreferences> = preferencesManager.orderFlow

    @ExperimentalCoroutinesApi
    private val catDomainFlow:Flow<List<CatDomain>> =
        preferencesFlow.flatMapLatest { filterPreferences ->
            Log.d("catFlow", filterPreferences.sortOrder.name)
            Log.d("catFlow", filterPreferences.chooseBD.name)
            GetListCatsUseCase(chooseRepository()).execute(filterPreferences.sortOrder.name)
    }

    @ExperimentalCoroutinesApi
    var cats: LiveData<List<CatDomain>> = catDomainFlow.asLiveData()

    fun onAddNewCatClick() = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToAddCatFragment(preferencesManager.getKeyBD()))
    }

    fun onTaskSwiped(catDomain: CatDomain) = viewModelScope.launch {
        DeleteCatUseCase(chooseRepository()).execute(catDomain)
        catEventChannel.send(CatEvent.ShowUndoDeleteTaskMessage(catDomain))
    }

    fun onUndoDeletedClick(catDomain: CatDomain) = viewModelScope.launch {
        SaveCatUseCase(chooseRepository()).execute(catDomain)
    }

    fun onCatSelected(catDomain: CatDomain) = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToEditTaskScreen(catDomain, preferencesManager.getKeyBD()))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("кот добавлен")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("кот обнавлён")
        }
    }

    private fun showTaskSavedConfirmationMessage(s: String) = viewModelScope.launch {
        catEventChannel.send(CatEvent.ShowTaskSavedConfirmationMessage(s))
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun choosingApiBD(chooseBD: ChooseBD) {
        preferencesManager.updateKeyBD(chooseBD)
    }


    sealed class CatEvent {
        data class NavigateToAddCatFragment(val keyBd: String) : CatEvent()
        data class NavigateToEditTaskScreen(val catDomain: CatDomain, val keyBd: String) : CatEvent()
        data class ShowUndoDeleteTaskMessage(val catDomain: CatDomain) : CatEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : CatEvent()
    }
}