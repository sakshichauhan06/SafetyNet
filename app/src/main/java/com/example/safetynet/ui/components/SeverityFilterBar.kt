package com.example.safetynet.ui.components

import android.adservices.common.AdFilters
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.safetynet.domain.SeverityLevel

data class FilterChip(
    val label: String,
    val color: Color,
    val severity: SeverityLevel
)

@Composable
fun SeverityFilterBar(
    activeFilters: Set<SeverityLevel>,
    onFilterToggle: (SeverityLevel) -> Unit
) {
    val filters = listOf(
        FilterChip("Critical", Color(0xFFEB2A34), SeverityLevel.RED),
        FilterChip("High", Color(0xFFE67E22), SeverityLevel.ORANGE),
        FilterChip("Medium", Color(0xFFF1C40F), SeverityLevel.YELLOW),
        FilterChip("Low", Color(0xFF2ECC71), SeverityLevel.GREEN),
        FilterChip("Other", Color.Gray, SeverityLevel.GREY)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters) { filter ->
            val isActive = activeFilters.contains(filter.severity)

            FilterChipItem(
                label = filter.label,
                color = filter.color,
                isActive = isActive,
                onClick = {
                    onFilterToggle(filter.severity)
                }
            )
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isActive) color.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.9f)
            )
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) color else Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, RoundedCornerShape(4.dp))
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isActive) color else Color.DarkGray
            )
        }
    }
}
