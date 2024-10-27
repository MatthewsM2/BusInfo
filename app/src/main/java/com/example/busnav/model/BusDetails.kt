package com.example.busnav.model

data class BusDetails(
    val vehicle_number: String,
    val busName: String,
    val trip: Int,
    val fromLocation: String,
    val arrivalTime: String,
    val toLocation: String,
    val reachedTime: String
)