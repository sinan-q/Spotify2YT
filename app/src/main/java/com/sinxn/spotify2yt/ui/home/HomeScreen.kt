package com.sinxn.spotify2yt.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.spotify2yt.domain.model.Playlists
import com.sinxn.spotify2yt.tools.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    var playlist by remember {
        mutableStateOf("")
    }
    LaunchedEffect(true) {
        if (!viewModel.isLogged) navController.navigate(Routes.SETUP_SCREEN)
    }
    if (uiState.playlists.isEmpty())
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column {
            OutlinedTextField(value = playlist,label={ Text(text = "Paste the Spotify Link")}, onValueChange = {playlist=it})
            Button(onClick = {
                viewModel.onEvent(HomeEvent.OnConvert(playlist))
                navController.navigate(Routes.PLAYLIST_SCREEN)
            }) {
                Text(text = "Convert")
            }
        }
    }
    else {
        LazyColumn() {
            items(uiState.playlists) {playlist->
                PlaylistCard(playlist) {
                    viewModel.onEvent(HomeEvent.OnSelect(playlist))
                    navController.navigate(Routes.PLAYLIST_SCREEN)
                }
            }
        }
    }

}

@Composable
fun PlaylistCard(playlist: Playlists,onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(modifier = Modifier
            .align(Alignment.Center)
            .padding(20.dp, 5.dp).clickable { onClick() }
        ) {
            Text(text = playlist.name)
        }
    }
}