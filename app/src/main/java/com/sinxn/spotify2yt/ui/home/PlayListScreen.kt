package com.sinxn.spotify2yt.ui.home

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.spotify2yt.R
import com.sinxn.spotify2yt.domain.model.Tracks

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
            Text(text = uiState.playlist?.name?: stringResource(R.string.playlist_name_not_found),fontWeight = FontWeight.Bold, fontSize = 24.sp ,modifier = Modifier.align(Alignment.Center))
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
                AlbumCard(playlistTrack = it,  onAction = {event->
                    viewModel.onEvent(event)
                })
            }
        })
    }
    LaunchedEffect(uiState.play) {
        if (uiState.play!=null) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.play))
            context.startActivity(intent)
        }
    }

}

@Composable
fun AlbumCard(
    playlistTrack: Tracks?,
    onAction: (HomeEvent) -> Unit,
) {
    var optionsState by remember { mutableStateOf(false) }
    val ytIdNotFound = playlistTrack?.youtube_id.isNullOrEmpty()
    Card(
        colors = if (ytIdNotFound) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer, ) else CardDefaults.cardColors(),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(0.dp, 5.dp)
            .clickable {
                onAction(HomeEvent.OnSongAction(SongEvent.Play(playlistTrack)))
            }) {
        Row(modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { onAction(HomeEvent.OnSongAction(SongEvent.Play(playlistTrack))) }) {
                Icon(if (ytIdNotFound) Icons.Filled.Refresh else Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.play_selected_song))
            }
            Column(modifier = Modifier
                .padding(10.dp, 15.dp)
                .fillMaxWidth(0.8f)) {
                Text(text = playlistTrack?.name?: "" )
            }
            IconToggleButton(checked = optionsState, onCheckedChange = {optionsState = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_selected_song_actions))
                DropdownMenu(
                    expanded = optionsState,
                    onDismissRequest = { optionsState = false }) {
                    DropdownMenuItem(
                        text = { Text(text = "Reload") },
                        onClick = {
                            onAction(HomeEvent.OnSongAction(SongEvent.Reload(playlistTrack!!)))
                        })
                }
            }

        }




    }

}