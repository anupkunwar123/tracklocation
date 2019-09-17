package com.anupkunwar.locationtracker.ui

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import com.anupkunwar.locationtracker.*
import com.anupkunwar.locationtracker.databinding.ActivityMainBinding
import com.anupkunwar.locationtracker.di.ViewModelFactory
import com.anupkunwar.locationtracker.service.LocationService
import com.google.android.gms.common.api.ResolvableApiException
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {


    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private lateinit var viewModel: MainActivityViewModel

    companion object {
        const val FINE_LOCATION_PERMISSION = 100
        const val LOCATION_RESOLUTION_REQUEST = 101
    }

    private var alertDialog: AlertDialog? = null
    var isTracking = ObservableField(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.handler = this

        isTracking.set(isLocationServiceRunning())
        viewModel = getViewModel(viewModelFactory)

        viewModel.locationSettingQueryResult.observe(this, Observer {
            when (it) {
                is Result.Success -> {
                    startLocationTracking()
                }

                is Result.Error -> {
                    if (it.exception is ResolvableApiException) {
                        try {
                            it.exception.startResolutionForResult(
                                this@MainActivity,
                                LOCATION_RESOLUTION_REQUEST
                            )
                        } catch (e: Exception) {

                        }

                    }
                }
            }
        })


    }


    private fun startLocationTracking() {
        if (isGpsProviderEnabled()) {
            isTracking.set(true)
            startService(Intent(this, LocationService::class.java))

        } else {
            viewModel.checkLocationSettingCriteria()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOCATION_RESOLUTION_REQUEST && resultCode == Activity.RESULT_OK) {
            startLocationTracking()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        alertDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            FINE_LOCATION_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationTracking()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            permissions[0]
                        )
                    ) {
                        shortToast(getString(R.string.turn_on_location_info))
                    }
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun checkFineLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                showAlertDialog(
                    getString(R.string.permission_needed),
                    getString(R.string.rational),
                    DialogInterface.OnClickListener { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            FINE_LOCATION_PERMISSION
                        )
                    },
                    getString(R.string.ok),
                    null,
                    getString(R.string.cancel)
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_PERMISSION
                )
            }
        } else {

            startLocationTracking()

        }
    }

    fun stopLocationUpdate() {
        stopService(Intent(this, LocationService::class.java))
        isTracking.set(false)
    }

    private fun showAlertDialog(
        title: String?,
        message: String,
        onPositiveButtonClickListener: DialogInterface.OnClickListener?,
        positiveText: String,
        onNegativeButtonClickListener: DialogInterface.OnClickListener?,
        negativeText: String
    ) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, onPositiveButtonClickListener)
            .setNegativeButton(negativeText, onNegativeButtonClickListener)
        alertDialog = builder.show()
    }

    private fun isLocationServiceRunning(): Boolean {
        var serviceRunning = false
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.getRunningServices(50)
        val i = l.iterator()
        while (i.hasNext()) {
            val runningServiceInfo = i
                .next()

            if (runningServiceInfo.service.className == LocationService::class.java.name) {
                serviceRunning = true

            }
        }
        return serviceRunning
    }

}
