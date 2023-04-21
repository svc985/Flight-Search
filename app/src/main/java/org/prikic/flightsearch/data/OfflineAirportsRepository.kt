package org.prikic.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineAirportsRepository(private val airportDao: AirportDao) : AirportsRepository {
    override fun getAutoSuggestedAirportsStream(text: String): Flow<List<Airport>> =
        airportDao.getAutoSuggestedAirports(text)

    override fun getDestinationsForAirportStream(id: Int): Flow<List<Flight>> =
        airportDao.getDestinationsForAirport(id)
}