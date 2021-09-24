package com.example.notedown.util

object Constants {
    //Fragment Contexts
    const val TASKS_FRAGMENT = "tasksFragment"
    const val ADD_EDIT_FRAGMENT = "addEditResults"
    const val DELETE_ALL_COMPLETED_TASKS_DIALOG = "deleteAllCompletedDialog"

    //Fragments Result Constants
    const val RESULT_TASK_ADDED_OK = 101
    const val RESULT_TASK_UPDATED_OK = 102
    const val RESULT_NO_UPDATION_IN_TASK_OK = 103

    //Fragments Result Keys
    const val ACTION_DONE_MESSAGE = "msg"

    // Room DB Constants
    const val TASK_TABLE = "task_table"

    // JETPACK DataStore Constants
    const val USER_PREFERENCES = "user_preferences"
}