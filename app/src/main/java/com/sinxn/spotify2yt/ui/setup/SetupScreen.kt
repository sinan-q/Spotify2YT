package com.sinxn.spotify2yt.ui.setup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.spotify2yt.ui.home.HomeViewModel

@Composable
fun SetupScreen(
    navController: NavController,
    viewModel: SetupViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "Setup Spotify")
            }
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "Setup Youtube Music")
            }
        }
    }
}