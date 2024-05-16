package com.example.geo.navigation

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.geo.pages.Main
import androidx.compose.runtime.mutableStateListOf
import com.example.geo.database.entity.LocationEntity
import com.example.geo.database.entity.TrackEntity
import com.example.geo.pages.AddTrack
import com.example.geo.pages.Track
import com.example.geo.pages.TrackInfo
import java.time.LocalTime

@Composable
fun NavContent(
//    modifier: Modifier = Modifier,
    navController: NavHostController,
    locations: List<Location>,
    onAddTrack: (String) -> Unit,
    tracks: List<TrackEntity>,
    curTrack: TrackEntity?,
    onStopTrack: (TrackEntity) -> Unit,
//    locationsInTrack: List<LocationEntity>,
    selectedTrack: TrackEntity?,
    onSelectTrack: (TrackEntity) -> Unit,
    trackLocations: List<LocationEntity>,
    onDeleteTrack: (TrackEntity) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Page.MAIN.route,
        modifier = Modifier.fillMaxSize(),

        ) {
        composable(Page.MAIN.route) {
            Main(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                locations = locations,
                tracks = tracks,
                onSelectTrack = onSelectTrack,

                )
        }
        composable(Page.ADD_TRACK.route) {
            AddTrack(
                navController = navController,
                onAddTrack = onAddTrack
            )
        }

        composable(Page.TRACK.route) {
            Track(
                locations = locations,
                navController = navController,
                track = curTrack,
                onStop = onStopTrack,

                )
        }

        composable(Page.TRACK_INFO.route) {
            TrackInfo(
                track = selectedTrack,
                locationsInTrack = trackLocations,
                onDeleteTrack = onDeleteTrack,
                navController = navController
            )
        }
    }
}