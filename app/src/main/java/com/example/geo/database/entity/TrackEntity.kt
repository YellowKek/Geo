package com.example.geo.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.geo.database.LocalTimeConverter
import java.time.Duration
import java.time.LocalTime

@Entity(
    tableName = "tracks",
    indices = [Index("id")]
)
data class TrackEntity (
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo("name") var name: String,
//    @ColumnInfo("start") var start: LocalTime,
    @ColumnInfo("distance") var distance: Double?,
    @ColumnInfo("duration") var duration: Long?
)