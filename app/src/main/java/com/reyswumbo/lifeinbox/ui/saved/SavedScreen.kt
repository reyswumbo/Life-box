package com.reyswumbo.lifeinbox.ui.saved

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyswumbo.lifeinbox.LifeInboxApplication
import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity
import com.reyswumbo.lifeinbox.domain.LinkSortOption
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors
import kotlinx.coroutines.launch

@Composable
fun SavedScreen() {
    val application = LocalContext.current.applicationContext as LifeInboxApplication
    val viewModel: SavedViewModel = viewModel(
        factory = SavedViewModelFactory(
            application.container.linkRepository,
            application.container.collectionRepository
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var showAddDialog by remember { mutableStateOf(false) }
    var editingLink by remember { mutableStateOf<LinkEntity?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SavedEvent.LinkSaved -> showAddDialog = false
                SavedEvent.LinkUpdated -> editingLink = null
                is SavedEvent.Message -> snackbarHostState.showSnackbar(event.text)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Your Link Library",
                style = MaterialTheme.typography.titleLarge,
                color = LifeInboxColors.DarkText
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = viewModel::onQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search your saved links...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )
                Box {
                    IconButton(onClick = { sortMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Sort,
                            contentDescription = "Sort links",
                            tint = LifeInboxColors.DarkText
                        )
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        LinkSortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    viewModel.onSortChange(option)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LifeInboxColors.Sage)
                    }
                }

                uiState.links.isEmpty() -> {
                    EmptyLibraryState(
                        searchActive = uiState.query.isNotBlank(),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            top = 4.dp,
                            bottom = 96.dp
                        )
                    ) {
                        items(uiState.links, key = { it.id }) { link ->
                            LinkCard(
                                link = link,
                                collectionName = uiState.collections
                                    .firstOrNull { it.id == link.collectionId }?.name
                                    ?: "Inbox",
                                onOpen = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link.url))
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("No app can open this link.")
                                        }
                                    }
                                },
                                onToggleFavorite = { viewModel.toggleFavorite(link) },
                                onEdit = {
                                    viewModel.clearDialogError()
                                    editingLink = link
                                },
                                onCopy = {
                                    clipboard.setText(AnnotatedString(link.url))
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Link copied.")
                                    }
                                },
                                onDelete = { viewModel.deleteLink(link) }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                viewModel.clearDialogError()
                showAddDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = LifeInboxColors.Sage
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add a link",
                tint = LifeInboxColors.DarkText
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }

    if (showAddDialog) {
        LinkEditorDialog(
            dialogTitle = "Save a link",
            urlEditable = true,
            initialUrl = "",
            initialTitle = "",
            initialNotes = "",
            initialCollectionId = uiState.collections.firstOrNull()?.id,
            collections = uiState.collections,
            isSaving = uiState.isSaving,
            errorMessage = uiState.dialogError,
            confirmLabel = "Save",
            onDismiss = {
                viewModel.clearDialogError()
                showAddDialog = false
            },
            onConfirm = { url, title, notes, collectionId ->
                viewModel.saveLink(url, title, notes, collectionId)
            }
        )
    }

    editingLink?.let { link ->
        LinkEditorDialog(
            dialogTitle = "Edit link",
            urlEditable = false,
            initialUrl = link.url,
            initialTitle = link.title,
            initialNotes = link.notes,
            initialCollectionId = link.collectionId,
            collections = uiState.collections,
            isSaving = uiState.isSaving,
            errorMessage = uiState.dialogError,
            confirmLabel = "Save changes",
            onDismiss = {
                viewModel.clearDialogError()
                editingLink = null
            },
            onConfirm = { _, title, notes, collectionId ->
                viewModel.updateLink(link.id, title, notes, collectionId ?: link.collectionId)
            }
        )
    }
}

@Composable
private fun EmptyLibraryState(
    searchActive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (searchActive) "No matches found" else "Your library is empty",
            style = MaterialTheme.typography.titleMedium,
            color = LifeInboxColors.DarkText,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (searchActive) {
                "Try a different search term."
            } else {
                "Save your first link and it will rest here safely."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = LifeInboxColors.SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}
