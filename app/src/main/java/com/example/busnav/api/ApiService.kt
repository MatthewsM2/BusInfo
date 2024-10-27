package com.example.busnav.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Station data class for nested station details
data class Station(
    val station: String,
    val arrivalTime: String,
    val departureTime: String
)

data class BusDetail(
    val vehicle_number: String,
    val name: String,
    val trip: String,
    val stations: List<Station>
)

interface ApiService {
    @GET("api/v1/schedules")
    fun getBusSchedules(
        @Query("departure") departure: String,
        @Query("destination") destination: String
    ): Call<List<BusDetail>>
}
