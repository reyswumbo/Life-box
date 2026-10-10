package com.reyswumbo.lifeinbox.ui.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.reyswumbo.lifeinbox.data.local.CollectionWithCount
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@Composable
fun MoveLinkDialog(
    linkTitle: String,
    collections: List<CollectionWithCount>,
    currentCollectionId: Long,
    onDismiss: () -> Unit,
    onPick: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move link") },
        text = {
            Column {
                Text(
                    text = "Move \"$linkTitle\" to:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LifeInboxColors.SecondaryText
                )
                Spacer(Modifier.size(10.dp))
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(collections, key = { it.collection.id }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(item.collection.id) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = CollectionVisuals.iconFor(item.collection.icon),
                                contentDescription = null,
                                tint = LifeInboxColors.DarkText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = item.collection.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = LifeInboxColors.DarkText,
                                modifier = Modifier.weight(1f)
                            )
                            if (item.collection.id == currentCollectionId) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Current collection",
                                    tint = LifeInboxColors.Sage,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
