package com.reyswumbo.lifeinbox.ui.petlab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.reyswumbo.lifeinbox.data.repository.PetConfigRepository
import com.reyswumbo.lifeinbox.domain.pet.Accessory
import com.reyswumbo.lifeinbox.domain.pet.BodyShape
import com.reyswumbo.lifeinbox.domain.pet.EyeStyle
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.domain.pet.PetTexture
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PetLabCategory { BODY, EYES, ACCESSORIES, COLORS, TEXTURES }

data class PetLabUiState(
    val draft: PetConfig = PetConfig.Default,
    val category: PetLabCategory = PetLabCategory.BODY,
    val isSaving: Boolean = false
)

sealed interface PetLabEvent {
    data class Message(val text: String) : PetLabEvent
}

class PetLabViewModel(
    private val petConfigRepository: PetConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetLabUiState())
    val uiState: StateFlow<PetLabUiState> = _uiState.asStateFlow()

    private val _events = Channel<PetLabEvent>(Channel.BUFFERED)
    val events: Flow<PetLabEvent> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            petConfigRepository.observeConfig().collect { config ->
                _uiState.update { it.copy(draft = config) }
            }
        }
    }

    fun selectCategory(category: PetLabCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onNameChange(value: String) {
        if (value.length > 24) return
        _uiState.update { it.copy(draft = it.draft.copy(name = value)) }
    }

    fun onBodyShape(shape: BodyShape) {
        _uiState.update { it.copy(draft = it.draft.copy(bodyShape = shape)) }
    }

    fun onEyeStyle(style: EyeStyle) {
        _uiState.update { it.copy(draft = it.draft.copy(eyeStyle = style)) }
    }

    fun onAccessory(accessory: Accessory) {
        _uiState.update { it.copy(draft = it.draft.copy(accessory = accessory)) }
    }

    fun onTexture(texture: PetTexture) {
        _uiState.update { it.copy(draft = it.draft.copy(texture = texture)) }
    }

    fun onBodyColor(hex: String) {
        _uiState.update { it.copy(draft = it.draft.copy(bodyColorHex = hex)) }
    }

    fun onEyeColor(hex: String) {
        _uiState.update { it.copy(draft = it.draft.copy(eyeColorHex = hex)) }
    }

    fun onAccentColor(hex: String) {
        _uiState.update { it.copy(draft = it.draft.copy(accentColorHex = hex)) }
    }

    fun save() {
        val current = _uiState.value
        if (current.isSaving) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val trimmed = current.draft.name.trim()
            val saved = current.draft.copy(name = if (trimmed.isEmpty()) "Pocket Pal" else trimmed)
            petConfigRepository.saveConfig(saved)
            _uiState.update {
                it.copy(draft = saved, isSaving = false)
            }
            _events.send(PetLabEvent.Message("${saved.name} saved!"))
        }
    }
}

class PetLabViewModelFactory(
    private val petConfigRepository: PetConfigRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetLabViewModel::class.java)) {
            return PetLabViewModel(petConfigRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}