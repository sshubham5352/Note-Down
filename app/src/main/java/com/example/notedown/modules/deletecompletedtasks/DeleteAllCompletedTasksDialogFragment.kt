package com.example.notedown.modules.deletecompletedtasks

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedTasksDialogFragment : DialogFragment() {
    //class level fields
    private val viewModel: DeleteAllCompletedTasksViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Deletion")
            .setMessage("Do you want to delete all completed tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAllCompletedTasks()
            }
            .create()
}