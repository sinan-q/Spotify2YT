package com.sinxn.spotify2yt.ui.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adamratzman.spotify.models.PlaylistTrack
import com.adamratzman.spotify.models.Track
import com.sinxn.spotify2yt.R
import com.sinxn.spotify2yt.domain.model.Tracks
import com.sinxn.spotify2yt.tools.ytId

@Composable
fun PlayListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState

    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)) {
            Text(text = uiState.playlist?.name?: stringResource(R.string.playlist_name_not_found),modifier = Modifier.align(Alignment.Center))
            Row(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { viewModel.savePlaylist() }) {
                    Icon(painter = painterResource(id = R.drawable.playlist_save_ic), contentDescription = stringResource(R.string.save_playlist))
                }
                IconButton(onClick = { viewModel.uploadPlaylist() }) {
                    Icon(painter = painterResource(id = R.drawable.playlist_upload_ic), contentDescription = stringResource(R.string.upload_playlist))
                }
            }

        }
        LazyColumn(content = {
            items(uiState.playlistSongs) {
                AlbumCard(playlistTrack = it) {tracks->
                    val url = "https://music.youtube.com/watch?v=${tracks?.youtube_id}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
        })
    }
}

@Composable
fun AlbumCard(
    playlistTrack: Tracks?,
    onClick: (Tracks?) -> Unit
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