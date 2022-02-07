package com.soyle.stories.desktop.view.project.none

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Preview
@Composable
fun WelcomeView() {
    val viewModel = remember { WelcomeViewModel() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = Modifier.background(Brush.linearGradient(
            colors = listOf(
                Color(0x3A, 0x51, 0x8E),
                Color(0x86, 0x2F, 0x89),
            ),
            end = Offset(400f, 400f)
        ))
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Image(
            painter = painterResource("/bronze logo.png"),
            contentDescription = "Soyle Stories Logo - Bronze",
            modifier = Modifier.height(200.dp)
                .aspectRatio(1f, true)
        )
        Text("Soyle Stories",
            fontFamily = FontFamily(listOf(Font(resource = "/corbel/CORBEL.TTF"))),
            color = Color.White,
            fontSize = 4.em
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedButton(
                onClick = viewModel::startProject,
            ) {
                Icon(imageVector = Icons.Outlined.Add, "New")
                Text("Create New Project")
            }
            OutlinedButton(
                onClick = {}
            ) {
                Icon(imageVector = Icons.Outlined.MailOutline, "New")
                Text("Open Project")
            }
        }
    }
}
