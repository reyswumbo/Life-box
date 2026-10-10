package com.reyswumbo.lifeinbox.ui.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollectionCard(
    name: String,
    count: Int,
    color: Color,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(136.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) LifeInboxColors.Sage else LifeInboxColors.PaleSage
        ),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LifeInboxColors.DarkText,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = LifeInboxColors.DarkText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (count == 1) "1 saved" else "$count saved",
                style = MaterialTheme.typography.labelSmall,
                color = LifeInboxColors.SecondaryText
            )
        }
    }
}
