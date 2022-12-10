package skosemani.example.projecttwo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import skosemani.example.projecttwo.databinding.ActivityMapsBinding
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val REQUEST_LOCATION_PERMISSION = 1
    private  lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lat: Double = 0.0
    private  var lon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        detectUserLocation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.google_maps -> {
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.google_places -> {
            val intent = Intent(this, PlacesActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.send_email -> {
            val intent = Intent(this, EmailActivity::class.java)
            startActivity(intent)
            true
        }
        R.id.about_app -> {
            val intent = Intent(this, AboutUsActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }



    private fun getAddress(loc:LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(loc!!.latitude, loc!!.longitude, 1)
        } catch (e1: IOException) {
            Log.e("Geocoding", getString(R.string.problem), e1)
        } catch (e2: IllegalArgumentException) {
            Log.e("Geocoding", getString(R.string.invalid)+
                    "Latitude = " + loc!!.latitude +
                    ", Longitude = " +
                    loc!!.longitude, e2)
        }
        // If the reverse geocode returned an address
        if (addresses != null) {
            // Get the first address
            val address = addresses[0]
            val addressText = String.format(
                "%s, %s, %s",
                address.getAddressLine(0),
                address.locality,
                address.countryName)
            return addressText
        }
        else
        {
            Log.e("Geocoding", getString(R.string.noaddress))
            return ""
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                detectUserLocation()
            }
        }
    }

    private fun isPermissionGranted () : Boolean{
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    @SuppressLint("MissingPermission")
    private fun detectUserLocation(){
        if(isPermissionGranted()){
            mMap.isMyLocationEnabled = true
            fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                    task ->
                val location: Location?= task.result
                if(location == null){
                }else{
                    lat = location.latitude
                    lon = location.longitude

                    setSharedPreferences("LAT", lat.toString())
                    setSharedPreferences("LON", lon.toString())

                    if(lat != 0.0 && lon != 0.0 ){
                        var mylocation = LatLng(lat, lon)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(mylocation)
                                .title(getAddress(mylocation))
                                .snippet("Salem Kosemani LAT AND LON: $lat $lon")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )
                        getAddress(mylocation)?.let { setSharedPreferences("ADDRESS", it) }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation,15f))
                        mMap.uiSettings.isZoomControlsEnabled = true
                    }
                }
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
    }
    private fun setSharedPreferences(key: String, value: String){
        val sharedPreference = getSharedPreferences("MAPSTORE", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(key, value)
        editor.commit()
    }
}