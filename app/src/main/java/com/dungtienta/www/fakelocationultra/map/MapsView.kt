package com.dungtienta.www.fakelocationultra.map

import android.location.Location
import com.dungtienta.www.fakelocationultra.base.BaseView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task

/**
 * Created by dungta on 3/15/18.
 */
interface MapsView: BaseView
{
    fun showEnableMockLocationError()

    fun moveCamera()

    fun addMarker(latLng: LatLng)

    fun moveCameraToDefaultLocation(locationResult: Task<Location>)
}