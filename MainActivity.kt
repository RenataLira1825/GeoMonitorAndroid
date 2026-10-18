package com.example.geomonitor
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.CurrentLocationRequest

class MainActivity : ComponentActivity() {
    private var message by mutableStateOf("Toque no botão para consultar sua localização.")
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        if (grants.values.any { it }) getLocation() else message = "Permissão não concedida."
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GeoMonitor", style = MaterialTheme.typography.headlineLarge)
                        Spacer(Modifier.height(12.dp))
                        Text("Sua localização, com sua permissão.")
                        Spacer(Modifier.height(24.dp))
                        Text(message)
                        Spacer(Modifier.height(20.dp))
                        Button(onClick = { requestLocation() }) { Text("Obter minha localização") }
                    }
                }
            }
        }
    }
    private fun requestLocation() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) getLocation() else permissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
    private fun getLocation() {
        try {
            val req = CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).build()
            LocationServices.getFusedLocationProviderClient(this).getCurrentLocation(req, null)
                .addOnSuccessListener { loc -> message = if (loc != null) "Latitude: %.5f\nLongitude: %.5f".format(loc.latitude, loc.longitude) else "Localização indisponível. Confira o GPS." }
                .addOnFailureListener { message = "Não foi possível obter a localização." }
        } catch (_: SecurityException) { message = "Permissão de localização não concedida." }
    }
}
