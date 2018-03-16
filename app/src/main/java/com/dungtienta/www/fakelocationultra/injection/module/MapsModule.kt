package com.dungtienta.www.fakelocationultra.injection.module

import android.content.Context
import android.location.LocationManager
import com.dungtienta.www.fakelocationultra.injection.ActivityScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import dagger.Module
import dagger.Provides

/**
 * Created by dungta on 3/15/18.
 */
@Module
class MapsModule
{
    @Provides
    @ActivityScope
    internal fun providesPlaceDetectionClient(context: Context): PlaceDetectionClient
    {
        return Places.getPlaceDetectionClient(context, null)
    }

    @Provides
    @ActivityScope
    internal fun providesFusedLocationProviderClient(context: Context): FusedLocationProviderClient
    {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @ActivityScope
    internal fun providesLocationManager(context: Context): LocationManager
    {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}