package data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SafetyPin::class], version = 1)
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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
