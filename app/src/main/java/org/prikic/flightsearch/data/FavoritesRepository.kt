package org.prikic.flightsearch.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Favorite] from a given data source.
 */
interface FavoritesRepository {
    /**
     * Retrieve all the favorites from the the given data source.
     */
    fun getAllFavoritesStream(): Flow<List<Favorite>>

    fun getAllFavoriteFlights(): Flow<List<Flight>>

    suspend fun insert(favorite: Favorite)

    suspend fun delete(favorite: Favorite)
}