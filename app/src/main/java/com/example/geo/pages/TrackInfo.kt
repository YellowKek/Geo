package com.example.geo.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.geo.database.entity.LocationEntity
import com.example.geo.database.entity.TrackEntity
import com.example.geo.navigation.Page
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.mapview.MapView

@Composable
fun TrackInfo(
    modifier: Modifier = Modifier,
    track: TrackEntity?,
    locationsInTrack: List<LocationEntity>,
    onDeleteTrack: (TrackEntity) -> Unit,
    navController: NavController
) {
    track?.let {
        Scaffold(
            modifier = modifier,
            topBar = {
                Column(modifier = modifier) {
                    Row {
                        Text(
                            text = "Название: ${track.name}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 5.em,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, bottom = 2.dp, start = 5.dp)
                        )
                    }
                    Row {
                        Text(
                            text = String.format("Расстояние: %.2f м", track.distance),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 5.em,
                            modifier = modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, bottom = 2.dp, start = 5.dp)
                        )
                    }
                    Row {
                        track.duration?.let { duration ->
                            Text(
                                text = "Время: ${duration / 3600 % 24}:" +
                                        "${duration / 60 % 60 / 10}${duration / 60 % 60 % 10}:" +
                                        "${duration % 60 / 10}${duration % 60 % 10}"    ,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 5.em,
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp, bottom = 2.dp, start = 5.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    androidx.compose.material3.Button(modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(60.dp),
                        onClick = {
                            onDeleteTrack(track)
                            navController.navigate(Page.MAIN.route)
                        }
                    )
                    {
                        Text("Удалить", fontSize = 7.em)
                    }
                }
            }
        ) {
            Box(
                modifier = modifier.padding(it),
            ) {
                AndroidView(
                    factory = {
                        MapView(it)
                    },
                    update = { mapView ->
                        mapView.mapWindow.map.apply {
                            mapObjects.clear()
                            locationsInTrack.let {
                                if (locationsInTrack.isNotEmpty()) {
                                    val pl =
                                        Polyline(locationsInTrack.map {
                                            Point(
                                                it.shirota,
                                                it.dolgota
                                            )
                                        })
                                    move(cameraPosition(Geometry.fromPolyline(pl)))
                                    mapObjects.addPolyline(pl)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}