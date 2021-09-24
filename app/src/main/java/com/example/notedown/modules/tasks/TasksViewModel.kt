package com.example.notedown.modules.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.notedown.data.PreferencesManager
import com.example.notedown.data.SortOrder
import com.example.notedown.data.Task
import com.example.notedown.data.TaskDao
import com.example.notedown.util.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")       //MutableStateFlow("")
    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val taskFlow =
        combine(searchQuery.asFlow(), preferencesFlow) { query, filterPreferences ->
            Pair(query, filterPreferences)
        }.flatMapLatest { (query, filterPreferences) ->
            taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
        }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedOnClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(isCompleted = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onTaskSelected(task: Task) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditFragmentResult(result: Int) = viewModelScope.launch {
        when (result) {
            Constants.RESULT_TASK_ADDED_OK -> tasksEventChannel.send(TasksEvent.ShowTaskAddedConfirmationMessage)
            Constants.RESULT_TASK_UPDATED_OK -> tasksEventChannel.send(TasksEvent.ShowTaskUpdatedConfirmationMessage)
        }
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedTasksScreen)
    }

    sealed class TasksEvent {
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        object NavigateToAddTaskScreen : TasksEvent()
        object ShowTaskAddedConfirmationMessage : TasksEvent()
        object ShowTaskUpdatedConfirmationMessage : TasksEvent()
        object NavigateToDeleteAllCompletedTasksScreen : TasksEvent()
    }
}
