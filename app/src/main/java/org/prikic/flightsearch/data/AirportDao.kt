package org.prikic.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :text || '%' OR name LIKE '%' || :text || '%'")
    fun getAutoSuggestedAirports(text: String): Flow<List<Airport>>

    @Query("SELECT a1.name AS selectedAirportName, a1.iata_code AS selectedAirportIata, \n" +
            "a2.name arrivalAirportName, a2.iata_code AS arrivalAirportIata FROM airport a1 \n" +
            "CROSS JOIN airport a2 WHERE a1.id =:id")
    fun getDestinationsForAirport(id: Int): Flow<List<Flight>>
}