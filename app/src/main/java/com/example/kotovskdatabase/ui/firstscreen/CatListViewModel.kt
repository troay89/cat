package com.example.kotovskdatabase.ui.firstscreen

import android.util.Log
import androidx.lifecycle.*
import com.example.kotovskdatabase.repositiry.RepositoryImpl
import com.example.kotovskdatabase.repositiry.cursor.CursorDataBase
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.ui.ADD_TASK_RESULT_OK
import com.example.kotovskdatabase.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CatListViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {

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
    private val catFlow:Flow<List<Cat>> =
        preferencesFlow.flatMapLatest { filterPreferences ->
            Log.d("catFlow", filterPreferences.sortOrder.name)
            Log.d("catFlow", filterPreferences.chooseBD.name)
        chooseRepository().getListCats(filterPreferences.sortOrder.name)
    }

    @ExperimentalCoroutinesApi
    var cats: LiveData<List<Cat>> = catFlow.asLiveData()

    fun onAddNewCatClick() = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToAddCatFragment(preferencesManager.getKeyBD()))
    }

    fun onTaskSwiped(cat: Cat) = viewModelScope.launch {
        chooseRepository().delete(cat)
        catEventChannel.send(CatEvent.ShowUndoDeleteTaskMessage(cat))
    }

    fun onUndoDeletedClick(cat: Cat) = viewModelScope.launch {
        chooseRepository().save(cat)
    }

    fun onCatSelected(cat: Cat) = viewModelScope.launch {
        catEventChannel.send(CatEvent.NavigateToEditTaskScreen(cat, preferencesManager.getKeyBD()))
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
        data class NavigateToEditTaskScreen(val cat: Cat, val keyBd: String) : CatEvent()
        data class ShowUndoDeleteTaskMessage(val cat: Cat) : CatEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : CatEvent()
    }
}