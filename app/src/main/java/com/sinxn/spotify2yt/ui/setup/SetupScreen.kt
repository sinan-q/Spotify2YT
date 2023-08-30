package com.sinxn.spotify2yt.ui.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SetupScreen(
    navController: NavController,
    viewModel: SetupViewModel = hiltViewModel()
) {

    var code by remember {
        mutableStateOf("")
    }
    var spotifyClientId by remember {
        mutableStateOf("")
    }
    var spotifyClientSecret by remember {
        mutableStateOf("")
    }
    var buttonExpandState by remember {
        mutableIntStateOf(0)
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            TextButton(onClick = {
                buttonExpandState = 1
                viewModel.ytmGetCode()
            }) {
                Text(text = "Setup Youtube Music")
            }
            if (buttonExpandState==1) {
                BasicTextField(value = code, onValueChange = { code = it })
                Button(onClick = {
                    viewModel.getTokenFromCode()
                }) {
                    Text(text = "Continue")
                }
            }



            TextButton(onClick = { buttonExpandState=2 }) {
                Text(text = "Setup Spotify")
            }
            if (buttonExpandState==2) {
                Text(text = "Enter Client ID")
                BasicTextField(value = spotifyClientId, onValueChange = { spotifyClientId = it })
                Text(text = "Enter Client Secret")
                BasicTextField(value = spotifyClientSecret, onValueChange = { spotifyClientSecret = it })
                Button(onClick = {
                    viewModel.setSpotify(spotifyClientId,spotifyClientSecret)
                }) {
                    Text(text = "Continue")
                }
            }

        }
    }
    LaunchedEffect(viewModel.ytmUrl) {
        code = viewModel.ytmUrl
    }
}