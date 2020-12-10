package com.sirius.net.ptaxi.ui.offre

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import com.sirius.net.ptaxi.adapters.OffresAdapter
import com.sirius.net.ptaxi.databinding.NewOfferFragmentBinding
import com.sirius.net.ptaxi.model.DateTime
import com.sirius.net.ptaxi.model.DemandTaxi
import com.sirius.net.ptaxi.model.OffreTaxi
import com.sirius.net.ptaxi.model.User
import com.sirius.net.ptaxi.tools.Constants
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NewOffreFragment : Fragment(), OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val viewModel: OffreViewModel by activityViewModels()
    private lateinit var binding: NewOfferFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestQueue: RequestQueue
    private lateinit var SharedPrefs: SharedPreferences
    private lateinit var offersList: ArrayList<OffreTaxi>

    private lateinit var mapView: MapView
    private lateinit var gMap: GoogleMap
    private val AUTOCOMPLETE_REQUEST_CODE = 1
    private val PERMISSION_REQUEST_CODE = 123
    private val LOCATION_CHECK_CODE = 200
    private val fields =
        listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
    private var point = ""
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null

    var currentDateTime = DateTime()
    var savedDateTime = DateTime()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_offer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        SharedPrefs = requireContext().getSharedPreferences("TLINK", Context.MODE_PRIVATE)
        requestQueue = Volley.newRequestQueue(requireContext())

//        offersList = ArrayList()
//        getOffers()

        checkLocationPermission()

        binding.confirmDirectionsCovOffer.setOnClickListener {
            //TODO post the request to the back en
            startDialog()
        }
        binding.departAdrCovOffer.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setHint("Rechercher votre place")
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            point = "depart"
        }
        binding.detinationAdrCovOffer.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setHint("Rechercher votre place")
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            point = "destination"
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap?) {
        gMap = map!!
        gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
        gMap.uiSettings?.isMyLocationButtonEnabled = false
        gMap.isMyLocationEnabled = true
        val algiers = LatLng(36.7525, 3.04197)
        startMarker = gMap.addMarker(
            MarkerOptions()
                .position(algiers)
                .title("Point de départ")
                .icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
                .draggable(true)
        )
        endMarker = gMap.addMarker(
            MarkerOptions()
                .position(algiers)
                .title("Destination")
                .icon(
                    BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
                .draggable(true)
                .visible(false)
        )
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(algiers, 10f))
        gMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(p0: Marker?) {}

            override fun onMarkerDrag(p0: Marker?) {}

            override fun onMarkerDragEnd(p0: Marker?) {
                //TODO get the position of the start marker and the end marker
                if (!endMarker!!.isVisible) {
                    endMarker!!.isVisible = true
                    gMap.moveCamera(CameraUpdateFactory.newLatLng(endMarker!!.position))
                }
            }
        })
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext()
                , Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext()
                , Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), PERMISSION_REQUEST_CODE
            )
        } else {
            Places.initialize(requireContext(), Constants.API_KEY)
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
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult().
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        LOCATION_CHECK_CODE
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }

    }

    private fun startDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.info_offer_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT
        )

        val todayButton: Button = dialog.findViewById(R.id.today_button)
        val dateButton: Button = dialog.findViewById(R.id.date_button)

        todayButton.setOnClickListener {
            // startSearch()
            // showOrdersList(ArrayList())
            val cal = Calendar.getInstance()
            currentDateTime.hour = cal.get(Calendar.HOUR)
            currentDateTime.minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(
                context,
                this,
                currentDateTime.hour,
                currentDateTime.minute,
                true
            ).show()
        }


        dateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            currentDateTime.day = cal.get(Calendar.DAY_OF_MONTH)
            currentDateTime.month = cal.get(Calendar.MONTH)
            currentDateTime.year = cal.get(Calendar.YEAR)
            currentDateTime.hour = cal.get(Calendar.HOUR)
            currentDateTime.minute = cal.get(Calendar.MINUTE)
            DatePickerDialog(
                requireContext(),
                this,
                currentDateTime.year,
                currentDateTime.month,
                currentDateTime.day
            ).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        //TODO post the info to the backend
        savedDateTime.day = dayOfMonth
        savedDateTime.month = month
        savedDateTime.year = year
        TimePickerDialog(context, this, currentDateTime.hour, currentDateTime.minute, true).show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        //TODO post the info to the backend and start the new fragment
        savedDateTime.hour = hourOfDay
        savedDateTime.minute = minute
        //showOrdersList(java.util.ArrayList())
        finalConfirmation()

    }

    private fun startSearch() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.searching_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT
            , ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        dialog.setCancelable(false)

        val cancelButton = dialog.findViewById<Button>(R.id.cancel_search_button)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

