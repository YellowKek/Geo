package com.example.geo.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.geo.database.entity.LocationEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDao {

    @Query("select * from locations where track_id = :trackId")
    fun getByTrack(trackId: Long): Flow<List<LocationEntity>>

    @Insert
    fun insert(locationEntity: LocationEntity): Long
}