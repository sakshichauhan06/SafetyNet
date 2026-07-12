package com.example.safetynet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TimelineEvent(
    val title: String,
    val description: String,
    val timestamp: Long,
    val isCompleted: Boolean
)

@Composable
fun StatusTimeline(
    events: List<TimelineEvent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "STATUS TIMELINE",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        events.forEachIndexed { index, event ->
            val isLast = index == events.lastIndex
            TimelineItem(
                event = event,
                isLast = isLast
            )
        }
    }
}

@Composable
private fun TimelineItem(
    event: TimelineEvent,
    isLast: Boolean
) {
    val lineColor = if (event.isCompleted) Color(0xFF1A237E) else Color.LightGray
    val circleColor = if (event.isCompleted) Color(0xFF1A237E) else Color.White
    val contentColor = if (event.isCompleted) Color(0xFF1A237E) else Color.Gray

    // Use IntrinsicSize.Min so Row measures children and gives real height
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),  // KEY FIX: Gives Row measurable height
        verticalAlignment = Alignment.Top
    ) {
        // Timeline column: circle + line
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            // Circle first
            Surface(
                shape = CircleShape,
                color = if (event.isCompleted) circleColor else Color.White,
                border = if (!event.isCompleted) {
                    androidx.compose.foundation.BorderStroke(2.dp, Color.LightGray)
                } else null,
                modifier = Modifier.size(12.dp)
            ) {}

            // Line extends downward, fills remaining space
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)  // KEY FIX: Fills space between circles
                        .padding(top = 4.dp, bottom = 4.dp)
                        .background(lineColor)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 32.dp)  // Space between items
        ) {
            // Title + timestamp row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top  // Align to top with circle
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )

                if (event.timestamp > 0) {
                    val formattedTime = SimpleDateFormat("MMM d, yyyy • h:mm a",
                        Locale.getDefault())
                        .format(Date(event.timestamp))

                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}