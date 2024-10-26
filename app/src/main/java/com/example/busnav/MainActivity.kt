package com.example.busnav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.busnav.ui.theme.BusNavTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

// Data class to represent bus details
data class BusDetails(
    val regNumber: String,
    val busName: String,
    val tripNumber: Int,
    val fromLocation: String,
    val arrivalTime: String,
    val toLocation: String,
    val reachedTime: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusRouteInputFields(modifier: Modifier = Modifier) {
    var fromLocation by remember { mutableStateOf("") }
    var toLocation by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "From" TextField for source location
        OutlinedTextField(
            value = fromLocation,
            onValueChange = { fromLocation = it },
            label = { Text("From") },
            placeholder = { Text("Enter starting location") },
            modifier = Modifier.fillMaxWidth()
        )

        // "To" TextField for destination location
        OutlinedTextField(
            value = toLocation,
            onValueChange = { toLocation = it },
            label = { Text("To") },
            placeholder = { Text("Enter destination") },
            modifier = Modifier.fillMaxWidth()
        )

        // Search Route Button
        Button(
            onClick = {
                // Handle search or route calculation logic here
                println("Searching route from $fromLocation to $toLocation")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Route")
        }
        CardList()
    }
}

@Composable
fun CardList() {
    // Sample data for bus details
    val busDetailsList = listOf(
        BusDetails(
            regNumber = "KL 05 AB 1234",
            busName = "Express Bus",
            tripNumber = 1,
            fromLocation = "City A",
            arrivalTime = "09:00 AM",
            toLocation = "City B",
            reachedTime = "11:30 AM"
        ),
        BusDetails(
            regNumber = "KL 05 AB 5678",
            busName = "Local Bus",
            tripNumber = 2,
            fromLocation = "City C",
            arrivalTime = "10:15 AM",
            toLocation = "City D",
            reachedTime = "01:45 PM"
        )
        // Add more BusDetails items as needed
    )

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
                text = "Bus Reg. Number: ${busDetails.regNumber}",
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
                text = "Trip Number: ${busDetails.tripNumber}",
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
