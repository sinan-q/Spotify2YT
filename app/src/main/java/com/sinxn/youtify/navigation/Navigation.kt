package com.sinxn.youtify.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sinxn.youtify.tools.Routes
import com.sinxn.youtify.ui.home.HomeScreen
import com.sinxn.youtify.ui.home.HomeViewModel
import com.sinxn.youtify.ui.home.PlayListScreen
import com.sinxn.youtify.ui.setup.SetupScreen

@Composable
fun Navigation(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
){
    NavHost(navController = navController, startDestination = Routes.HOME_SCREEN ) {
        composable(Routes.SETUP_SCREEN){
            SetupScreen(navController = navController)
        }
        composable(Routes.HOME_SCREEN){
            HomeScreen(navController = navController, homeViewModel)
        }
        composable(Routes.PLAYLIST_SCREEN) {
            PlayListScreen(navController = navController, viewModel =  homeViewModel)
        }

        composable(Routes.PLAYLIST_SCREEN+"/?url={url}", arguments = listOf(navArgument("url") {
            type= NavType.StringType
            defaultValue = ""
        })) {
            PlayListScreen(navController = navController, viewModel =  homeViewModel, playlistUrl = it.arguments?.getString("url"))
        }

    }

}