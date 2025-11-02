package utils

object TimeUtils {

    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

       return when{
           days > 0 -> "Reported $days ${if (days == 1L) "day" else "days"} ago"
           hours > 0 -> "Reported $hours ${if (hours == 1L) "hour" else "hours"} ago"
           minutes > 0 -> "Reported $minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
           else -> "Reported just now"
       }
    }

}