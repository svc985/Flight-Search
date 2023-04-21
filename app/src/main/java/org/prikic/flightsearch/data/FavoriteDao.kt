package org.prikic.flightsearch.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * from favorite ORDER BY id ASC")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("select f.departure_code as selectedAirportIata, a1.name as selectedAirportName, " +
            "f.destination_code as arrivalAirportIata, a2.name as arrivalAirportName " +
            "from favorite f inner join airport a1 on a1.iata_code = f.departure_code " +
            "inner join airport a2 on a2.iata_code = f.destination_code")
    fun getAllFavoriteFlights() : Flow<List<Flight>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)
}
