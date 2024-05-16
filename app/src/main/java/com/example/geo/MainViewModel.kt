package com.example.geo

import android.app.Application
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.geo.database.AppDatabase
import com.example.geo.database.entity.LocationEntity
import com.example.geo.database.entity.TrackEntity
import com.example.geo.locating.Locator
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalTime

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(app.applicationContext)
    var locations = mutableStateListOf<Location>()

    var tracks by mutableStateOf(listOf<TrackEntity>())
        private set

    private val db =
        Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "RMP").build()

    private val trackDao = db.getTrackDao()
    private val locationDao = db.getLocationDao()

    var curTrack by mutableStateOf<TrackEntity?>(null)
    var selectedTrack by mutableStateOf<TrackEntity?>(null)
    var selectedTrackLocations by mutableStateOf(listOf<LocationEntity>())

    private var curTrackId: Long? = null
    private var locationsSelector: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Locator.location.collect { loc ->
                withContext(Dispatchers.Main) {
                    loc?.let { locations.add(it) }
                }
            }
        }
        getAllTracks()
    }


    private fun getAllTracks() {
        viewModelScope.launch(Dispatchers.IO) {
            trackDao.getAll().collect { trackList ->
                withContext(Dispatchers.Main) {
                    tracks = trackList
                }
            }
        }
    }

    fun addTrack(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val curLoc = locations.last()
            locations.clear()
            locations.add(curLoc)
            curTrackId = trackDao.insert(name)
            curTrack = trackDao.getById(curTrackId!!)
            curTrack!!.distance = 0.0
        }
    }

    fun stopTrack(track: TrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            trackDao.update(track.id, track.duration!!, track.distance!!)
            locations.forEach { location ->
                locationDao.insert(
                    LocationEntity(
                        0,
                        location.latitude,
                        location.longitude,
                        LocalTime.now(),
                        curTrackId!!
                    )
                )
            }
        }
    }

    fun selectTrack(track: TrackEntity) {
        selectedTrack = track;
        getTrackLocations()
    }

    fun deleteTrack(track: TrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            trackDao.delete(track)
        }
    }

    private fun getTrackLocations() {
        selectedTrack?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    locationsSelector?.cancelAndJoin()
                } catch (_: Throwable) {}
                locationsSelector = launch {
                    locationDao.getByTrack(it.id).collect { selectedTrackLocations = it }
                }
            }
        }
    }


    fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication<Application>().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener {
            viewModelScope.launch {
                fusedLocationClient.requestLocationUpdates(
                    Locator.locationRequest,
                    Locator,
                    Looper.getMainLooper()
                )
            }
        }

    }
}