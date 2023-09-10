package com.sinxn.spotify2yt.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinxn.spotify2yt.R
import com.sinxn.spotify2yt.domain.model.Tracks
import com.sinxn.spotify2yt.ui.home.PlaylistEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPlaylistDialog(
    _title: String?,
    _description: String?,
    videoIds: List<Tracks>,
    onDismiss: () -> Unit,
    onConfirm: (PlaylistEvent) -> Unit

) {
    var title by remember { mutableStateOf(_title?:"") }
    var description by remember { mutableStateOf(_description?:"") }
    var privacyStatus by remember { mutableStateOf(true) }
    var sourcePlaylist by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = stringResource(R.string.upload_playlist))
        },
        text = {
            Column {
                OutlinedTextField(
                    label = { Text(text = stringResource(R.string.upload_playlist_title)) },
                    value = title,
                    onValueChange = { title = it })
                OutlinedTextField(
                    label = { Text(text = stringResource(R.string.upload_playlist_desc)) },
                    value = description,
                    onValueChange = { description = it })
                Text(text = stringResource(R.string.upload_playlist_privacy_status))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = privacyStatus, onClick = { privacyStatus  = true })
                    Text(text = "Private")
                    Spacer(modifier = Modifier.width(20.dp))
                    RadioButton(selected = !privacyStatus, onClick = { privacyStatus  = false })
                    Text(text = "Public")
                }

                OutlinedTextField(
                    label = { Text(text = stringResource(R.string.upload_playlist_source_playlist)) },
                    value = sourcePlaylist,
                    onValueChange = { sourcePlaylist = it })
            }

        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onConfirm(PlaylistEvent.Upload(title, description, videoIds,if (privacyStatus) "PRIVATE" else "PUBLIC" , sourcePlaylist))
            }) {
                Text(stringResource(R.string.upload_button))
            }
        },
    )
}