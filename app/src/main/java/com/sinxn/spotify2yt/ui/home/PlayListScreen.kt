package com.sinxn.spotify2yt.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adamratzman.spotify.models.PlaylistTrack

@Composable
fun PlayListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = viewModel.playlistName)
        }
        LazyColumn(content = {
            Log.d("TAG", "PlayListScreen: ${viewModel.playlistSongs}")
            items(viewModel.playlistSongs) {
                AlbumCard(playlistTrack = it)
            }
        })
    }
}

@Composable
fun AlbumCard(playlistTrack: PlaylistTrack) {
    Column(
        Modifier
            .fillMaxWidth(0.8f)
            .padding(15.dp),) {
        Text(text =playlistTrack.track?.asTrack?.name?: "" )
    }
}