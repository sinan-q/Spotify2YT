package com.sinxn.spotify2yt.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController
import com.sinxn.spotify2yt.tools.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    var playlist by remember {
        mutableStateOf("")
    }
    LaunchedEffect(true) {
        if (!viewModel.isLogged) navController.navigate(Routes.SETUP_SCREEN)
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {

            OutlinedTextField(value = playlist,label={ Text(text = "Paste the Spotify Link")}, onValueChange = {playlist=it})
            Button(onClick = {
                viewModel.onConvert(playlist)
                navController.navigate(Routes.PLAYLIST_SCREEN)
            }) {
                Text(text = "Convert")
            }
        }
    }

}