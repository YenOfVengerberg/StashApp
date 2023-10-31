package com.mobile.stashapp.setup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.stashapp.ServerPreference
import com.mobile.stashapp.data.model.SystemStatus
import com.mobile.stashapp.data.repositories.SystemStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val systemStatusRepository: SystemStatusRepository,
    private val serverPreference: ServerPreference
): ViewModel() {

    companion object {
        const val DEFAULT_PORT = "9999"
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    var hostEditable by mutableStateOf("")
        private set

    var portEditable by mutableStateOf(DEFAULT_PORT)
        private set

    var apiKeyEditable by mutableStateOf("")
        private set


    fun init() {
        val baseUrl = serverPreference.getBaseUrl()
        if (baseUrl == null) {
            _uiState.update {
                UiState.ConnectionForm
            }
        } else {
            viewModelScope.launch {
                val status = systemStatusRepository.getSystemStatus()
                if (status == SystemStatus.OK) {
                    _uiState.update {
                        UiState.NavToHome
                    }
                } else {
                    Log.d("system", "error")
                }
            }
        }
    }

    fun uiEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            is UiEvent.OnHostUpdate -> {
                onHostUpdate(uiEvent.host)
            }
            is UiEvent.OnPortUpdate -> {
                onPortUpdate(uiEvent.port)
            }
            is UiEvent.OnApiKeyUpdate -> {
                onApiKeyUpdate(uiEvent.apiKey)
            }
            is UiEvent.OnConnect -> {
                viewModelScope.launch {
                    _uiState.update {
                        UiState.Loading
                    }
                    serverPreference.apply {
                        host = hostEditable
                        port = portEditable
                        apiKey = apiKeyEditable
                    }

                    val status = systemStatusRepository.getSystemStatus()
                    if (status == SystemStatus.OK) {
                        _uiState.update {
                            UiState.NavToHome
                        }
                    } else {
                        Log.d("system", "error")
                    }
                }

            }
        }
    }

    private fun onHostUpdate(host: String) {
        this.hostEditable = host
    }

    private fun onPortUpdate(port: String) {
        this.portEditable = port
    }

    private fun onApiKeyUpdate(apiKey: String) {
        this.apiKeyEditable = apiKey
    }

}


sealed interface UiState {
    object Loading: UiState
    object ConnectionForm: UiState
    object NavToHome: UiState
}

sealed interface UiEvent {

    data class OnHostUpdate(val host: String): UiEvent
    data class OnPortUpdate(val port: String): UiEvent
    data class OnApiKeyUpdate(val apiKey: String): UiEvent

    object OnConnect: UiEvent

}