/*
    //the dialog initialisation for orders list
    private fun showOrdersList(ordersList: java.util.ArrayList<OrderCovoiturage>) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(com.google.android.gms.location.R.layout.cov_order_select)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT
            , ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val orderListRecycler = dialog.findViewById<RecyclerView>(com.google.android.gms.location.R.id.covOrders_recycler)
        val layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        val click = object : CovOrdersClick {
            override fun onClick(position: Int) {
                infoOrder(position)
                dialog.dismiss()
            }

        }
        val adapter = CovOrdersAdapter(ordersList, click)

        orderListRecycler.setHasFixedSize(false)
        orderListRecycler.layoutManager = layoutManager
        orderListRecycler.adapter = adapter
        dialog.show()

    }

    private fun infoOrder(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.info_cov_order)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT
            , ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val confirmButtonOrder = dialog.findViewById<Button>(com.google.android.gms.location.R.id.confirm_cov_order_button)
        //val note = dialog.findViewById<TextView>(R.id.note_demand)

        confirmButtonOrder.setOnClickListener {
            finalConfirmation(position)
            dialog.dismiss()
        }
        val backButtonOrder = dialog.findViewById<ImageView>(com.google.android.gms.location.R.id.back_image_order)
        //val note = dialog.findViewById<TextView>(R.id.note_demand)

        backButtonOrder.setOnClickListener {
            showOrdersList(java.util.ArrayList())
            dialog.dismiss()
        }
        dialog.show()
    }
*/

    private fun finalConfirmation() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.notes_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT
            , ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val confirmButton = dialog.findViewById<Button>(R.id.confirm_note_button)
        //val note = dialog.findViewById<TextView>(R.id.note_demand)

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        if (point == "depart") {
                            binding.departAdrCovOffer.text = place.name
                            startMarker?.position = place.latLng
                        } else if (point == "destination") {
                            binding.detinationAdrCovOffer.text = place.name
                            endMarker?.isVisible = true
                            endMarker?.position = place.latLng
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        Toast.makeText(
                            requireContext()
                            , "Erreur lors la recherche de la place, Réssayer s'il vous plait."
                            , Toast.LENGTH_LONG
                        ).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Places.initialize(requireContext(), Constants.API_KEY)
                mapView.getMapAsync(this)
                checkGps()
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(
                    requireContext(),
                    "L'application doit avoir votre localisation pour fonctionner."
                    ,
                    Toast.LENGTH_SHORT
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

/*
    private fun getOffers() {
        val url = "https://www.sirius-iot.eu/Dev/Tlink/Android_API_Partner.php?list_offers"
        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val jsonObject = jsonResponse.getJSONObject("OFFERS_LIST")
                if (jsonObject.getString("error") == "false") {
                    val jsonArray = jsonObject.getJSONArray("LIST")
                    handleRequest(jsonArray)
                } else {
                    Toast.makeText(
                        requireContext()
                        , jsonObject.getString("message"), Toast.LENGTH_LONG
                    ).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                requestQueue.stop()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params: HashMap<String, String> = HashMap()
                //Adding parameters to request
                params["idUser"] = SharedPrefs.getString("id_user", "").toString()
                //returning parameter
                return params;
            }

        }
        requestQueue.add(request)
    }

    private fun handleRequest(jsonArray: JSONArray) {
        for (i in 0 until jsonArray.length()) {
            val offerObject: JSONObject = jsonArray.getJSONObject(i)

            val id = offerObject.getString("id_offer")
            val depart = offerObject.getString("depart")
            val depart_longitude = offerObject.getString("depart_longitude")
            val depart_latitude = offerObject.getString("depart_latitude")
            val destination = offerObject.getString("destination")
            val destination_longitude = offerObject.getString("destination_longitude")
            val destination_latitude = offerObject.getString("destination_latitude")
            val num_place = offerObject.getString("num_place")
            val cost_offer = offerObject.getString("cost_offer")
            val depart_heure = offerObject.getString("depart_heure")
            val depart_date = offerObject.getString("depart_date")
            val id_user_partner = offerObject.getString("id_user_partner")
            val id_order = offerObject.getString("id_order")
            var user: User? = null
            if (id_order != null && id_order.isNotEmpty()) {
                val orderObject = offerObject.getJSONObject("INFOS_CLIENT")
                val id_user = offerObject.getString("id_user")
                val name = offerObject.getString("name")
                val surname = offerObject.getString("surname")
                val mail = offerObject.getString("mail")
                val tel1 = offerObject.getString("tel1")
                val tel2 = offerObject.getString("tel2")
                val descrp = offerObject.getString("descrp")

                user = User(id_user, name, surname, mail, tel1, tel2, descrp)
            }

            offersList.add(
                OffreTaxi(
                    id, depart, destination,
                    depart_longitude.toFloat(),
                    depart_latitude.toFloat(),
                    destination_longitude.toFloat(),
                    destination_latitude.toFloat(),
                    num_place.toInt(),
                    cost_offer.toFloat(),
                    depart_heure, depart_date, id_order, user
                )
            )
        }
        recyclerView.adapter = OffresAdapter(
            requireActivity().findNavController(R.id.nav_host_fragment),
            requireContext(),
            offersList
        )
    }*/
}