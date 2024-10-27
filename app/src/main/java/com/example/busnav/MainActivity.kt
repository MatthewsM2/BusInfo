// File: MainActivity.kt
package com.example.busnav

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.busnav.api.BusDetail
import com.example.busnav.api.RetrofitClient
import com.example.busnav.model.BusDetails
import com.example.busnav.ui.theme.BusNavTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusNavTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BusRouteInputFields(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusRouteInputFields(modifier: Modifier = Modifier) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }
    var busDetailsList by remember { mutableStateOf<List<BusDetails>>(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = fromLocation,
            onValueChange = { fromLocation = it },
            label = { Text("From") },
            placeholder = { Text("Enter starting location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = toLocation,
            onValueChange = { toLocation = it },
            label = { Text("To") },
            placeholder = { Text("Enter destination") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                Log.d("RouteSearch", "Searching route from $fromLocation to $toLocation")
                fetchBusSchedules(fromLocation, toLocation) { fetchedBusDetails ->
                    busDetailsList = fetchedBusDetails
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Route")
        }

        CardList(busDetailsList)
    }
}

private fun fetchBusSchedules(departure: String, destination: String,onBusDetailsFetched: (List<BusDetails>) -> Unit) {
    val apiService = RetrofitClient.apiService
    val call = apiService.getBusSchedules(departure, destination)

    call.enqueue(object : Callback<List<BusDetail>> {
        override fun onResponse(call: Call<List<BusDetail>>, response: Response<List<BusDetail>>) {
            if (response.isSuccessful) {


                Log.e("API_RESPONSE_RAW", response.raw().toString())
                val busDetailsList = response.body() ?: emptyList()

                Log.d("API_RESPONSE", "Fetched ${busDetailsList.size} bus details")
                val gson = Gson()
                val busDetailsJson = gson.toJson(busDetailsList)
                Log.d("API_RESPONSE", "Fetched bus details: $busDetailsJson")

                val fetchedBusDetails = busDetailsList.map { busDetail ->
                    val busName = busDetail.name ?: "Unavailable"
                    val firstStation = busDetail.stations.firstOrNull()?.station ?: "N/A"
                    val firstStationArrival = busDetail.stations.firstOrNull()?.arrivalTime ?: "N/A"
                    val dest = busDetail.stations.find { it.station.equals(destination, ignoreCase = true) }
                    val toLoc = dest?.station?:"Unavilable"
                    val reach = dest?.arrivalTime?:"N/A"
                    BusDetails(
                        vehicle_number = busDetail.vehicle_number,
                        busName = busName, // Or get bus name from API response if available
                        trip = busDetail.trip.toIntOrNull() ?: 0, // Handle potential null values
                        fromLocation = firstStation, // Use the provided departure location
                        arrivalTime = firstStationArrival,
                        toLocation = toLoc, // Use the provided destination location
                        reachedTime = reach
                    )
                }

                onBusDetailsFetched(fetchedBusDetails)
            } else {
                Log.e("API_RESPONSE", "Error: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<List<BusDetail>>, t: Throwable) {
            Log.e("API_RESPONSE", "Failed to fetch data: ${t.message}")
        }
    })
}



@Composable
fun CardList(busDetailsList: List<BusDetails>) {
//    val busDetailsList = listOf(
//        BusDetails(
//            vehicle_number = "KL 05 AB 1234",
//            busName = "Express Bus",
//            trip = 1,
//            fromLocation = "City A",
//            arrivalTime = "09:00 AM",
//            toLocation = "City B",
//            reachedTime = "11:30 AM"
//        ),
//        BusDetails(
//            vehicle_number = "KL 05 AB 5678",
//            busName = "Local Bus",
//            trip = 2,
//            fromLocation = "City C",
//            arrivalTime = "10:15 AM",
//            toLocation = "City D",
//            reachedTime = "01:45 PM"
//        )
//    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(busDetailsList) { busDetails ->
            RouteCard(busDetails = busDetails)
        }
    }
}

@Composable
fun RouteCard(busDetails: BusDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bus Reg. Number: ${busDetails.vehicle_number}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = "Bus Name: ${busDetails.busName}",
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = "Trip Number: ${busDetails.trip}",
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "From: ${busDetails.fromLocation} - Arrival at: ${busDetails.arrivalTime}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Text(
                text = "To: ${busDetails.toLocation} - Reached at: ${busDetails.reachedTime}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusRouteInputFieldsPreview() {
    BusNavTheme {
        BusRouteInputFields()
    }
}
