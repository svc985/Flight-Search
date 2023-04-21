package org.prikic.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFavoritesRepository(private val favoriteDao: FavoriteDao) : FavoritesRepository {
    override fun getAllFavoritesStream(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()
    override fun getAllFavoriteFlights(): Flow<List<Flight>> = favoriteDao.getAllFavoriteFlights()

    override suspend fun insert(favorite: Favorite) = favoriteDao.insert(favorite)

    override suspend fun delete(favorite: Favorite) = favoriteDao.delete(favorite)
}