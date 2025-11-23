package com.example.carebuddy.emergency

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

class LocationHelper(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLastLocation(onResult: (Double?, Double?) -> Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            onResult(null, null)
            return
        }

        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult(location.latitude, location.longitude)
                } else {
                    onResult(null, null)
                }
            }
            .addOnFailureListener {
                onResult(null, null)
            }
    }
}
