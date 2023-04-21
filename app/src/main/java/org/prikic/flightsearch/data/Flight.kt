package org.prikic.flightsearch.data

data class Flight(
    val selectedAirportIata: String = "",
    val selectedAirportName: String = "",
    val arrivalAirportIata: String = "",
    val arrivalAirportName: String = "",
)
