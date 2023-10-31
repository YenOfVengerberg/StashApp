package com.mobile.stashapp.homeScreen.homeView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.stashapp.ServerPreference
import com.mobile.stashapp.data.model.UiConfig
import com.mobile.stashapp.data.model.Modes
import com.mobile.stashapp.data.model.SceneThumb
import com.mobile.stashapp.data.model.SortBy
import com.mobile.stashapp.data.model.SortDirection
import com.mobile.stashapp.data.model.SortType
import com.mobile.stashapp.data.model.UiMode
import com.mobile.stashapp.data.repositories.ScenesRepository
import com.mobile.stashapp.data.repositories.UiConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scenesRepository: ScenesRepository,
    private val serverPreference: ServerPreference
): ViewModel() {

    private val _uiState = MutableStateFlow<HomeViewUiState>(HomeViewUiState.Loading)
    val uiState = _uiState.asStateFlow()

    val apiKey = serverPreference.apiKey ?: ""

    fun init() {
        viewModelScope.launch {

            val scenes = scenesRepository.findScene(1, SortBy(SortType.ADDED_TIME, SortDirection.DESC))
            if (scenes.isNotEmpty()) {
                _uiState.update {
                    HomeViewUiState.ContentList(scenes)
                }
            } else {
                _uiState.update {
                    HomeViewUiState.NoData
                }
            }
        }

    }

}

sealed interface HomeViewUiState {
    object Loading: HomeViewUiState
    object NoData: HomeViewUiState
    data class ContentList(val items: List<SceneThumb>): HomeViewUiState
}