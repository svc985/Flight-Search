package org.prikic.flightsearch.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.prikic.flightsearch.FlightsApplication
import org.prikic.flightsearch.ui.destinations.AirportDestinationsViewModel
import org.prikic.flightsearch.ui.home.HomeViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                flightsApplication().container.airportsRepository,
                flightsApplication().container.favoritesRepository,
                flightsApplication().userPreferencesRepository
            )
        }

        // Initializer for AirportDestinationsViewModel
        initializer {
            AirportDestinationsViewModel(
                this.createSavedStateHandle(),
                flightsApplication().container.airportsRepository,
                flightsApplication().container.favoritesRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [FlightsApplication].
 */
fun CreationExtras.flightsApplication(): FlightsApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as FlightsApplication)