package com.mobile.stashapp.vidDetailScreen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.mobile.stashapp.uiComponents.StashLoader

@UnstableApi
@Composable
fun VidDetailRoute(
    modifier: Modifier = Modifier,
    vidDetailViewModel: VidDetailViewModel = hiltViewModel()
) {
    val uiState by vidDetailViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = vidDetailViewModel, block = {
        vidDetailViewModel.init()
    })

    VidDetailView(uiState = uiState, apiKey = vidDetailViewModel.apiKey ?: "", modifier = modifier)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@UnstableApi
@Composable
private fun VidDetailView(
    uiState: VidDetUiState,
    apiKey: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    Scaffold {
        Box(modifier = modifier.padding(it), contentAlignment = Alignment.Center) {
            when (uiState) {
                is VidDetUiState.Loading -> {
                    StashLoader(modifier)
                }
                is VidDetUiState.Error -> {
                    Text(
                        "Something went wrong",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is VidDetUiState.Content -> {
                    Column {
                        val streamUrl = remember {
                            uiState.scene.streams.find {
                                it.label == "Direct stream"
                            }?.url
                        }

                        val markers = remember {
                            uiState.scene.markers.map {
                                it.seconds * 1000L
                            }.toLongArray()
                        }
                        VidPlayer(
                            modifier = Modifier.weight(0.4F),
                            streamUrl = streamUrl ?: "",
                            markers = markers,
                            apiKey = apiKey
                        )
                        if (LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE) {
                            Column(modifier = Modifier
                                .weight(0.6F)
                                .padding(bottom = 20.dp)
                                .verticalScroll(rememberScrollState())
                            ) {
                                Spacer(modifier = Modifier.padding(10.dp))
                                Text(
                                    text = uiState.scene.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.padding(3.dp))
                                Text(
                                    text = uiState.scene.date,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                uiState.scene.studio?.let {
                                    Spacer(modifier = Modifier.padding(3.dp))
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .alpha(0.8F)
                                    )
                                }

                                Spacer(modifier = Modifier.padding(3.dp))
                                Text(
                                    text = uiState.scene.details,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .alpha(0.8F)
                                )
                                Spacer(modifier = Modifier.padding(5.dp))
                                FlowRow(modifier = Modifier.padding(horizontal = 10.dp)) {
                                    uiState.scene.performers.forEachIndexed { index, performer ->
                                        val text = remember {
                                            if (index == uiState.scene.performers.size - 1) {
                                                "${performer.name}"
                                            } else {
                                                "${performer.name},"
                                            }
                                        }
                                        Text(
                                            text = text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(start = 5.dp),
                                            textDecoration = TextDecoration.Underline,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.padding(10.dp))
                                if (uiState.scene.markers.isNotEmpty()) {
                                    LazyRow(modifier = Modifier.padding(horizontal = 10.dp)) {
                                        items(uiState.scene.markers) {
                                            Card(modifier = Modifier
                                                .width(150.dp)
                                                .wrapContentHeight()
                                                .padding(end = 20.dp)
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 5.dp)) {
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(context).addHeader("ApiKey", apiKey)
                                                            .data(it.screenshot)
                                                            .scale(Scale.FILL)
                                                            .build(),
                                                        contentDescription = "",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(100.dp),
                                                        contentScale = ContentScale.Fit
                                                    )

                                                    Spacer(modifier = Modifier.padding(3.dp))


                                                    Text(
                                                        text = it.title,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        fontWeight = FontWeight.SemiBold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    Spacer(modifier = Modifier.padding(3.dp))

                                                    Text(
                                                        text = (it.seconds / 60F).toString(),
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.padding(start = 5.dp),
                                                        fontWeight = FontWeight.SemiBold
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
            }
        }
    }
}


@UnstableApi
@Composable
private fun VidPlayer(
    modifier: Modifier,
    streamUrl: String,
    markers: LongArray,
    apiKey: String
) {
    val exoPlayer = rememberExoPlayer(streamUrl = streamUrl, apiKey = apiKey)
    val playerView = rememberPlayerView(exoPlayer = exoPlayer, markers = markers)

    AndroidView(modifier = modifier, factory = { playerView }) {
    }

    DisposableEffect(key1 = Unit, effect = {
        onDispose {
            exoPlayer.release()
        }
    })
}

internal fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Activity.hideSystemUi() {
    window?.let {
        WindowCompat.setDecorFitsSystemWindows(it, false)
        WindowInsetsControllerCompat(it, it.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

fun Activity.showSystemUi() {
    window?.let {
        WindowCompat.setDecorFitsSystemWindows(it, true)
        WindowInsetsControllerCompat(it, it.decorView)
            .show(WindowInsetsCompat.Type.systemBars())
    }
}

@UnstableApi
@Composable
fun rememberPlayerView(
    exoPlayer: ExoPlayer,
    markers: LongArray
): PlayerView {
    val context = LocalContext.current
    val bgColor = MaterialTheme.colorScheme.surface.toArgb()
    val playerView = remember {
        PlayerView(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            useController = true
            setShowPreviousButton(false)
            setShowNextButton(false)
            setFullscreenButtonClickListener { isFullScreen ->
                context.findActivity()?.let {
                    if (isFullScreen) {
                        it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        it.hideSystemUi()
                    } else {
                        it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        it.showSystemUi()
                    }
                }
            }
            artworkDisplayMode
            setExtraAdGroupMarkers(markers, markers.map { false }.toBooleanArray())
            player = exoPlayer
            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)

//            setShutterBackgroundColor(bgColor)
//            setBackgroundColor(bgColor)
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
fun rememberExoPlayer(
    streamUrl: String,
    apiKey: String
): ExoPlayer {

    val context = LocalContext.current
    val exoPlayer = remember(streamUrl) {
        ExoPlayer.Builder(context).build().apply {
            videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
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

    return exoPlayer
}