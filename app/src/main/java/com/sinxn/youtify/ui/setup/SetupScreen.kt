package com.sinxn.youtify.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sinxn.youtify.ui.theme.linkBackground

@Composable
fun SetupScreen(
    navController: NavController,
    viewModel: SetupViewModel = hiltViewModel()
) {
    var buttonExpandState by remember {
        mutableIntStateOf(0)
    }
    Box(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(1f), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            SetupButton(number = 1, text = "Setup Youtube Music", onToggle = {
                buttonExpandState = if (it) 1 else 0
            })

            if (buttonExpandState==1) {
                SetupYoutube(viewModel.ytmUrl, onFinish= { viewModel.getTokenFromCode() })
            }
            SetupButton(number = 2, text = "Setup Spotify", onToggle ={buttonExpandState = if (it) 2 else 0} )
            if (buttonExpandState==2) {
                SetupSpotify { spotifyClientId, spotifyClientSecret->
                    viewModel.setSpotify(spotifyClientId,spotifyClientSecret)}
            }

        }
    }
    LaunchedEffect(true) {
        viewModel.ytmGetCode()
    }
}

@Composable
fun SetupButton(
    number: Int,
    text: String,
    onToggle: (Boolean) -> Unit,
) {
    var toggle by remember { mutableStateOf(false) }
    Card(modifier= Modifier
        .padding(vertical = 3.dp)
        .fillMaxWidth()
        .clickable {
            toggle = !toggle
            onToggle(toggle)
        }, shape = RoundedCornerShape(5.dp)
    ) {
        Row(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(if (!toggle) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp, contentDescription = null)
        }
    }
}

@Composable
private fun SetupYoutube(code: String, onFinish: ()->Unit) {
    var code by remember {
        mutableStateOf(code)
    }
    Column(
        Modifier
            .padding(top = 5.dp)
            .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
            .padding(10.dp)) {
        StepScreen(stepNumber = 1,"Authorize youtube pair request.\n\nOpen the link and select your Youtube Music account")
        BasicTextField(modifier = Modifier
            .padding(vertical = 5.dp)
            .background(linkBackground)
            .padding(10.dp), readOnly = true, value = code, textStyle = TextStyle(fontStyle = FontStyle.Italic, background = linkBackground, fontSize = 14.sp), onValueChange = { code = it })
        RightSideButtons(onClick = { /*TODO*/ }, text = "Open Link", icon = Icons.Filled.KeyboardArrowRight)

        StepScreen(stepNumber = 2,"After Authorization Click Finish")
        RightSideButtons(onClick = { onFinish() }, text = "Finish")

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupSpotify(onFinish: (String, String) -> Unit) {
    var spotifyClientId by remember {
        mutableStateOf("")
    }
    var spotifyClientSecret by remember {
        mutableStateOf("")
    }
    Column(modifier = Modifier
        .padding(5.dp)
        .border(width = 0.5.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
        .padding(10.dp)) {
        StepScreen(stepNumber = 1, description = "Generate a new app at Spotify Developer and Set http://localhost as redirect URI for your app\n\nOpen the link and sign in with your Spotify account to create new app")
        BasicTextField(modifier = Modifier
            .padding(vertical = 5.dp)
            .background(linkBackground)
            .padding(10.dp), readOnly = true, value = "https://developer.spotify.com/dashboard/create", textStyle = TextStyle(fontStyle = FontStyle.Italic, background = linkBackground, fontSize = 14.sp), onValueChange = {  })
        RightSideButtons(onClick = { /*TODO*/ }, text = "Open Link", icon = Icons.Filled.KeyboardArrowRight)
        StepScreen(stepNumber = 2, description = "Paste Client ID and Client Secret from your created app in Spotify Developer\n\nClick Finish after Entering")

        OutlinedTextField(value = spotifyClientId, label = {
            Text(text = "Enter Client ID") }, onValueChange = { spotifyClientId = it })
        OutlinedTextField(value = spotifyClientSecret, label = {
            Text(text = "Enter Client Secret")
        }, onValueChange = { spotifyClientSecret = it })
        RightSideButtons(onClick = {
            onFinish(spotifyClientId,spotifyClientSecret)
        }, text = "Finish")
    }

}

@Composable
private fun StepScreen(
    stepNumber: Int,
    description: String
) {
    Text(text = "Step $stepNumber", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
    Text(text = description, lineHeight = 18.sp)

}

@Composable
fun RightSideButtons(
    onClick: ()-> Unit,
    text: String,
    icon: ImageVector? = null
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        MyButton(onClick = { onClick() }, text = text, icon = icon)
    }
}

@Composable
fun MyButton(
    onClick: ()-> Unit,
    text: String,
    icon: ImageVector? = null

) {
    ElevatedButton(onClick = { onClick() }, shape = RoundedCornerShape(5.dp)) {
        Text(text = text)
        if (icon!=null) Icon(icon,null)
    }
}