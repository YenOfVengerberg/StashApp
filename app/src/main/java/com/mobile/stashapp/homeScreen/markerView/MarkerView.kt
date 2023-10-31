package com.mobile.stashapp.homeScreen.markerView

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.mobile.stashapp.data.model.Marker
import com.mobile.stashapp.uiComponents.StashLoader
import kotlinx.coroutines.flow.collect
import kotlin.math.absoluteValue

@UnstableApi
@Composable
fun MarkerViewRoute(
    modifier: Modifier = Modifier,
    markersViewModel: MarkersViewModel = hiltViewModel()
) {
    val uiState by markersViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = markersViewModel, block = {
        markersViewModel.init()
    })

    MarkerView(
        uiState = uiState,
        apiKey = markersViewModel.apiKey ?: "",
        modifier = modifier,
        onLoadMore = {
            markersViewModel.loadMore()
        }
    )
}

@UnstableApi
@Composable
private fun MarkerView(
    uiState: UiState,
    apiKey: String,
    modifier: Modifier,
    onLoadMore: () -> Unit
) {

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (uiState) {
            is UiState.Loading -> {
                StashLoader(modifier)
            }
            is UiState.NothingToShow -> {
                Text(
                    "No markers to show",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is UiState.Markers -> {
                MarkersPager(
                    listOfMarkers = uiState.list,
                    isNextPageAvailable = uiState.isNextPageAvailable,
                    apiKey = apiKey,
                    modifier = modifier,
                    loadMore = onLoadMore
                )
            }
        }
    }

}

@UnstableApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MarkersPager(
    listOfMarkers: List<Marker>,
    isNextPageAvailable: Boolean,
    apiKey: String,
    modifier: Modifier,
    loadMore: () -> Unit
) {
    val pagerState = rememberPagerState()

    LaunchedEffect(key1 = pagerState, listOfMarkers, block = {
        snapshotFlow { pagerState.currentPage }.collect {
            if (it == listOfMarkers.size) {
                loadMore()
            }
        }
    })

    VerticalPager(
        pageCount = listOfMarkers.size + 1,
        state = pagerState
    ) { page ->

        val shouldPlay by remember(pagerState) {
            derivedStateOf {
                (pagerState.currentPageOffsetFraction.absoluteValue < .75 && pagerState.currentPage == page) ||
                        (pagerState.currentPageOffsetFraction > 0.75 && pagerState.targetPage == page)
            }
        }

        val isLastPage = remember(listOfMarkers) {
            listOfMarkers.size == page
        }

        if (isLastPage) {
            if (isNextPageAvailable) {
                StashLoader()
            } else {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = "No more markers to show",
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Box {
                MarkerPlayer(
                    streamUrl = listOfMarkers[page].stream,
                    apiKey = apiKey,
                    shouldPlay = shouldPlay
                )

                MarkerInfo(marker = listOfMarkers[page], modifier = Modifier.fillMaxWidth().align(
                    Alignment.BottomStart
                ))
            }
        }

    }
}

@Composable
fun MarkerInfo(
    marker: Marker,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = marker.title,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Text(
            text = marker.sceneName,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row {
            marker.tags.forEach {
                Text(
                    text = "#${it.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(horizontal = 3.dp))
            }
        }
    }
}

@UnstableApi
@Composable
private fun MarkerPlayer(
    streamUrl: String,
    apiKey: String,
    shouldPlay: Boolean
) {
    val exoPlayer = rememberExoPlayerWithLifecycle(streamUrl = streamUrl, apiKey = apiKey)
    val playerView = rememberPlayerView(exoPlayer = exoPlayer)

    Box {
        AndroidView(factory = { playerView }) {
            exoPlayer.playWhenReady = shouldPlay
        }
    }

    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            exoPlayer.release()
        }
    })
}


@UnstableApi
@Composable
fun rememberPlayerView(exoPlayer: ExoPlayer): PlayerView {
    val context = LocalContext.current
    val bgColor = MaterialTheme.colorScheme.surface.toArgb()
    val playerView = remember {
        PlayerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player = exoPlayer
            setShowBuffering(SHOW_BUFFERING_ALWAYS)
            setShutterBackgroundColor(bgColor)
            setBackgroundColor(bgColor)
        }
    }
    DisposableEffect(key1 = true) {
        onDispose {
            playerView.player = null
        }
    }
    return playerView
}


@Composable
@UnstableApi
fun rememberExoPlayerWithLifecycle(
    streamUrl: String,
    apiKey: String
): ExoPlayer {

    val context = LocalContext.current
    val exoPlayer = remember(streamUrl) {
        ExoPlayer.Builder(context).build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
            repeatMode = Player.REPEAT_MODE_ONE
            setHandleAudioBecomingNoisy(true)
            val defaultDataSource = DefaultHttpDataSource.Factory()
            defaultDataSource.setDefaultRequestProperties(HashMap<String, String>().apply {
                put("ApiKey", apiKey)
            })
            val source = ProgressiveMediaSource.Factory(defaultDataSource)
                .createMediaSource(MediaItem.fromUri(streamUrl))
            setMediaSource(source)
            prepare()
        }
    }
    var appInBackground by remember {
        mutableStateOf(false)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, appInBackground, effect = {
        val lifecycleObserver = getExoPlayerLifecycleObserver(exoPlayer, appInBackground) {
            appInBackground = it
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    })

    return exoPlayer
}

private fun getExoPlayerLifecycleObserver(
    exoPlayer: ExoPlayer,
    wasAppInBackground: Boolean,
    setWasAppInBackground: (Boolean) -> Unit
): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (wasAppInBackground)
                    exoPlayer.playWhenReady = true
                setWasAppInBackground(false)
            }
            Lifecycle.Event.ON_PAUSE -> {
                exoPlayer.playWhenReady = false
                setWasAppInBackground(true)
            }
            Lifecycle.Event.ON_STOP -> {
                exoPlayer.playWhenReady = false
                setWasAppInBackground(true)
            }
            Lifecycle.Event.ON_DESTROY -> {
                exoPlayer.release()
            }
            else -> {}
        }
    }