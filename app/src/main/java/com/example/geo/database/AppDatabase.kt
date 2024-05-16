package com.example.geo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.geo.database.dao.LocationDao
import com.example.geo.database.dao.TrackDao
import com.example.geo.database.entity.LocationEntity
import com.example.geo.database.entity.TrackEntity

@Database(
    version = 1,
    entities = [
        LocationEntity::class,
        TrackEntity::class
    ]
)
@TypeConverters(LocalTimeConverter::class, DurationConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLocationDao() : LocationDao
    abstract fun getTrackDao(): TrackDao
}