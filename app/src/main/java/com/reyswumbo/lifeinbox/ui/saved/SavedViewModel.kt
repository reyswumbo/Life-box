package com.reyswumbo.lifeinbox.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reyswumbo.lifeinbox.data.local.CollectionWithCount
import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity
import com.reyswumbo.lifeinbox.data.repository.CollectionRepository
import com.reyswumbo.lifeinbox.data.repository.CollectionResult
import com.reyswumbo.lifeinbox.data.repository.LinkRepository
import com.reyswumbo.lifeinbox.data.repository.SaveLinkResult
import com.reyswumbo.lifeinbox.domain.LinkSortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SavedUiState(
    val query: String = "",
    val sort: LinkSortOption = LinkSortOption.Newest,
    val collections: List<CollectionWithCount> = emptyList(),
    val selectedCollectionId: Long? = null,
    val links: List<LinkEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val dialogError: String? = null,
    val isSavingCollection: Boolean = false,
    val collectionDialogError: String? = null
) {
    val totalLinkCount: Int get() = collections.sumOf { it.linkCount }
}

sealed interface SavedEvent {
    data object LinkSaved : SavedEvent
    data object LinkUpdated : SavedEvent
    data object CollectionSaved : SavedEvent
    data class CollectionDeleted(val name: String) : SavedEvent
    data class Message(val text: String) : SavedEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class SavedViewModel(
    private val linkRepository: LinkRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val sort = MutableStateFlow(LinkSortOption.Newest)
    private val selectedCollectionId = MutableStateFlow<Long?>(null)

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    private val _events = Channel<SavedEvent>(Channel.BUFFERED)
    val events: Flow<SavedEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                query.flatMapLatest { linkRepository.observeLinks(it) },
                sort,
                collectionRepository.observeCollectionsWithCount(),
                selectedCollectionId
            ) { links, sortOption, collections, selectedId ->
                val filtered = if (selectedId == null) {
                    links
                } else {
                    links.filter { it.collectionId == selectedId }
                }
                SavedUiState(
                    collections = collections,
                    selectedCollectionId = selectedId,
                    links = sortOption.sort(filtered),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.update { current ->
                    current.copy(
                        collections = state.collections,
                        selectedCollectionId = state.selectedCollectionId,
                        links = state.links,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
        _uiState.update { it.copy(query = value) }
    }

    fun onSortChange(value: LinkSortOption) {
        sort.value = value
        _uiState.update { it.copy(sort = value) }
    }

    fun selectCollection(collectionId: Long?) {
        selectedCollectionId.value = collectionId
        _uiState.update { it.copy(selectedCollectionId = collectionId) }
    }

    fun clearDialogError() {
        _uiState.update { it.copy(dialogError = null) }
    }

    fun clearCollectionDialogError() {
        _uiState.update { it.copy(collectionDialogError = null) }
    }

    fun saveLink(rawUrl: String, title: String, notes: String, collectionId: Long?) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, dialogError = null) }
            when (val result = linkRepository.addLink(rawUrl, title, notes, collectionId)) {
                is SaveLinkResult.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(SavedEvent.LinkSaved)
                    _events.send(SavedEvent.Message("Saved to your library."))
                }

                is SaveLinkResult.Duplicate -> _uiState.update {
                    it.copy(
                        isSaving = false,
                        dialogError = "That link is already saved as \"${result.existing.title}\"."
                    )
                }

                is SaveLinkResult.InvalidUrl -> _uiState.update {
                    it.copy(isSaving = false, dialogError = result.reason)
                }

                is SaveLinkResult.Error -> _uiState.update {
                    it.copy(isSaving = false, dialogError = result.message)
                }
            }
        }
    }

    fun updateLink(id: Long, title: String, notes: String, collectionId: Long) {
        if (_uiState.value.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, dialogError = null) }
            when (val result = linkRepository.updateLink(id, title, notes, collectionId)) {
                is SaveLinkResult.Success -> {
                    _uiState.update { it.copy(isSaving = false) }
                    _events.send(SavedEvent.LinkUpdated)
                    _events.send(SavedEvent.Message("Link updated."))
                }

                is SaveLinkResult.Error -> _uiState.update {
                    it.copy(isSaving = false, dialogError = result.message)
                }

                else -> _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun moveLink(link: LinkEntity, collectionId: Long) {
        viewModelScope.launch {
            when (val result = linkRepository.moveLink(link.id, collectionId)) {
                is SaveLinkResult.Success -> _events.send(SavedEvent.Message("Link moved."))
                is SaveLinkResult.Error -> _events.send(SavedEvent.Message(result.message))
                else -> _events.send(SavedEvent.Message("Could not move link."))
            }
        }
    }

    fun deleteLink(link: LinkEntity) {
        viewModelScope.launch {
            val deleted = linkRepository.deleteLink(link.id)
            _events.send(
                SavedEvent.Message(if (deleted) "Link deleted." else "Could not delete link.")
            )
        }
    }

    fun toggleFavorite(link: LinkEntity) {
        viewModelScope.launch {
            linkRepository.toggleFavorite(link.id, !link.isFavorite)
        }
    }

    fun createCollection(name: String, colorHex: String, icon: String) {
        if (_uiState.value.isSavingCollection) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingCollection = true, collectionDialogError = null) }
            when (val result = collectionRepository.createCollection(name, colorHex, icon)) {
                is CollectionResult.Success -> {
                    _uiState.update { it.copy(isSavingCollection = false) }
                    _events.send(SavedEvent.CollectionSaved)
                    _events.send(SavedEvent.Message("Collection created."))
                }

                is CollectionResult.DuplicateName -> _uiState.update {
                    it.copy(
                        isSavingCollection = false,
                        collectionDialogError = "A collection with that name already exists."
                    )
                }

                is CollectionResult.Invalid -> _uiState.update {
                    it.copy(isSavingCollection = false, collectionDialogError = result.reason)
                }

                is CollectionResult.Error -> _uiState.update {
                    it.copy(isSavingCollection = false, collectionDialogError = result.message)
                }
            }
        }
    }

    fun renameCollection(id: Long, name: String, colorHex: String, icon: String) {
        if (_uiState.value.isSavingCollection) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingCollection = true, collectionDialogError = null) }
            when (val result = collectionRepository.renameCollection(id, name, colorHex, icon)) {
                is CollectionResult.Success -> {
                    _uiState.update { it.copy(isSavingCollection = false) }
                    _events.send(SavedEvent.CollectionSaved)
                    _events.send(SavedEvent.Message("Collection updated."))
                }

                is CollectionResult.DuplicateName -> _uiState.update {
                    it.copy(
                        isSavingCollection = false,
                        collectionDialogError = "A collection with that name already exists."
                    )
                }

                is CollectionResult.Invalid -> _uiState.update {
                    it.copy(isSavingCollection = false, collectionDialogError = result.reason)
                }

                is CollectionResult.Error -> _uiState.update {
                    it.copy(isSavingCollection = false, collectionDialogError = result.message)
                }
            }
        }
    }

    fun deleteCollection(id: Long, name: String) {
        viewModelScope.launch {
            when (val result = collectionRepository.deleteCollection(id)) {
                is CollectionResult.Success -> {
                    if (_uiState.value.selectedCollectionId == id) {
                        selectCollection(null)
                    }
                    _events.send(SavedEvent.CollectionDeleted(name))
                    _events.send(SavedEvent.Message("Collection deleted. Links moved to Inbox."))
                }

                is CollectionResult.Invalid -> _events.send(SavedEvent.Message(result.reason))
                is CollectionResult.Error -> _events.send(SavedEvent.Message(result.message))
                CollectionResult.DuplicateName -> Unit
            }
        }
    }
}

class SavedViewModelFactory(
    private val linkRepository: LinkRepository,
    private val collectionRepository: CollectionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedViewModel::class.java)) {
            return SavedViewModel(linkRepository, collectionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
