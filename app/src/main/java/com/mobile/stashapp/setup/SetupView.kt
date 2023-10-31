package com.mobile.stashapp.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobile.stashapp.theme.StashAppTheme
import com.mobile.stashapp.uiComponents.StashLoader
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SetupViewRoute(
    navToHome: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = viewModel, block = {
        viewModel.init()
    })

    LaunchedEffect(key1 = uiState, block = {
        if (uiState is UiState.NavToHome) {
            navToHome()
        }
    })

    SetupView(
        uiState = uiState,
        host = viewModel.hostEditable,
        port = viewModel.portEditable,
        apiKey = viewModel.apiKeyEditable,
        onUiEvent = {
            viewModel.uiEvent(it)
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupView(
    uiState: UiState,
    host: String,
    port: String,
    apiKey: String,
    onUiEvent: (UiEvent) -> Unit
) {

    Scaffold {

        when (uiState) {
            is UiState.ConnectionForm -> {
                ConnectToServerForm(
                    host, port, apiKey, onUiEvent,
                    modifier = Modifier
                        .padding(it)
                        .padding(20.dp)
                )
            }
            else -> {
                StashLoader(modifier = Modifier.padding(it))
            }
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectToServerForm(
    host: String,
    port: String,
    apiKey: String,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        Text(
            text = "Connect to Server",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = host,
            onValueChange = { onUiEvent(UiEvent.OnHostUpdate(it)) },
            singleLine = true,
            label = {
                Text(text = "Host", style = MaterialTheme.typography.labelMedium)
            },
            supportingText = {
                Text(
                    text = "http://192.168.1.100 or http://myserver.local",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = port,
            onValueChange = { onUiEvent(UiEvent.OnPortUpdate(it)) },
            singleLine = true,
            label = {
                Text(text = "Port:", style = MaterialTheme.typography.labelMedium)
            }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = apiKey,
            onValueChange = { onUiEvent(UiEvent.OnApiKeyUpdate(it)) },
            singleLine = true,
            label = {
                Text(text = "API Key", style = MaterialTheme.typography.labelMedium)
            },
            supportingText = {
                Text(
                    text = "Leave blank if authentication not available.",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        FilledIconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            onUiEvent(UiEvent.OnConnect)
        }) {
            Text(text = "Connect", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview
@Composable
fun ConnectToServerFormPreview() {
    StashAppTheme(darkTheme = true) {
        SetupView(
            uiState = UiState.ConnectionForm,
            host = "",
            port = "",
            apiKey = "",
            onUiEvent = {}
        )
    }
}