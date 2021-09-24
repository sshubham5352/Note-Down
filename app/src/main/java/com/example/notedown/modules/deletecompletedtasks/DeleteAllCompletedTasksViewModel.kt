package com.example.notedown.modules.deletecompletedtasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.notedown.data.TaskDao
import com.example.notedown.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedTasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun deleteAllCompletedTasks() = applicationScope.launch {
        taskDao.deleteAllCompletedTasks()
    }
}