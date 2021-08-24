package com.example.notedown.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notedown.util.Constants
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = Constants.TASK_TABLE)
@Parcelize
data class Task(
    val title: String,
    val description: String = "N/A",
    val isPriority: Boolean = false,
    val isCompleted: Boolean = false,
    val timeInMillis: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {

    val modifiedTime: String
        get() = DateFormat.getTimeInstance().format(timeInMillis)
}