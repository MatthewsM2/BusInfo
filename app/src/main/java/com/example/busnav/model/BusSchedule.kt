package com.example.busnav.model

data class BusSchedule(
    val vehicle_number: String,
    val trip: Int,
    val stations: List<Station>
)