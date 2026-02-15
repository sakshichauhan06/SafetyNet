package com.example.safetynet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.safetynet.domain.IncidentType
import com.example.safetynet.domain.SeverityLevel
import com.example.safetynet.utils.AppConstants


@Database(entities = [SafetyPin::class], version = AppConstants.DATABASE_VERSION, exportSchema = true)
@TypeConverters(Converters::class)
abstract class SafetyPinDatabase : RoomDatabase() {

    abstract fun safetyPinDao(): SafetyPinDao

    companion object {
        @Volatile
        private var INSTANCE: SafetyPinDatabase? = null

        fun getDatabase(context: Context): SafetyPinDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SafetyPinDatabase::class.java,
                    AppConstants.DATABASE_NAME
                )
//                    .fallbackToDestructiveMigration()
//                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromSeverityLevel(value: SeverityLevel): String {
        return value.name
    }

    @TypeConverter
    fun toSeverityLevel(value: String): SeverityLevel {
        return SeverityLevel.valueOf(value)
    }

    @TypeConverter
    fun fromIncidentType(value: IncidentType): String {
        return value.name
    }

    @TypeConverter
    fun toIncidentType(value: String): IncidentType {
        return IncidentType.valueOf(value)
    }
}
