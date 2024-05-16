package com.example.geo.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.example.geo.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalTime

@Dao
interface TrackDao {

    @Query("select * from tracks")
    fun getAll(): Flow<List<TrackEntity>>

    @Query("insert into tracks (name) values(:name)")
    fun insert(name: String): Long

    @Query("select * from tracks where id = :id")
    fun getById(id: Long): TrackEntity

    @Query("update tracks set duration = :duration, distance = :distance where id = :id")
    fun update(id: Long, duration: Long, distance: Double)

    @Delete
    fun delete(track: TrackEntity)
}