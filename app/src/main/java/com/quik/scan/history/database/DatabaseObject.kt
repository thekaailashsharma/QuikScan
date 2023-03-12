package com.quik.scan.history.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1)
abstract class DatabaseObject : RoomDatabase() {

    abstract fun hisDao(): HistoryDao

    companion object {
        @Volatile
        private var Instance: DatabaseObject? = null

        fun getInstance(context: Context): DatabaseObject {
            synchronized(this) {
                if (Instance == null) {
                    Instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            DatabaseObject::class.java,
                            "historyDatabase"
                        )
                            .build()
                }
            }
            return Instance!!
        }
    }
}
