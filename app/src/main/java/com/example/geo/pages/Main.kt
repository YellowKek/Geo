package com.example.geo.pages

import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.mapview.MapView
import androidx.compose.ui.unit.em
import com.example.geo.database.entity.TrackEntity
import com.example.geo.navigation.Page

@Composable
fun Main(
    modifier: Modifier = Modifier,
    navController: NavController,
    locations: List<Location>,
    tracks: List<TrackEntity>,
    onSelectTrack:(TrackEntity) -> Unit = {},

) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tracks",
                fontSize = 10.em,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(top = 10.dp, bottom = 20.dp),
                color = Color.Magenta
            )
        }

        Scaffold(modifier = modifier, bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 0.dp, 20.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                androidx.compose.material3.Button(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(60.dp),
                    onClick = { navController.navigate(Page.ADD_TRACK.route) }
                )
                {
                    Text("Добавить", fontSize = 7.em)
                }
            }
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 0.dp, 0.dp, 20.dp)
                ) {
                    LazyColumn {
                        items(tracks) {
                            TrackCard(it) {
                                onSelectTrack(it)
                                navController.navigate(Page.TRACK_INFO.route)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackCard(
    track: TrackEntity,
    onSelect: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(0.dp, 0.dp, 0.dp, 5.dp)
            .clickable { onSelect() },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 8.em,
                    text = track.name,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}