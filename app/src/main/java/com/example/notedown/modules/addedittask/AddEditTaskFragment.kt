package com.example.notedown.modules.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notedown.R
import com.example.notedown.databinding.FragmentAddEditTaskBinding
import com.example.notedown.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {
    //class level fields
    private lateinit var binding: FragmentAddEditTaskBinding
    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            titleTask.setText(viewModel.taskTitle)
            priorityCheckbox.isChecked = viewModel.isTaskPriority
            priorityCheckbox.jumpDrawablesToCurrentState()
            descriptionTask.setText(viewModel.taskDescription)
            if (viewModel.task == null) {
                relativeTimeStamp.visibility = View.INVISIBLE
                createdTimeStamp.visibility = View.GONE
            } else {
                relativeTimeStamp.text = viewModel.task?.relativeTimeStamp
                createdTimeStamp.text =
                    getString(R.string.created_on, viewModel.task?.createTimeStamp)
            }
            titleTask.addTextChangedListener {
                viewModel.taskTitle = it.toString()
            }

            descriptionTask.addTextChangedListener {
                viewModel.taskDescription = it.toString()
            }

            priorityCheckbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isTaskPriority = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    AddEditTaskViewModel.AddEditTaskEvent.ShowEmptyTitleError -> {
                        binding.titleTask.error = "Title can't be empty!"
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackToTasksScreen -> {
                        //clearing focus in order to close soft keyboard
                        binding.titleTask.clearFocus()
                        binding.descriptionTask.clearFocus()

                        setFragmentResult(
                            Constants.ADD_EDIT_FRAGMENT,
                            bundleOf(Constants.ACTION_DONE_MESSAGE to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}
