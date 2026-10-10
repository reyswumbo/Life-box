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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import com.reyswumbo.lifeinbox.data.local.CollectionWithCount
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
    var movingLink by remember { mutableStateOf<LinkEntity?>(null) }
    var showCreateCollection by remember { mutableStateOf(false) }
    var editingCollection by remember { mutableStateOf<CollectionWithCount?>(null) }
    var pendingDelete by remember { mutableStateOf<CollectionWithCount?>(null) }
    var collectionMenuFor by remember { mutableStateOf<Long?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var headerMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SavedEvent.LinkSaved -> showAddDialog = false
                SavedEvent.LinkUpdated -> editingLink = null
                SavedEvent.CollectionSaved -> {
                    showCreateCollection = false
                    editingCollection = null
                }

                is SavedEvent.CollectionDeleted -> pendingDelete = null
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Link Library",
                    style = MaterialTheme.typography.titleLarge,
                    color = LifeInboxColors.DarkText,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = { headerMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = LifeInboxColors.DarkText
                        )
                    }
                    DropdownMenu(
                        expanded = headerMenuExpanded,
                        onDismissRequest = { headerMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add link") },
                            onClick = {
                                headerMenuExpanded = false
                                viewModel.clearDialogError()
                                showAddDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("New collection") },
                            onClick = {
                                headerMenuExpanded = false
                                viewModel.clearCollectionDialogError()
                                showCreateCollection = true
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            CollectionShelf(
                collections = uiState.collections,
                totalCount = uiState.totalLinkCount,
                selectedCollectionId = uiState.selectedCollectionId,
                onSelect = viewModel::selectCollection,
                menuFor = collectionMenuFor,
                onMenuChange = { collectionMenuFor = it },
                onRename = { item ->
                    collectionMenuFor = null
                    viewModel.clearCollectionDialogError()
                    editingCollection = item
                },
                onDelete = { item ->
                    collectionMenuFor = null
                    pendingDelete = item
                }
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
                                    .firstOrNull { it.collection.id == link.collectionId }
                                    ?.collection?.name
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
                                onMove = { movingLink = link },
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
            initialCollectionId = uiState.selectedCollectionId
                ?: uiState.collections.firstOrNull()?.collection?.id,
            collections = uiState.collections.map { it.collection },
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
            collections = uiState.collections.map { it.collection },
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

    movingLink?.let { link ->
        MoveLinkDialog(
            linkTitle = link.title,
            collections = uiState.collections,
            currentCollectionId = link.collectionId,
            onDismiss = { movingLink = null },
            onPick = { targetId ->
                viewModel.moveLink(link, targetId)
                movingLink = null
            }
        )
    }

    if (showCreateCollection) {
        CollectionEditorDialog(
            dialogTitle = "New collection",
            initialName = "",
            initialColorHex = CollectionVisuals.palette.first(),
            initialIcon = "folder",
            isSaving = uiState.isSavingCollection,
            errorMessage = uiState.collectionDialogError,
            confirmLabel = "Create",
            onDismiss = {
                viewModel.clearCollectionDialogError()
                showCreateCollection = false
            },
            onConfirm = { name, colorHex, icon ->
                viewModel.createCollection(name, colorHex, icon)
            }
        )
    }

    editingCollection?.let { item ->
        CollectionEditorDialog(
            dialogTitle = "Edit collection",
            initialName = item.collection.name,
            initialColorHex = item.collection.colorHex,
            initialIcon = item.collection.icon,
            isSaving = uiState.isSavingCollection,
            errorMessage = uiState.collectionDialogError,
            confirmLabel = "Save",
            onDismiss = {
                viewModel.clearCollectionDialogError()
                editingCollection = null
            },
            onConfirm = { name, colorHex, icon ->
                viewModel.renameCollection(item.collection.id, name, colorHex, icon)
            }
        )
    }

    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete \"${item.collection.name}\"?") },
            text = {
                Text(
                    text = if (item.linkCount == 0) {
                        "This collection is empty and will be removed."
                    } else {
                        "Its ${item.linkCount} saved links will move to Inbox. " +
                            "No links will be deleted."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCollection(item.collection.id, item.collection.name)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun CollectionShelf(
    collections: List<CollectionWithCount>,
    totalCount: Int,
    selectedCollectionId: Long?,
    onSelect: (Long?) -> Unit,
    menuFor: Long?,
    onMenuChange: (Long?) -> Unit,
    onRename: (CollectionWithCount) -> Unit,
    onDelete: (CollectionWithCount) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(end = 4.dp)
    ) {
        item(key = "all") {
            CollectionCard(
                name = "All",
                count = totalCount,
                color = LifeInboxColors.PaleSage,
                icon = Icons.Filled.Apps,
                selected = selectedCollectionId == null,
                onClick = { onSelect(null) },
                onLongClick = null
            )
        }
        items(collections, key = { it.collection.id }) { item ->
            Box {
                CollectionCard(
                    name = item.collection.name,
                    count = item.linkCount,
                    color = CollectionVisuals.colorFromHex(item.collection.colorHex),
                    icon = CollectionVisuals.iconFor(item.collection.icon),
                    selected = selectedCollectionId == item.collection.id,
                    onClick = { onSelect(item.collection.id) },
                    onLongClick = { onMenuChange(item.collection.id) }
                )
                DropdownMenu(
                    expanded = menuFor == item.collection.id,
                    onDismissRequest = { onMenuChange(null) }
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        leadingIcon = {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                        },
                        onClick = { onRename(item) }
                    )
                    if (!item.collection.isDefault) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = {
                                Icon(Icons.Filled.Delete, contentDescription = null)
                            },
                            onClick = { onDelete(item) }
                        )
                    }
                }
            }
        }
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
