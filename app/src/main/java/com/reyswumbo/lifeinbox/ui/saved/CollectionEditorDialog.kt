package com.reyswumbo.lifeinbox.ui.saved

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@Composable
fun CollectionEditorDialog(
    dialogTitle: String,
    initialName: String,
    initialColorHex: String,
    initialIcon: String,
    isSaving: Boolean,
    errorMessage: String?,
    confirmLabel: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, colorHex: String, icon: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var colorHex by remember { mutableStateOf(initialColorHex) }
    var icon by remember { mutableStateOf(initialIcon) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Collection name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Icon",
                    style = MaterialTheme.typography.labelLarge,
                    color = LifeInboxColors.SecondaryText
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(CollectionVisuals.iconKeys) { key ->
                        val selected = key == icon
                        Icon(
                            imageVector = CollectionVisuals.iconFor(key),
                            contentDescription = key,
                            tint = LifeInboxColors.DarkText,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) LifeInboxColors.PaleSage
                                    else LifeInboxColors.WarmCream
                                )
                                .border(
                                    BorderStroke(
                                        if (selected) 2.dp else 0.dp,
                                        if (selected) LifeInboxColors.Sage else Color.Transparent
                                    ),
                                    CircleShape
                                )
                                .clickable { icon = key }
                                .padding(9.dp)
                        )
                    }
                }

                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelLarge,
                    color = LifeInboxColors.SecondaryText
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(CollectionVisuals.palette) { hex ->
                        val selected = hex.equals(colorHex, ignoreCase = true)
                        Spacer(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CollectionVisuals.colorFromHex(hex))
                                .border(
                                    BorderStroke(
                                        if (selected) 2.dp else 1.dp,
                                        if (selected) LifeInboxColors.DarkText
                                        else LifeInboxColors.PaleSage
                                    ),
                                    CircleShape
                                )
                                .clickable { colorHex = hex }
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, colorHex, icon) },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(confirmLabel)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
