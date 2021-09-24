package com.example.notedown.modules.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notedown.data.Task
import com.example.notedown.data.TaskDao
import com.example.notedown.util.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val taskDao: TaskDao
) : ViewModel() {

    val task = state.get<Task>("task")
    var taskTitle = state.get<String>("taskName") ?: task?.title ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var isTaskPriority = state.get<Boolean>("isPriority") ?: task?.isPriority ?: false
        set(value) {
            field = value
            state.set("isPriority", field)
        }

    var taskDescription = state.get<String>("taskDescription") ?: task?.description ?: ""
        set(value) {
            field = value
            state.set("taskDescription", value)
        }

    //creating channel
    private val eventsChannel = Channel<AddEditTaskEvent>()
    val events = eventsChannel.receiveAsFlow()

    fun onSaveClick() {
        if (validateFields()) {
            if (task != null)
                updateTask()
            else addNewTask()
        }
    }

    private fun validateFields(): Boolean {
        var valid = true
        viewModelScope.launch {
            if (taskTitle.isBlank()) {
                eventsChannel.send(AddEditTaskEvent.ShowEmptyTitleError)
                valid = false
            }
        }
        return valid
    }

    private fun updateTask() = viewModelScope.launch {
        if (taskTitle != task?.title || isTaskPriority != task.isPriority || taskDescription != task.description) {
            taskDao.update(
                task!!.copy(
                    title = taskTitle, description = taskDescription,
                    isPriority = isTaskPriority, modifiedTimeInMillis = System.currentTimeMillis()
                )
            )
            eventsChannel.send(AddEditTaskEvent.NavigateBackToTasksScreen(Constants.RESULT_TASK_UPDATED_OK))
        } else
            eventsChannel.send(AddEditTaskEvent.NavigateBackToTasksScreen(Constants.RESULT_NO_UPDATION_IN_TASK_OK))

    }

    private fun addNewTask() = viewModelScope.launch {
        taskDao.insert(Task(taskTitle, taskDescription, isTaskPriority))
        eventsChannel.send(AddEditTaskEvent.NavigateBackToTasksScreen(Constants.RESULT_TASK_ADDED_OK))
    }

    sealed class AddEditTaskEvent {
        object ShowEmptyTitleError : AddEditTaskEvent()
        data class NavigateBackToTasksScreen(val result: Int) : AddEditTaskEvent()
    }
}
