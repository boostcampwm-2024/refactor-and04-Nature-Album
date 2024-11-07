package com.and04.naturealbum.ui.home

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient

class GPSHandler(
    activity: Activity,
    private val isGpsEnabled: (IntentSenderRequest) -> Unit,
    private val takePicture: () -> Unit,
) {
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_LOW_POWER,
        0L,
    ).build()
    private val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    private val client: SettingsClient = LocationServices.getSettingsClient(activity)

    fun startLocationUpdates() {
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                takePicture()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    resolveLocationSettings(exception)
                }
            }
    }

    private fun resolveLocationSettings(exception: Exception) {
        val resolvable = exception as ResolvableApiException
        val intentSenderRequest = IntentSenderRequest.Builder(resolvable.resolution).build()
        try {
            isGpsEnabled(intentSenderRequest)
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }
}
