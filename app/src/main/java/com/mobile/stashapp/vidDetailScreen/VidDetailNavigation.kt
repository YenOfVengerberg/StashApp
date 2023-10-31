package com.mobile.stashapp.vidDetailScreen

import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val vidDetailRoute = "vidDetailRoute"
private const val vidIdArg = "vidId"

fun NavController.navigateToVidDetail(vidId: String) {
    navigate("vidDetailRoute/$vidId")
}

@UnstableApi
fun NavGraphBuilder.vidDetailView() {
    composable("$vidDetailRoute/{$vidIdArg}") {
        VidDetailRoute()
    }
}