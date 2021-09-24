package com.example.notedown.modules.tasks

import android.os.Bundle
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notedown.R
import com.example.notedown.data.SortOrder
import com.example.notedown.data.Task
import com.example.notedown.databinding.FragmentTasksBinding
import com.example.notedown.util.Constants
import com.example.notedown.util.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnTaskItemClickListener {
    // class level fields
    private val viewModel: TasksViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val tasksAdapter = TasksAdapter(this)
        binding.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = tasksAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)
            fabAddTask.setOnClickListener {
                viewModel.onAddNewTaskClick()
            }
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        //Handling events from viewModel
        /*
         * "launchWhenStarted" is used instead of "launch" because it get stops when "onStop" is called
         * and starts when "onStart" is called that means if we receive a call back from our channel
         * and the app is in the background then it would wait and stack all the callbacks until the
         * app comes back to foreground.
         * */
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                event.task,
                                "Edit Task"
                            )
                        findNavController().navigate(action)
                    }
                    TasksViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(
                                null,
                                "New Task"
                            )
                        findNavController().navigate(action)
                    }
                    TasksViewModel.TasksEvent.ShowTaskAddedConfirmationMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Task Added Successfully.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    TasksViewModel.TasksEvent.ShowTaskUpdatedConfirmationMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Task Updated Successfully.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    TasksViewModel.TasksEvent.NavigateToDeleteAllCompletedTasksScreen -> {
                        val action =
                            TasksFragmentDirections.actionGlobalDeleteAllCompletedTasksDialogFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }

        //fragmentResultListener
        setFragmentResultListener(Constants.ADD_EDIT_FRAGMENT) { _, bundle ->
            val result = bundle.getInt(Constants.ACTION_DONE_MESSAGE)
            viewModel.onAddEditFragmentResult(result)
        }

        //let fragment know that toolbar has menu options
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                TransitionManager.beginDelayedTransition(activity?.findViewById(R.id.toolbar))
                true
            }

            R.id.action_sort_by_title -> {
                viewModel.onSortOrderSelected(SortOrder.BY_TITLE)
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedOnClick(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                viewModel.onDeleteAllCompletedClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}
