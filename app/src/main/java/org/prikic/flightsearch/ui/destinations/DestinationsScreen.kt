package org.prikic.flightsearch.ui.destinations

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.prikic.flightsearch.FlightTopAppBar
import org.prikic.flightsearch.R
import org.prikic.flightsearch.TAG
import org.prikic.flightsearch.data.Favorite
import org.prikic.flightsearch.data.Flight
import org.prikic.flightsearch.ui.AppViewModelProvider
import org.prikic.flightsearch.ui.navigation.NavigationDestination
import org.prikic.flightsearch.ui.theme.FavoriteStar
import org.prikic.flightsearch.ui.theme.NonFavoriteStar

object FlightDestinations : NavigationDestination {
    override val route = "airport_details"
    override val titleRes = R.string.flight_details_title
    const val flightIdArg = "airportId"
    val routeWithArgs = "$route/{$flightIdArg}"
}

@Composable
fun DestinationsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AirportDestinationsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var flight = Flight()
    val flightsUiState = viewModel.destinationAirportsUiState.collectAsState()
    val favoriteUiState = viewModel.favoriteAirportsUiState.collectAsState()

    flightsUiState.value.forEach {
        if (it.selectedAirportIata == it.arrivalAirportIata) {
            flight = it
        }
    }

    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            FlightTopAppBar(
                title = stringResource(FlightDestinations.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) { innerPadding ->
        AirportDestinationsBody(
            flight = flight,
            flightsUiState = flightsUiState.value,
            favorites = favoriteUiState.value,
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun AirportDestinationsBody(
    flight: Flight,
    flightsUiState: List<Flight>,
    favorites: List<Favorite>,
    viewModel: AirportDestinationsViewModel,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Flights from ${flight.selectedAirportIata}",
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(8.dp)
        )
        AirportDestinations(
            flightsUiState = flightsUiState,
            favorites = favorites,
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            modifier = modifier
        )
    }
}

@Composable
private fun AirportDestinations(
    flightsUiState: List<Flight>,
    favorites: List<Favorite>,
    viewModel: AirportDestinationsViewModel,
    coroutineScope: CoroutineScope,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = flightsUiState, key = { it.arrivalAirportIata }) { flightUiState ->
            if (flightUiState.selectedAirportIata != flightUiState.arrivalAirportIata) {
                AirportDestination(
                    flightUiState = flightUiState,
                    favorites = favorites,
                    viewModel = viewModel,
                    coroutineScope = coroutineScope
                )
                Divider()
            }
        }
    }
}

@Composable
private fun AirportDestination(
    flightUiState: Flight,
    favorites: List<Favorite>,
    viewModel: AirportDestinationsViewModel,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    Log.d(LocalContext.TAG, "favorites size:${favorites.size}")
    var isFavorite = false
    var favoriteToDelete: Favorite? = null
    favorites.forEach {
        if (it.departureCode == flightUiState.selectedAirportIata && it.destinationCode == flightUiState.arrivalAirportIata) {
            isFavorite = true
            favoriteToDelete = it
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .padding(vertical = 16.dp),
        ) {
            Text(
                text = stringResource(id = R.string.depart),
                fontSize = 12.sp
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(flightUiState.selectedAirportIata)
                    }
                    append(" ")
                    append(flightUiState.selectedAirportName)
                }
            )
            Text(
                text = stringResource(id = R.string.arrive),
                fontSize = 12.sp
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(flightUiState.arrivalAirportIata)
                    }
                    append(" ")
                    append(flightUiState.arrivalAirportName)
                }
            )
        }
        IconButton(
            onClick = {
                coroutineScope.launch {
                    if (isFavorite) {
                        Log.d(TAG, "delete favorite:${flightUiState.selectedAirportIata} ${flightUiState.arrivalAirportIata}")
                        viewModel.deleteFavoriteState(favoriteToDelete)
                    } else {
                        Log.d(TAG, "save favorite:${flightUiState.selectedAirportIata} ${flightUiState.arrivalAirportIata}")
                        viewModel.updateFavoriteState(flightUiState.selectedAirportIata, flightUiState.arrivalAirportIata)
                    }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.non_favorite),
                tint = if (isFavorite) FavoriteStar else NonFavoriteStar,
                contentDescription = stringResource(id = R.string.favorite)
            )
        }
    }
}