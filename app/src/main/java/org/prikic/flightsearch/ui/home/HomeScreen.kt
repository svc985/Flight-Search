package org.prikic.flightsearch.ui.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import org.prikic.flightsearch.FlightTopAppBar
import org.prikic.flightsearch.R
import org.prikic.flightsearch.TAG
import org.prikic.flightsearch.data.Airport
import org.prikic.flightsearch.data.Flight
import org.prikic.flightsearch.ui.AppViewModelProvider
import org.prikic.flightsearch.ui.navigation.NavigationDestination
import org.prikic.flightsearch.ui.theme.FavoriteStar

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

/**
 * Entry route for Home screen
 */
@Composable
fun HomeScreen(
    navigateToAirportDestinations: (Airport) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val searchTextUiState by viewModel.searchTextUiState.collectAsState()
    val favoriteFlightsUiState by viewModel.favoriteFlights.collectAsState()

    Log.d(LocalContext.TAG, "searchTextUiState:$searchTextUiState")

    Scaffold(
        topBar = {
            FlightTopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false
            )
        }
    ) { innerPadding ->
        HomeBody(
            airportsList = homeUiState.airportsList,
            favoriteFlights = favoriteFlightsUiState,
            searchText = searchTextUiState,
            viewModel,
            onAirportClick = navigateToAirportDestinations,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun HomeBody(
    airportsList: List<Airport>,
    favoriteFlights: List<Flight>,
    searchText: String,
    viewModel: HomeViewModel,
    onAirportClick: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    // Returns a scope that's cancelled when HomeBody is removed from composition
    val coroutineScope = rememberCoroutineScope()
    var value by remember { mutableStateOf("") }
    val context = LocalContext.current

    Log.d(context.TAG, "edit text value:$searchText")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                value = it
                viewModel.saveSearchText(value)
                Log.d(context.TAG, "saved search text:$value")

                if (it.isNotEmpty()) {
                    coroutineScope.launch {
                        viewModel.getAirportSuggestions(it)
                    }
                }
            },
            modifier = modifier
                .padding(20.dp)
                .fillMaxWidth()
        )

        if (airportsList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_items_description),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
        } else {
            if (searchText.isEmpty()) {
                Log.d(TAG, "showing favorites with size:${favoriteFlights.size}")
                FavoriteFlightsBody(favoriteFlights = favoriteFlights)
            } else {
                AirportList(
                    airportsList = airportsList,
                    onAirportClick = {
                        Log.d(context.TAG, "clicked on:${it.name}")
                        onAirportClick(it)
                    })
            }
        }
    }
}

@Composable
private fun FavoriteFlightsBody(
    favoriteFlights: List<Flight>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.favorite_routes),
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(8.dp)
        )
        FavoriteFlights(
            favoriteFlights = favoriteFlights,
            modifier = modifier
        )
    }
}

@Composable
private fun FavoriteFlights(
    favoriteFlights: List<Flight>,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = favoriteFlights, key = { it.arrivalAirportIata }) {
            FavoriteFlight(
                flight = it,
            )
            Divider()
        }
    }
}

@Composable
private fun FavoriteFlight(
    flight: Flight,
    modifier: Modifier = Modifier
) {
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
                        append(flight.selectedAirportIata)
                    }
                    append(" ")
                    append(flight.selectedAirportName)
                }
            )
            Text(
                text = stringResource(id = R.string.arrive),
                fontSize = 12.sp
            )
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(flight.arrivalAirportIata)
                    }
                    append(" ")
                    append(flight.arrivalAirportName)
                }
            )
        }
        IconButton(
            onClick = {
                Log.d(TAG, "clicked on ${flight.selectedAirportIata} - ${flight.arrivalAirportIata}")
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.non_favorite),
                tint = FavoriteStar,
                contentDescription = stringResource(id = R.string.favorite)
            )
        }
    }
}

@Composable
private fun AirportList(
    airportsList: List<Airport>,
    onAirportClick: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(items = airportsList, key = { it.id }) { airport ->
            AirportItem(airport = airport, onAirportClick = onAirportClick)
            Divider()
        }
    }
}

@Composable
private fun AirportItem(
    airport: Airport,
    onAirportClick: (Airport) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable { onAirportClick(airport) }
        .padding(vertical = 16.dp)
    ) {
        Text(
            text = airport.iataCode,
            modifier = Modifier.weight(1.0f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = airport.name,
            modifier = Modifier.weight(4.0f)
        )
    }
}