package com.mobile.stashapp.homeScreen.markerView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.stashapp.ServerPreference
import com.mobile.stashapp.data.model.Marker
import com.mobile.stashapp.data.repositories.MarkersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.nextInt

@HiltViewModel
class MarkersViewModel @Inject constructor(
    private val serverPreference: ServerPreference,
    private val markersRepository: MarkersRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    val apiKey = serverPreference.apiKey

    private val randomKey = kotlin.random.Random.nextInt(100000..999999)
    private var loadMoreJob: Job? = null
    private var page: Int = 1

    fun init() {
        viewModelScope.launch {
            val list = markersRepository.getRandomMarkers(page, randomKey)
            if (list.isEmpty()) {
                _uiState.update {
                    UiState.NothingToShow
                }
            } else {
                _uiState.update {
                    UiState.Markers(list)
                }
            }
        }
    }

    fun loadMore() {
        if (loadMoreJob?.isActive == true) {
            return
        }

        loadMoreJob = viewModelScope.launch {
            val list = markersRepository.getRandomMarkers(page + 1, randomKey)
            page += 1
            _uiState.update {
                if (it is UiState.Markers) {
                    val updatedMarkersList = ArrayList<Marker>()
                    updatedMarkersList.addAll(it.list)
                    updatedMarkersList.addAll(list)

                    UiState.Markers(
                        updatedMarkersList,
                        list.isNotEmpty()
                    )

                } else {
                    it
                }
            }
        }
    }

}

sealed interface UiState {
    object Loading: UiState
    data class Markers(
        val list: List<Marker>,
        val isNextPageAvailable: Boolean = true
    ): UiState
    object NothingToShow: UiState
}