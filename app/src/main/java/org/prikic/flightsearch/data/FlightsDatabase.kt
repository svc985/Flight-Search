package org.prikic.flightsearch.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors

@Database(
    entities = [Airport::class, Favorite::class],
    version = 1,
    exportSchema = false)
abstract class FlightsDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var Instance: FlightsDatabase? = null

        fun getDatabase(context: Context): FlightsDatabase {
            return Instance ?: synchronized(this) {
                try {
                    Log.d("FlightsDatabase", "creating database...")
                    val db =
                        Room.databaseBuilder(
                            context,
                            FlightsDatabase::class.java,
                            "flights_database"
                        )
                            // Setting this option in your app's database builder means that Room
                            // permanently deletes all data from the tables in your database when it
                            // attempts to perform a migration with no defined migration path.
                            .fallbackToDestructiveMigration()
                            .createFromAsset("databases/flight_search.db")

                    db
                        .setQueryCallback(object : QueryCallback {
                            override fun onQuery(sqlQuery: String, bindArgs: List<Any?>) {
                                Log.d(
                                    "FlightsDatabase",
                                    "SQL Query: $sqlQuery SQL Args: $bindArgs"
                                )
                            }
                        }, Executors.newSingleThreadExecutor())
                        .build()
                        .also { Instance = it }
                } catch (e: Exception) {
                    e.message?.let { Log.e("FlightsDatabase", it) }
                    return Instance!!
                }
            }
        }
    }
}