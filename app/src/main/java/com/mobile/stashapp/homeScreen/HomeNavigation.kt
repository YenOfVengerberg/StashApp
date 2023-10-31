package com.mobile.stashapp.homeScreen

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val homeViewRoute = "homeRoute"

fun NavController.navigateToHome() {
    navigate(homeViewRoute)
}

fun NavGraphBuilder.homeView(navigateToVidDetail: (String) -> Unit) {
    composable(homeViewRoute) {
        HomeScreen(navigateToVidDetail = navigateToVidDetail)
    }
}