package com.mobile.stashapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mobile.stashapp.homeScreen.homeView
import com.mobile.stashapp.homeScreen.navigateToHome
import com.mobile.stashapp.setup.SetupViewRoute
import com.mobile.stashapp.vidDetailScreen.navigateToVidDetail
import com.mobile.stashapp.vidDetailScreen.vidDetailView

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "setup") {
        composable("setup") {
            SetupViewRoute(navToHome = {
                navController.navigateToHome()
            })
        }
        homeView(navigateToVidDetail = {
            navController.navigateToVidDetail(it)
        })

        vidDetailView()
    }
}