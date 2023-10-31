package com.mobile.stashapp.homeScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile.stashapp.R
import com.mobile.stashapp.homeScreen.homeView.HomeViewRoute
import com.mobile.stashapp.homeScreen.markerView.MarkerViewRoute
import com.mobile.stashapp.homeScreen.searchView.SearchViewRoute

enum class BottomNavItems(val route: String) {
    HOME_VIEW("home"),
    SEARCH_VIEW("search"),
    MARKERS_VIEW("markers")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@UnstableApi
fun HomeScreen(
    navigateToVidDetail: (String) -> Unit
) {
    val bottomNavController = rememberNavController()

    var selectedItem by remember {
        mutableStateOf(BottomNavItems.HOME_VIEW)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedItem == BottomNavItems.HOME_VIEW,
                    onClick = {
                        selectedItem = BottomNavItems.HOME_VIEW
                        bottomNavController.navigate(BottomNavItems.HOME_VIEW.route) {
                            popUpTo(BottomNavItems.HOME_VIEW.route)
                        }
                    },
                    icon = {
                          Icon(
                              painter = painterResource(id = R.drawable.home),
                              contentDescription = "Home"
                          )
                    },
                    label = {
                        Text(text = "Home")
                    }
                )
                NavigationBarItem(
                    selected = selectedItem == BottomNavItems.SEARCH_VIEW,
                    onClick = {
                        selectedItem = BottomNavItems.SEARCH_VIEW
                        bottomNavController.navigate(BottomNavItems.SEARCH_VIEW.route) {
                            popUpTo(BottomNavItems.SEARCH_VIEW.route)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search"
                        )
                    },
                    label = {
                        Text(text = "Search")
                    }
                )
                NavigationBarItem(
                    selected = selectedItem == BottomNavItems.MARKERS_VIEW,
                    onClick = {
                        selectedItem = BottomNavItems.MARKERS_VIEW
                        bottomNavController.navigate(BottomNavItems.MARKERS_VIEW.route) {
                            popUpTo(BottomNavItems.MARKERS_VIEW.route)
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.marker),
                            contentDescription = "Marker"
                        )
                    },
                    label = {
                        Text(text = "Marker")
                    }
                )
            }
        }
    ) {
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItems.HOME_VIEW.route,
            modifier = Modifier.padding(it)
        ) {
            composable(BottomNavItems.HOME_VIEW.route) {
                HomeViewRoute(navigateToVidDetail = navigateToVidDetail)
            }
            composable(BottomNavItems.SEARCH_VIEW.route) {
                SearchViewRoute()
            }
            composable(BottomNavItems.MARKERS_VIEW.route) {
                MarkerViewRoute()
            }
        }
    }
}