package com.and04.naturealbum.ui.home

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient

class GPSHandler(
    private val activity: Activity,
    private val isGpsEnabled: (IntentSenderRequest) -> Unit,
    private val takePicture: () -> Unit,
) {
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationRequest: LocationRequest

    init {
        mLocationRequest = LocationRequest.create().apply {
            interval = 20 * 1000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        mSettingsClient = LocationServices.getSettingsClient(activity)
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

    fun startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(activity) {
                takePicture()
            }
            .addOnFailureListener(activity) { e ->
                if (e is ResolvableApiException) {
                    resolveLocationSettings(e)
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
