package com.example.kotovskdatabase.ui.firstscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CatListViewModel(val preferencesManager: PreferencesManager): ViewModel() {

//    private val repository = Repository.get()
    private val repository = CursorDataBase.get()

    @ExperimentalCoroutinesApi
    private val preferencesFlow: Flow<String> = preferencesManager.orderFlow2


    //    https://habr.com/ru/post/529944/
    private val catEventChannel = Channel<CatEvent>()
    val catEvent: Flow<CatEvent> = catEventChannel.receiveAsFlow()

//    @ExperimentalCoroutinesApi
//    private val catFlow = preferencesFlow.flatMapLatest { filterPreferences ->
//        repository.getTasks(filterPreferences)
//    }

    private val catFlow = repository.getAll()

    fun onAddNewCatClick() = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToAddCatFragment)
    }

    fun onTaskSwiped(cat: Cat) = viewModelScope.launch {
        repository.delete(cat)
        catEventChannel.send(CatEvent.ShowUndoDeleteTaskMessage(cat))
    }

    fun onUndoDeletedClick(cat: Cat) = viewModelScope.launch {
        repository.save(cat)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onCatSelected(cat: Cat) = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToEditTaskScreen(cat))
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


    sealed class CatEvent {
        object NavigateToAddCatFragment : CatEvent()
        data class NavigateToEditTaskScreen(val cat: Cat) : CatEvent()
        data class ShowUndoDeleteTaskMessage(val cat: Cat) : CatEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : CatEvent()
    }

    @ExperimentalCoroutinesApi
    val cats: LiveData<List<Cat>> = catFlow.asLiveData()
}