package com.example.notedown.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.notedown.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        /**
         * this method gets called for the first time this database gets created but not everytime
         * app starts.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //db operations
            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Buy Grocery", "bananas, apples, grapes, chiku"))
                dao.insert(Task("Go to GYM", "chest day", isCompleted = true))
                dao.insert(Task("Have protein shake", isCompleted = true))
                dao.insert(Task("Attend classes", "10-1pm", true))
                dao.insert(Task("Learn Android Development", "Work on Note down app", true))
                dao.insert(Task("Wash the dishes", "wash all the dishes at 9'o clock4"))
            }
        }
    }
}





