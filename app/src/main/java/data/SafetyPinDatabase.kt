package data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import domain.SeverityLevel


@Database(entities = [SafetyPin::class], version = 2)
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
                    "safety_pin_database"
                )
                    .fallbackToDestructiveMigration()
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
}
