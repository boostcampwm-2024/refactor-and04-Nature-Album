package com.and04.naturealbum.ui.home

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

class GPSHandler(
    private val context: Context,
    private val takePicture: () -> Unit,
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_LOW_POWER,
        0L,
    ).build()
    private val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    private val client: SettingsClient = LocationServices.getSettingsClient(context)

    fun startLocationUpdates(showGPSActivationDialog: (IntentSenderRequest) -> Unit) {
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                takePicture()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    resolveLocationSettings(exception, showGPSActivationDialog)
                }
            }
    }

    private fun resolveLocationSettings(
        resolvable: ResolvableApiException,
        showGPSActivationDialog: (IntentSenderRequest) -> Unit,
    ) {
        val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()
        try {
            showGPSActivationDialog(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            Log.d("FFFF", "${location.latitude}, ${location.longitude}")
        }
    }
}
