package com.example.notedown.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notedown.util.Constants
import com.example.notedown.util.Helper
import kotlinx.android.parcel.Parcelize

@Entity(tableName = Constants.TASK_TABLE)
@Parcelize
data class Task(
    val title: String,
    val description: String = "N/A",
    val isPriority: Boolean = false,
    val isCompleted: Boolean = false,
    val createdTimeInMillis: Long = System.currentTimeMillis(),
    val modifiedTimeInMillis: Long = createdTimeInMillis,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val relativeTimeStamp: String
        get() = Helper.getRelativeTimeStamp(modifiedTimeInMillis)

    val createTimeStamp: String
        get() = Helper.primaryDateFormat.format(createdTimeInMillis)
}
