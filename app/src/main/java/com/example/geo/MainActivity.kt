package com.example.geo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.geo.navigation.NavContent
import com.example.geo.ui.theme.GeoTheme
import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {
    private val mvm: MainViewModel by viewModels<MainViewModel>()
    private lateinit var requester: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("49243626-023b-4a52-b9fd-a9a31a581ba9")
        MapKitFactory.initialize(this)

        requester = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (!it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                mvm.startLocationUpdates()
            }
        }

        requester.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        setContent {
            val navController: NavHostController = rememberNavController()
            GeoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavContent(
                        navController = navController,
                        locations = mvm.locations,
                        onAddTrack = mvm::addTrack,
                        tracks = mvm.tracks,
                        curTrack = mvm.curTrack,
                        onStopTrack = mvm::stopTrack,
                        trackLocations = mvm.selectedTrackLocations,
                        selectedTrack = mvm.selectedTrack,
                        onSelectTrack = mvm::selectTrack,
                        onDeleteTrack = mvm::deleteTrack,

                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
