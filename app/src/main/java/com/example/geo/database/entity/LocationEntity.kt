package com.example.geo.database.entity

import androidx.room.*
import com.example.geo.database.LocalTimeConverter
import java.time.LocalTime

@Entity(
    tableName = "locations",
    indices = [Index("id")],
    foreignKeys = [ForeignKey(
        entity = TrackEntity::class,
        parentColumns = ["id"],
        childColumns = ["track_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo("shirota") var shirota: Double,
    @ColumnInfo("dolgota") var dolgota: Double,
    @ColumnInfo("time") var time: LocalTime,
    @ColumnInfo("track_id") var trackId: Long,
)
