package com.sirius.net.ptaxi.ui.taxi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.adapters.TaxiOffersAdapter
import com.sirius.net.ptaxi.adapters.TaxiOffersClick
import com.sirius.net.ptaxi.databinding.TaxiFragmentBinding
import com.sirius.net.ptaxi.model.OffreTaxi


class TaxiFragment : Fragment(),OnMapReadyCallback {

    private val viewModel: TaxiViewModel by activityViewModels()
    private lateinit var binding: TaxiFragmentBinding
    private lateinit var mapView:MapView
    private lateinit var gMap:GoogleMap
    private val API_KEY = "AIzaSyCvYZHnDSX4RfKZp-zsZ5s91-_2H-7Fk-E"
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val PERMISSION_REQUEST_CODE = 123
    private val LOCATION_CHECK_CODE = 200
    private val fields = listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG)
    private var point = ""
    private var startMarker:Marker? = null
    private var endMarker:Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.taxi_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        checkLocationPermission()
        val cancelButton = binding.appeler
        cancelButton.setOnClickListener {
            appeler()
        }





    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        gMap = map!!
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.mapstyle))
        gMap.uiSettings?.isMyLocationButtonEnabled = false
        gMap.isMyLocationEnabled = true
        val algiers = LatLng(36.7525, 3.04197)
        startMarker = gMap.addMarker(
            MarkerOptions()
                .position(algiers)
                .title("Point de départ")
                .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true)
        )
        endMarker = gMap.addMarker(
            MarkerOptions()
                .position(algiers)
                .title("Destination")
                .icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true)
                .visible(false)
        )
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(algiers,10f))
        gMap.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(p0: Marker?) {}

            override fun onMarkerDrag(p0: Marker?) {}

            override fun onMarkerDragEnd(p0: Marker?) {
                //TODO get the position of the start marker and the end marker
                if(!endMarker!!.isVisible){
                    endMarker!!.isVisible = true
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(endMarker!!.position))
                }
            }
        })
    }

    private fun checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(requireContext()
                        , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext()
                        , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_REQUEST_CODE)
        }else{
            Places.initialize(requireContext(), API_KEY)
            mapView.getMapAsync(this)
            checkGps()
        }
    }

    private fun checkGps() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    // Show the dialog by calling startResolutionForResult().
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(),
                            LOCATION_CHECK_CODE)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    private fun startSearch() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.searching_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT
                , ConstraintLayout.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(false)

        val cancelButton = dialog.findViewById<Button>(R.id.cancel_search_button)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //the dialog initialisation for offers list
    private fun showOffersList(offersList: ArrayList<OffreTaxi>){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.offer_selection_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT
                , ConstraintLayout.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)

        val offerListRecycler = dialog.findViewById<RecyclerView>(R.id.offers_recycler)
        val layoutManager = LinearLayoutManager(requireContext(),VERTICAL,false)
        val click = object: TaxiOffersClick{
            override fun onClick(position: Int) {
                //finalConfirmation(position)
                dialog.dismiss()
            }
        }
        val adapter = TaxiOffersAdapter(offersList,click)

        offerListRecycler.setHasFixedSize(false)
        offerListRecycler.layoutManager = layoutManager
        offerListRecycler.adapter = adapter
        dialog.show()

    }

    private fun appeler() {


        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.arrive_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT
                , ConstraintLayout.LayoutParams.MATCH_PARENT)
        dialog.setCancelable(false)

        val cancelButton = dialog.findViewById<Button>(R.id.apayer)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Places.initialize(requireContext(), API_KEY)
                mapView.getMapAsync(this)
                checkGps()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(
                        requireContext(), "L'application doit avoir votre localisation pour fonctionner."
                        , LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

   override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}