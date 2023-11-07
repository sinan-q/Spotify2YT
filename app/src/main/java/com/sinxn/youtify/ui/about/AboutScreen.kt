package com.sinxn.youtify.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sinxn.youtify.R

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(modifier = Modifier.fillMaxWidth(0.3f),painter = painterResource(id = R.drawable.ic_app_name_colored), contentDescription = "app_logo")
        Spacer(modifier = Modifier.height(20.dp))
        CardLayout(imageId = R.drawable.about_version, text = "Version: 0.1 beta", subText = "com.sinxn.youtify") {}
        CardLayout(imageId = R.drawable.about_github, text = "Source Code", subText = "https://github.com/sinan-q/Youtify") {
            openLink(context,"https://github.com/sinan-q/Youtify")
        }
        CardLayout(imageId = R.drawable.about_issues, text = "Issues", subText = "Report any issues") {
            openLink(context,"https://github.com/sinan-q/Youtify/issues")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Made with ❤️ Sinan", modifier = Modifier.clickable {
            openLink(context,"https://github.com/sinan-q/")
        })
    }
}

private fun openLink(context: Context, link: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(link)
    context.startActivity(intent)
}

@Composable
fun CardLayout(
    imageId: Int,
    imageDesc: String? = null,
    text: String,
    subText: String = "",
    onClick: () -> Unit
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp), ) {
        Row(modifier = Modifier.clickable { onClick() }, verticalAlignment = Alignment.CenterVertically) {
            Image(modifier = Modifier
                .padding(15.dp)
                .size(50.dp), painter = painterResource(id = imageId), contentDescription = imageDesc)
            Column() {
                Text(text = text, fontWeight = FontWeight.Bold)
                if (subText.isNotBlank()) Text(text = subText, fontWeight = FontWeight.ExtraLight, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}