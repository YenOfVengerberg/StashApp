package com.mobile.stashapp.homeScreen.homeView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.mobile.stashapp.data.model.SceneThumb
import com.mobile.stashapp.uiComponents.StashLoader

@Composable
fun HomeViewRoute(
    modifier: Modifier = Modifier,
    navigateToVidDetail: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = homeViewModel, block = {
        homeViewModel.init()
    })

    HomeView(modifier = modifier, uiState = uiState, navigateToVidDetail = navigateToVidDetail, apiKey = homeViewModel.apiKey)
}

@Composable
private fun HomeView(
    modifier: Modifier,
    navigateToVidDetail: (String) -> Unit,
    uiState: HomeViewUiState,
    apiKey: String
) {

    val context = LocalContext.current

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        when (uiState) {
            is HomeViewUiState.Loading -> {
                StashLoader()
            }
            is HomeViewUiState.NoData -> {
                Text(
                    text = "Nothing to show",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            is HomeViewUiState.ContentList -> {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth().padding(top = 10.dp)) {
                    items(uiState.items) {
                        Card(modifier = Modifier
                            .clickable {
                                navigateToVidDetail(it.id)
                            }
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)) {
                            Column {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).addHeader("ApiKey", apiKey)
                                        .data(it.screenshot)
                                        .scale(Scale.FILL)
                                        .build(),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp),
                                    contentScale = ContentScale.FillBounds
                                )

                                Text(
                                    text = it.title.toUpperCase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(8.dp)
                                )

                                val performers by remember {
                                    var temp = ""
                                    it.performers.forEachIndexed { index, performerName ->
                                        temp += performerName.name
                                        if (index != it.performers.size - 1) {
                                            temp += ", "
                                        }
                                    }
                                    mutableStateOf(temp)
                                }

                                Row {
                                    it.performers.forEach {
                                        Text(
                                            text = performers,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                                        )
                                    }
                                }

                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
                                    Text(
                                        text = it.date,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}