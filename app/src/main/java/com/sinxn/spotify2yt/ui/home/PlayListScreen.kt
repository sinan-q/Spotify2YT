package com.sinxn.spotify2yt.ui.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adamratzman.spotify.models.PlaylistTrack
import com.adamratzman.spotify.models.Track
import com.sinxn.spotify2yt.R
import com.sinxn.spotify2yt.tools.ytId

@Composable
fun PlayListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = viewModel.playlistName)
        }
        LazyColumn(content = {
            items(viewModel.playlistSongs) {
                AlbumCard(playlistTrack = it) {track->
                    val url = "https://music.youtube.com/watch?v=${track?.ytId}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
        })
    }
}

@Composable
fun AlbumCard(
    playlistTrack: Track?,
    onClick: (Track?) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth(0.8f)
            .padding(15.dp)
            .clickable {
                onClick(playlistTrack)
            },) {
        Text(text = playlistTrack?.name?: "" )

    }
}