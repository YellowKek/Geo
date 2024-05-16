package com.example.geo.database

import androidx.room.TypeConverter
import java.time.Duration
import java.time.LocalTime

class LocalTimeConverter {
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toLocalTime(time: String?): LocalTime? {
        return time?.let { LocalTime.parse(it) }
    }
}

class DurationConverter {
    @TypeConverter
    fun fromDuration(time: Duration?): String? {
        return time?.toString()
    }

    @TypeConverter
    fun toDuration(time: String?): Duration? {
        return time?.let { Duration.parse(it) }
    }
}