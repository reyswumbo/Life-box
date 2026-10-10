package com.reyswumbo.lifeinbox.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LinkCard(
    link: LinkEntity,
    collectionName: String,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val formattedDate = remember(link.createdAt) { formatDate(link.createdAt) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = LifeInboxColors.SurfaceWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = link.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LifeInboxColors.DarkText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = link.domain,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$collectionName \u00B7 $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeInboxColors.SecondaryText
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (link.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = if (link.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (link.isFavorite) LifeInboxColors.SoftYellow else LifeInboxColors.SecondaryText,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More options",
                    tint = LifeInboxColors.SecondaryText,
                    modifier = Modifier.size(22.dp)
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Open") },
                    leadingIcon = { Icon(Icons.Filled.OpenInNew, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onOpen()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Edit") },
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Move") },
                    leadingIcon = { Icon(Icons.Filled.MoveDown, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onMove()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Copy URL") },
                    leadingIcon = { Icon(Icons.Filled.ContentCopy, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onCopy()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onDelete()
                    }
                )
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String =
    SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(epochMillis))
