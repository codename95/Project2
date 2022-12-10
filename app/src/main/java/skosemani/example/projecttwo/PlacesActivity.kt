package skosemani.example.projecttwo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import kotlin.collections.ArrayList

class PlacesActivity : AppCompatActivity() {

    private lateinit var recyclerview: RecyclerView


    private var mPlacesClient: PlacesClient? = null
    private val M_MAX_ENTRIES = 5
    private lateinit var mLikelyPlaceNames: Array<String>
    private lateinit var mLikelyPlaceAddresses: java.util.ArrayList<String>
    private lateinit var mLikelyPlaceLatLngs: java.util.ArrayList<LatLng>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)

        val apiKey = getString(R.string.api_key)
        Places.initialize(applicationContext, apiKey)
        mPlacesClient = Places.createClient(this)
        mLikelyPlaceNames = arrayOf<String>("","","","","")
        mLikelyPlaceAddresses = java.util.ArrayList<String>(5)
        mLikelyPlaceLatLngs = java.util.ArrayList<LatLng>(5)


        recyclerview = findViewById<RecyclerView>(R.id.recyclerview)

        recyclerview.layoutManager = LinearLayoutManager(this)

        getCurrentPlaceLikelihoods()

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        val placeFields = Arrays.asList(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        val placeResponse: Task<FindCurrentPlaceResponse> =
            mPlacesClient!!.findCurrentPlace(request)
        placeResponse.addOnCompleteListener(this,
            OnCompleteListener<FindCurrentPlaceResponse?> { task ->
                if (task.isSuccessful) {
                    val response = task.result
                    // Set the count, handling cases where less than 5 entries are returned.
                    val count: Int
                    if (response.placeLikelihoods.size < M_MAX_ENTRIES) {
                        count = response.placeLikelihoods.size
                    } else {
                        count = M_MAX_ENTRIES
                    }
                    println("Found a place")
                    var i = 0
                    for (placeLikelihood: PlaceLikelihood in response.placeLikelihoods) {
                        val currPlace = placeLikelihood.place
                        mLikelyPlaceNames[i] = (currPlace.name)
                        Log.i("OREA",currPlace.name)
                        mLikelyPlaceAddresses.add(currPlace.address)
                        mLikelyPlaceLatLngs.add(currPlace.latLng)
                        val currLatLng =
                            if (mLikelyPlaceLatLngs[i] == null) "" else mLikelyPlaceLatLngs[i].toString()
                        Log.i(
                            "OREA", String.format(
                                "Place " + currPlace.name
                                        + " has likelihood: " + placeLikelihood.likelihood
                                        + " at " + currLatLng
                            )
                        )
                        i++
                        if (i > (count - 1)) {
                            break
                        }
                    }

                    // Populate and refresh the RecyclerView
                    println(mLikelyPlaceNames)
                    //recyclerView.adapter = RecyclerAdapter(mLikelyPlaceNames)  // pass in data to be displayed
                    //viewAdapter.notifyDataSetChanged()

                    val data = ArrayList<PlacesViewModel>()

                    for (i in mLikelyPlaceNames) {
                        data.add(PlacesViewModel(" $i"))
                    }

                    val adapter = CustomAdapter(data)

                    recyclerview.adapter = adapter

                } else {
                    val exception: Exception? = task.getException()
                    if (exception is ApiException) {
                        Log.e("OREA", "Place not found: " + exception.statusCode)
                    }
                }
            })
    }

}