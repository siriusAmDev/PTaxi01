package com.sirius.net.ptaxi.ui.order

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
import com.sirius.net.ptaxi.adapters.OrdersAdapter
import com.sirius.net.ptaxi.databinding.FragmentOrderBinding
import com.sirius.net.ptaxi.model.DemandTaxi
import com.sirius.net.ptaxi.model.OffreTaxi
import com.sirius.net.ptaxi.model.User
import org.json.JSONArray
import org.json.JSONObject

class OrderFragment : Fragment() {

    private val viewModel: OrderViewModel by activityViewModels()
    private lateinit var binding: FragmentOrderBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var requestQueue: RequestQueue
    private lateinit var SharedPrefs: SharedPreferences
    private lateinit var ordersList: ArrayList<DemandTaxi>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_order, container, false)
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

        ordersList = ArrayList()
        getOrders()

    }

    private fun getOrders() {
        val url = "https://www.sirius-iot.eu/Dev/Tlink/Android_API_Partner.php?list_orders"
        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val jsonObject = jsonResponse.getJSONObject("ORDERS_LIST")
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

        }
        requestQueue.add(request)
    }

    private fun handleRequest(jsonArray: JSONArray) {
        for (i in 0 until jsonArray.length()) {
            val orderObject: JSONObject = jsonArray.getJSONObject(i)

            val id = orderObject.getString("id_order")
            val depart = orderObject.getString("depart")
            val depart_longitude = orderObject.getString("depart_longitude")
            val depart_latitude = orderObject.getString("depart_latitude")
            val destination = orderObject.getString("destination")
            val destination_longitude = orderObject.getString("destination_longitude")
            val destination_latitude = orderObject.getString("destination_latitude")
            val nbr_passengers = orderObject.getString("nbr_passengers")
            val heure = orderObject.getString("heure")
            val date = orderObject.getString("date")
            val note_order = orderObject.getString("note_order")
            var user: User? = null
            if (orderObject.has("INFOS_CLIENT")) {
                val customerObject = orderObject.getJSONObject("INFOS_CLIENT")
                val id_user = customerObject.getString("id_user")
                val name = customerObject.getString("name")
                val surname = customerObject.getString("surname")
                val mail = customerObject.getString("mail")
                val tel1 = customerObject.getString("tel1")
                val tel2 = customerObject.getString("tel2")
                val descrp = customerObject.getString("descrp")

                user = User(id_user, name, surname, mail, tel1, tel2, descrp)
            }

            ordersList.add(
                DemandTaxi(
                    id, depart, destination,
                    depart_longitude.toFloat(),
                    depart_latitude.toFloat(),
                    destination_longitude.toFloat(),
                    destination_latitude.toFloat(),
                    nbr_passengers.toInt(),
                    heure, date, "", 0, user!!
                )
            )

            recyclerView.adapter = OrdersAdapter(
                requireActivity().findNavController(R.id.nav_host_fragment),
                requireContext(),
                ordersList
            )
        }}
}