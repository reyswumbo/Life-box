package com.reyswumbo.lifeinbox.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity
import com.reyswumbo.lifeinbox.data.repository.CollectionRepository
import com.reyswumbo.lifeinbox.data.repository.LinkRepository
import com.reyswumbo.lifeinbox.data.repository.SaveLinkResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val url: String = "",
    val selectedCollectionId: Long? = null,
    val collections: List<CollectionEntity> = emptyList(),
    val dailyCount: Int = 0,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val message: String = "Take your time."
)

sealed interface HomeEvent {
    data class Saved(val title: String, val collectionName: String) : HomeEvent
    data class Failed(val reason: String) : HomeEvent
}

class HomeViewModel(
    private val linkRepository: LinkRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            combine(
                collectionRepository.observeCollections(),
                linkRepository.observeTodayCount()
            ) { collections, todayCount ->
                collections to todayCount
            }.catch { }
                .collect { (collections, todayCount) ->
                    _uiState.update {
                        it.copy(collections = collections, dailyCount = todayCount)
                    }
                }
        }
    }

    fun onUrlChange(value: String) {
        _uiState.update { it.copy(url = value, errorMessage = null) }
    }

    fun onCollectionSelected(id: Long?) {
        _uiState.update { it.copy(selectedCollectionId = id) }
    }

    fun submit() {
        val current = _uiState.value
        if (current.isSaving) return
        if (current.url.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Paste a link to save.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = linkRepository.addLink(current.url, null, "", current.selectedCollectionId)) {
                is SaveLinkResult.Success -> {
                    val collectionName = current.collections
                        .firstOrNull { it.id == current.selectedCollectionId }
                        ?.name
                        ?: "Inbox"
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            url = "",
                            message = "Yum! Your link is safe.",
                            errorMessage = null
                        )
                    }
                    _events.send(HomeEvent.Saved("Saved link", collectionName))
                }

                is SaveLinkResult.Duplicate -> {
                    val reason = "That link is already saved as \"${result.existing.title}\"."
                    _uiState.update { it.copy(isSaving = false, errorMessage = reason) }
                    _events.send(HomeEvent.Failed(reason))
                }

                is SaveLinkResult.InvalidUrl -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.reason) }
                    _events.send(HomeEvent.Failed(result.reason))
                }

                is SaveLinkResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                    _events.send(HomeEvent.Failed(result.message))
                }
            }
        }
    }
}

class HomeViewModelFactory(
    private val linkRepository: LinkRepository,
    private val collectionRepository: CollectionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(linkRepository, collectionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
