package org.prikic.flightsearch.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Airport] from a given data source.
 */
interface AirportsRepository {
    fun getAutoSuggestedAirportsStream(text: String): Flow<List<Airport>>

    fun getDestinationsForAirportStream(id: Int): Flow<List<Flight>>
}