package com.sinxn.spotify2yt.ui.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sinxn.spotify2yt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit

) {
    var playlist by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = stringResource(R.string.enter_playlist_link))
        },
        text = {
            OutlinedTextField(
                label = { Text(text = stringResource(R.string.paste_spotify_link)) },
                value = playlist,
                onValueChange = { playlist = it })
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onConfirm(playlist)
            }) {
                Text("Convert")
            }
        },
    )
}