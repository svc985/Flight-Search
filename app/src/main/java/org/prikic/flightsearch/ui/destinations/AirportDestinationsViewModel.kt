package org.prikic.flightsearch.ui.destinations

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.prikic.flightsearch.TAG
import org.prikic.flightsearch.data.AirportsRepository
import org.prikic.flightsearch.data.Favorite
import org.prikic.flightsearch.data.FavoritesRepository
import org.prikic.flightsearch.data.Flight

/**
 * ViewModel to retrieve airport data from the [AirportsRepository]'s data source.
 */
class AirportDestinationsViewModel(
    savedStateHandle: SavedStateHandle,
    airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val airportId: Int = checkNotNull(savedStateHandle[FlightDestinations.flightIdArg])

    val favoriteAirportsUiState: StateFlow<List<Favorite>> =
        favoritesRepository.getAllFavoritesStream()
            .map {
                it.forEach { favorite ->
                    Log.d(TAG, "favorite data:$favorite")
                }
                it
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    val destinationAirportsUiState: StateFlow<List<Flight>> =
        airportsRepository.getDestinationsForAirportStream(airportId)
            .filterNotNull()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    suspend fun updateFavoriteState(departureIata: String, arrivalIata: String) {
        val favorite = Favorite(0, departureIata, arrivalIata)
        favoritesRepository.insert(favorite)
    }

    suspend fun deleteFavoriteState(favorite: Favorite?) {
        if (favorite == null) {
            Log.d(TAG, "favorite is null, can't delete it")
        }
        favoritesRepository.delete(favorite!!)
    }
}