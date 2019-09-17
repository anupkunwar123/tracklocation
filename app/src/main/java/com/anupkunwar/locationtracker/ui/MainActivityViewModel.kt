package com.anupkunwar.locationtracker.ui

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anupkunwar.locationtracker.Result
import com.anupkunwar.locationtracker.di.ActivityScope
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import javax.inject.Inject

@ActivityScope
class MainActivityViewModel @Inject constructor(
    private val app: Application,
    private val locationRequest: LocationRequest
) : ViewModel() {

    companion object {
        const val LOCATION_TAG = "locationTag"
    }


    val locationSettingQueryResult = MutableLiveData<Result<Boolean>>()

    fun checkLocationSettingCriteria() {
        LocationServices.getSettingsClient(app)
            .checkLocationSettings(
                LocationSettingsRequest.Builder().addLocationRequest(
                    locationRequest
                ).setAlwaysShow(true).build()
            )
            .addOnSuccessListener {
                locationSettingQueryResult.value = Result.Success(true)
            }
            .addOnFailureListener {
                locationSettingQueryResult.value = Result.Success(false)
            }
    }


}