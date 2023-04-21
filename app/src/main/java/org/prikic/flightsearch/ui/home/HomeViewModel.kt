package org.prikic.flightsearch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prikic.flightsearch.data.Airport
import org.prikic.flightsearch.data.AirportsRepository
import org.prikic.flightsearch.data.FavoritesRepository
import org.prikic.flightsearch.data.Flight
import org.prikic.flightsearch.data.UserPreferencesRepository

class HomeViewModel(
    private var airportsRepository: AirportsRepository,
    favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(
        HomeUiState()
    )

    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    val searchTextUiState: StateFlow<String> =
        userPreferencesRepository.searchText.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ""
        )

    val favoriteFlights: StateFlow<List<Flight>> =
        favoritesRepository.getAllFavoriteFlights()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = emptyList()
            )

    suspend fun getAirportSuggestions(searchText: String) {
        airportsRepository.getAutoSuggestedAirportsStream(searchText).collect {
            updateAirportSuggestions(it)
        }
    }

    private fun updateAirportSuggestions(list: List<Airport>) {
        _homeUiState.update {
            it.copy(airportsList = list)
        }
    }

    fun saveSearchText(searchText: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveSearchTextPreference(searchText)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class HomeUiState(
    val airportsList: List<Airport> = listOf(),
)
