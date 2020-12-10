package com.sirius.net.ptaxi.ui.offre

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.adapters.OffresAdapter
import com.sirius.net.ptaxi.databinding.FragmentOfferBinding
import com.sirius.net.ptaxi.model.DemandTaxi
import com.sirius.net.ptaxi.model.OffreTaxi
import com.sirius.net.ptaxi.model.User
import org.json.JSONArray
import org.json.JSONObject

class OffreFragment : Fragment() {

    private val viewModel: OffreViewModel by activityViewModels()
    private lateinit var binding: FragmentOfferBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestQueue: RequestQueue
    private lateinit var SharedPrefs: SharedPreferences
    private lateinit var offersList: ArrayList<OffreTaxi>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_offer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.optionsRecycler
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1, VERTICAL, false)
        recyclerView.setHasFixedSize(false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        SharedPrefs = requireContext().getSharedPreferences("TLINK", Context.MODE_PRIVATE)
        requestQueue = Volley.newRequestQueue(requireContext())

        offersList = ArrayList()
        getOffers()

    }

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
    }
}