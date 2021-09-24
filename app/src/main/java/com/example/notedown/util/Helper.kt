package com.example.notedown.util

import java.text.SimpleDateFormat
import java.util.*

object Helper {
    //object fields
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy/HH/mm/ss", Locale.US)
    val primaryDateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US)

    fun getRelativeTimeStamp(timeInMillis: Long): String {
        val givenTimeStamp = simpleDateFormat.format(timeInMillis).split("/")
        val currentTimeStamp = simpleDateFormat.format(Date()).split("/")
        /* index positions:-
        * 0 -> day
        * 1 -> month
        * 2 -> year
        * 3 -> hours
        * 4 -> minutes
        * 5 -> seconds
        * */
        var timeGap: Int

        timeGap = currentTimeStamp[2].compareTo(givenTimeStamp[2])
        if (timeGap > 0)
            return "$timeGap years ago"

        timeGap = currentTimeStamp[1].compareTo(givenTimeStamp[1])
        if (timeGap > 0)
            return "$timeGap months ago"

        timeGap = currentTimeStamp[0].compareTo(givenTimeStamp[0])
        if (timeGap > 0)
            return "$timeGap days ago"

        timeGap = currentTimeStamp[3].compareTo(givenTimeStamp[3])
        if (timeGap > 0)
            return "$timeGap hrs ago"

        timeGap = currentTimeStamp[4].compareTo(givenTimeStamp[4])
        if (timeGap > 0)
            return "$timeGap mins ago"

        return "Just now"
    }
}
