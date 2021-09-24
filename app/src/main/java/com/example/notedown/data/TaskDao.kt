package com.example.notedown.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_TITLE -> getTasksSortedByTitle(query, hideCompleted)
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY isPriority DESC, title")
    fun getTasksSortedByTitle(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY isPriority DESC, createdTimeInMillis DESC")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE isCompleted = 1")
    suspend fun deleteAllCompletedTasks()
}
