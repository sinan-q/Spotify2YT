package com.sinxn.spotify2yt.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sinxn.spotify2yt.tools.Routes
import com.sinxn.spotify2yt.ui.home.HomeScreen
import com.sinxn.spotify2yt.ui.home.HomeViewModel
import com.sinxn.spotify2yt.ui.home.PlayListScreen
import com.sinxn.spotify2yt.ui.setup.SetupScreen

@Composable
fun Navigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
){
    NavHost(navController = navController, startDestination = Routes.HOME_SCREEN ) {
        composable(Routes.SETUP_SCREEN){
            SetupScreen(navController = navController)
        }
        composable(Routes.HOME_SCREEN){
            HomeScreen(navController = navController, homeViewModel)
        }
        composable(Routes.PLAYLIST_SCREEN) {
            PlayListScreen(navController = navController, homeViewModel)
        }

    }

}