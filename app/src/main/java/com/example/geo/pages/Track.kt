package com.example.geo.pages

import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.geo.database.entity.TrackEntity
import com.example.geo.navigation.Page
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.mapview.MapView
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun AddTrack(
    modifier: Modifier = Modifier,
    navController: NavController,
    onAddTrack: (String) -> Unit,

    ) {
    val trackName = remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.padding(20.dp),
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 0.dp, 20.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                androidx.compose.material3.Button(modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(60.dp),
                    onClick = {
                        onAddTrack(trackName.value)
                        navController.navigate(Page.TRACK.route)
                    }
                )
                {
                    Text("Начать", fontSize = 8.em)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                OutlinedTextField(
                    value = trackName.value,
                    onValueChange = { trackName.value = it },
                    placeholder = {
                        Text(
                            text = "Название",
                            fontSize = 8.em
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(fontSize = 8.em),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun Track(
    track: TrackEntity?,
    locations: List<Location>,
    modifier: Modifier = Modifier,
    navController: NavController,
    onStop: (TrackEntity) -> Unit,

    ) {
    track?.let {
        val elapsedSeconds = remember { mutableLongStateOf(0L) }
        var distance by remember { mutableDoubleStateOf(0.0) }
        Scaffold(
            modifier = modifier,
            bottomBar = {
                StopButton(
                    navController = navController,
                    onStop = onStop,
                    track = track,
                    elapsedSeconds = elapsedSeconds,
                    distance = distance,
                )
            },
            topBar = {
                TimerDisplay(track = track, modifier, elapsedSeconds, distance)
            }
        ) {
            YaMap(modifier, locations, it) {
                distance = it
            }
        }
    }
}


@Composable
fun TimerDisplay(
    track: TrackEntity,
    modifier: Modifier,
    elapsedSeconds: MutableLongState,
    distance: Double,
) {
    val startTime = remember { mutableLongStateOf(System.nanoTime()) }
    val handler = remember { Handler(Looper.getMainLooper()) }

    LaunchedEffect(key1 = Unit) {
        val updateTimerRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.nanoTime() - startTime.longValue
                elapsedSeconds.longValue = TimeUnit.NANOSECONDS.toSeconds(elapsedTime)
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateTimerRunnable, 1000)
    }
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
                text = String.format("Расстояние: %.2f км", distance),
                fontFamily = FontFamily.Monospace,
                fontSize = 5.em,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp, start = 5.dp)
            )
        }
        Row {
            Text(
                text = "Время: ${elapsedSeconds.longValue / 3600 % 24}:" +
                        "${elapsedSeconds.longValue / 60 % 60 / 10}${elapsedSeconds.longValue / 60 % 60 % 10}:" +
                        "${elapsedSeconds.longValue % 60 / 10}${elapsedSeconds.longValue % 60 % 10}",
                fontFamily = FontFamily.Monospace,
                fontSize = 5.em,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp, start = 5.dp)
            )

        }
    }

}

@Composable
fun StopButton(
    navController: NavController,
    onStop: (TrackEntity) -> Unit,
    track: TrackEntity,
    elapsedSeconds: MutableLongState,
    distance: Double
) {
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
                track.duration = elapsedSeconds.longValue
                track.distance = distance
                onStop(track)
                navController.navigate(Page.MAIN.route)
            }
        )
        {
            Text("Стоп", fontSize = 7.em)
        }
    }
}

@Composable
fun YaMap(
    modifier: Modifier,
    locations: List<Location>,
    paddingValues: PaddingValues,
    onChangeDistance: (Double) -> Unit

) {
    var lastLoc: Location? = null
    Box(
        modifier = modifier.padding(paddingValues),
    ) {
        AndroidView(
            factory = {
                MapView(it)
            },
            update = { mapView ->
                mapView.mapWindow.map.apply {
                    mapObjects.clear()
                    locations.let {
                        if (locations.isNotEmpty()) {
                            if (locations.size == 1) lastLoc = locations.last()
                            lastLoc?.let {
                                val pl =
                                    Polyline(locations.map { Point(it.latitude, it.longitude) })
                                move(cameraPosition(Geometry.fromPolyline(pl)))
                                mapObjects.addPolyline(pl)
                                if (locations.size >= 2) {
                                    if ((locations.last().latitude != lastLoc!!.latitude) ||
                                        (locations.last().longitude != lastLoc!!.longitude)
                                    )
                                        onChangeDistance(
                                            calcTrackDistance(locations)
                                        )
                                }
                            }
                            lastLoc = locations.last()
                        }
                    }
                }
            }
        )
    }
}

fun calcTrackDistance(coordinates: List<Location>): Double {
    if (coordinates.size <= 1) {
        return 0.0
    }

    var totalDistance = 0.0
    for (i in 1 until coordinates.size) {
        val lat1 = Math.toRadians(coordinates[i - 1].latitude)
        val lat2 = Math.toRadians(coordinates[i].latitude)
        val lon1 = Math.toRadians(coordinates[i - 1].longitude)
        val lon2 = Math.toRadians(coordinates[i].longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val earthRadius = 6371.0
        val distance = earthRadius * c

        totalDistance += distance
    }

    return totalDistance
}