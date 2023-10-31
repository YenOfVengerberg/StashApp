package com.mobile.stashapp.vidDetailScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.stashapp.ServerPreference
import com.mobile.stashapp.data.model.Scene
import com.mobile.stashapp.data.repositories.ScenesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VidDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scenesRepository: ScenesRepository,
    private val serverPreference: ServerPreference
): ViewModel() {

    private val vidId: String = checkNotNull(savedStateHandle["vidId"])

    private val _uiState = MutableStateFlow<VidDetUiState>(VidDetUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val apiKey = serverPreference.apiKey ?: ""

    fun init() {
        viewModelScope.launch {
            val scene = scenesRepository.findSceneWithId(vidId)
            if (scene == null) {
                _uiState.update {
                    VidDetUiState.Error
                }
            } else {
                _uiState.update {
                    VidDetUiState.Content(scene)
                }
            }
        }
    }

}

sealed interface VidDetUiState {
    object Loading: VidDetUiState
    object Error: VidDetUiState
    data class Content(val scene: Scene): VidDetUiState
}