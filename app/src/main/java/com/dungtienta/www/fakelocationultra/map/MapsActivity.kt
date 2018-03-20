package com.dungtienta.www.fakelocationultra.map

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.dungtienta.www.fakelocationultra.R
import com.dungtienta.www.fakelocationultra.base.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task

class MapsActivity : BaseActivity(), MapsView, OnMapReadyCallback
{
    companion object
    {
        val TAG = MapsActivity::class.simpleName
    }

    private lateinit var presenter: MapsPresenter

    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private var mCameraPosition: CameraPosition? = null

    // Default location to the happiest place on earth.
    private val mDefaultLocation = LatLng(33.8121, -117.9190)
    private var mLastKnownLocation: Location? = null

    // Keys for storing activity state.
    private val KEY_CAMERA_POSITION = "camera_position"
    private val KEY_LOCATION = "location"

    private var mLocationPermissionGranted: Boolean = false
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private val DEFAULT_ZOOM = 15

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // Retrieve location and camera position from saved instance state
        if (savedInstanceState != null)
        {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        presenter = MapsPresenter(this)
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle?)
    {
        if (mMap != null)
        {
            outState!!.putParcelable(KEY_CAMERA_POSITION, mMap?.cameraPosition)
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
            super.onSaveInstanceState(outState)
        }
    }

    override fun onResume()
    {
        super.onResume()
        updateLocationUI()
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.maps_menu, menu);
        return true
    }

    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId) {

            R.id.menu_developer_options -> goToDeveloperSettings()
            R.id.menu_location_history -> throw IllegalStateException("Need to implement location history")
        }
        return true
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        mMap?.setOnMapClickListener { latLng: LatLng? ->
            Log.d("poop", "Map clickd");
            if (latLng != null)
            {
                presenter.setMockLocation(latLng)
            }
        }

        // Prompt the user for permission
        getLocationPermission()
    }

    private fun getDeviceLocation()
    {
        if (mLocationPermissionGranted)
        {
            presenter.getDeviceLocation()
        }
    }

    override fun moveCameraToDefaultLocation(locationResult: Task<Location>)
    {
        locationResult.addOnCompleteListener(this) { task ->
            if (task.isSuccessful)
            {
                // Set the map's camera position to the current location of the device.
                mLastKnownLocation = task.result
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(mLastKnownLocation!!.latitude,
                                mLastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
            }
            else
            {
                Log.d(MapsActivity.TAG, "Current location is null. Using defaults.")
                Log.e(MapsActivity.TAG, "Exception: " + task.exception?.message)
                mMap?.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM.toFloat()))
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
            }
        }
    }

    override fun addMarker(latLng: LatLng)
    {
        currentMarker?.remove()

        currentMarker = mMap?.addMarker(MarkerOptions()
                .title("Fake Location")
                .position(latLng!!)
                .snippet("Lat: " + latLng?.latitude + " Long: " + latLng?.longitude))
    }

    private fun showCurrentLocation()
    {
        if (mLocationPermissionGranted)
        {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            mMap?.addMarker(MarkerOptions()
                    .title("Default shit location.")
                    .position(mDefaultLocation)
                    .snippet("Stuff"))

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray)
    {
        when (requestCode)
        {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ->
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true
                    updateLocationUI()
                }
            }
        }
        getDeviceLocation()
    }

    private fun getLocationPermission()
    {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true
            updateLocationUI()
            getDeviceLocation()
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI()
    {
        try
        {
            if (mLocationPermissionGranted)
            {
                mMap?.isMyLocationEnabled = true
                mMap?.uiSettings?.isMyLocationButtonEnabled = true
            }
            else
            {
                mMap?.isMyLocationEnabled = false
                mMap?.uiSettings?.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
            }
        }
        catch (e: SecurityException)
        {
            Log.e("Exception: %s", e.message)
        }
    }

    override fun moveCamera()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEnableMockLocationError()
    {
        //TODO: Preferably show a dialog explaining why you're sending the user to developer settings
        goToDeveloperSettings()
    }
}
