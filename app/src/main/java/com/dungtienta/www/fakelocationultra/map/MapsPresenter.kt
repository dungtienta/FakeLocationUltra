package com.dungtienta.www.fakelocationultra.map

import android.location.Location
import android.os.SystemClock
import android.util.Log
import com.dungtienta.www.fakelocationultra.base.BasePresenter
import com.dungtienta.www.fakelocationultra.injection.component.DaggerMapsComponent
import com.dungtienta.www.fakelocationultra.injection.module.ContextModule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

/**
 * Created by dungta on 3/15/18.
 */
class MapsPresenter(val mapsView: MapsView) : BasePresenter<MapsView>(mapsView)
{
    override fun inject()
    {
        DaggerMapsComponent.builder()
                .contextModule(ContextModule(view.getContext()))
                .build()
                .inject(this)
    }

    @Inject lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    fun setMockLocation(latLng: LatLng)
    {
        val PROVIDER_NAME = "poop"

        val newLocation = Location(PROVIDER_NAME)

        newLocation.latitude = latLng.latitude
        newLocation.longitude = latLng.longitude
        newLocation.time = System.currentTimeMillis()

        newLocation.accuracy = 6.toFloat()
        newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();

        try
        {
            mFusedLocationProviderClient.setMockMode(true)
            mFusedLocationProviderClient.setMockLocation(newLocation).addOnCompleteListener {
                Log.d("poop", "Fuse location update complete")
                mapsView.addMarker(latLng)
            }
        }
        catch (e: SecurityException)
        {
            // Send the user to developer options to enable mock locations
            mapsView.showEnableMockLocationError()
        }
    }

    fun getDeviceLocation()
    {
        try
        {
            val locationResult = mFusedLocationProviderClient.lastLocation
            mapsView.moveCameraToDefaultLocation(locationResult)
        }
        catch (e: SecurityException)
        {
            // Send the user to developer options to enable mock locations
            mapsView.showEnableMockLocationError()
        }
    }
}
