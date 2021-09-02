package com.example.kotovskdatabase.ui.firstscreen

import android.graphics.LightingColorFilter
import android.util.Log
import androidx.lifecycle.*
import com.example.kotovskdatabase.repositiry.Repository
import com.example.kotovskdatabase.repositiry.RequestsDao
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CatListViewModel(
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    fun chooseRepository() = if (preferencesManager.getKeyBD() == ChooseBD.BY_ROOM.name) {
        Log.d("init", "ROOM")
        Repository.get()
    } else {
        Log.d("init", "COURSE")
        CursorDataBase.get()
    }

//    val repository2 = chooseRepository()
//    val preferencesFlow: Flow<FilterPreferences> = preferencesManager.orderFlow

//    @ExperimentalCoroutinesApi
//    private val preferencesFlow: Flow<String> = preferencesManager.orderFlow2


    //    https://habr.com/ru/post/529944/
    private val catEventChannel = Channel<CatEvent>()
    val catEvent: Flow<CatEvent> = catEventChannel.receiveAsFlow()

    //    @ExperimentalCoroutinesApi
//    private val catFlow = preferencesFlow.flatMapLatest { filterPreferences ->
    val preferencesKey = preferencesManager
//    }

//    val catFlow = repository.getAll()

    fun onAddNewCatClick() = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToAddCatFragment)
    }

    fun onTaskSwiped(cat: Cat) = viewModelScope.launch {
        chooseRepository().delete(cat)
        catEventChannel.send(CatEvent.ShowUndoDeleteTaskMessage(cat))
    }

    fun onUndoDeletedClick(cat: Cat) = viewModelScope.launch {
        chooseRepository().save(cat)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateKeySortOrder(sortOrder)
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

    fun choosingApiBD(chooseBD: ChooseBD) {
        preferencesKey.updateKeyBD(chooseBD)
    }


    sealed class CatEvent {
        object NavigateToAddCatFragment : CatEvent()
        data class NavigateToEditTaskScreen(val cat: Cat) : CatEvent()
        data class ShowUndoDeleteTaskMessage(val cat: Cat) : CatEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : CatEvent()
    }

//    @ExperimentalCoroutinesApi
//    val cats: List<Cat> = catFlow
//        .asLiveData()
}