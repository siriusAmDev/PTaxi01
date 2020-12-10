package com.sirius.net.ptaxi.ui.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.activities.Login.LoginActivity
import com.sirius.net.ptaxi.adapters.BrandsAdapter
import com.sirius.net.ptaxi.databinding.RegisterVehicleFragmentBinding
import com.sirius.net.ptaxi.tools.Constants
import org.json.JSONObject
import kotlin.collections.set


class RegisterVehicleFragment : Fragment() {

    private val viewModel: RegisterVehicleViewModel by activityViewModels()
    private lateinit var binding: RegisterVehicleFragmentBinding
    private lateinit var registerButton: Button
    private lateinit var vehicleIdentifNum: TextView
    private lateinit var vehicleChassisNum: TextView

    private lateinit var vehicleBrand: AutoCompleteTextView
    private lateinit var vehicleModel: TextView

    //    private lateinit var vehicleType: TextView
    private lateinit var acceptConditions: CheckBox
    private lateinit var requestQueue: RequestQueue
    private lateinit var SharedPrefs: SharedPreferences
    private lateinit var brandsDataAdapter: BrandsAdapter

    var name = ""
    var surname = ""
    var phone1 = ""
    var phone2 = ""
    var email = ""
    var password = ""
    var id_brand = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.register_vehicle_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requestQueue = Volley.newRequestQueue(requireContext())
        SharedPrefs = requireContext().getSharedPreferences("TLINK", Context.MODE_PRIVATE)

        registerButton = binding.registerButton
        vehicleIdentifNum = binding.vehicleIdentifNum
        vehicleChassisNum = binding.vehicleChassisNum
        vehicleBrand = binding.vehicleBrand
        vehicleModel = binding.vehicleModel
        acceptConditions = binding.termCheckbox

  /*      vehicleIdentifNum.text = "0000000000"
        vehicleChassisNum.text = "00000000"
        vehicleModel.text = "model"
        acceptConditions.isChecked = true*/

        name = arguments?.getString("name").toString()
        surname = arguments?.getString("surname").toString()
        phone1 = arguments?.getString("phone1").toString()
        phone2 = arguments?.getString("phone2").toString()
        email = arguments?.getString("email").toString()
        password = arguments?.getString("password").toString()


        bindBrandsSpinner()

        //TODO faire la registration et naviguer vers la confirmation
        registerButton.setOnClickListener {
            verifyInputs()
        }
    }

    fun bindBrandsSpinner() {

        for (i in 0 until Constants.brands.size) {
            Constants.brandsNames.add(Constants.brands.get(i).name)
        }
        brandsDataAdapter = BrandsAdapter(requireActivity(), R.layout.brand_item, Constants.brands)

        vehicleBrand.setAdapter(brandsDataAdapter)

        vehicleBrand.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            id_brand = brandsDataAdapter.getItemId(position).toString()
        }

        vehicleBrand.setOnClickListener(View.OnClickListener { vehicleBrand.showDropDown() })
    }

    /**
     * pour verifier les input est ce qu'il ne sont pas vides
     */
    private fun verifyInputs() {
        val identifNum = vehicleIdentifNum.text.toString()
        val chassisNum = vehicleChassisNum.text.toString()
        val model = vehicleModel.text.toString()
        val type = "" //vehicleType.text.toString()
        val acceptTerms = acceptConditions.isChecked

        if (identifNum.isNotEmpty() && chassisNum.isNotEmpty() && id_brand.isNotEmpty() && model.isNotEmpty()) { //&& type.isNotEmpty()
            if (acceptTerms) {
                signOut(identifNum, chassisNum, id_brand, model, type, acceptTerms)
            }
        }
    }

    private fun signOut(
        identifNum: String,
        chassisNum: String,
        brand: String,
        model: String,
        type: String,
        acceptTerms: Boolean
    ) {

        val url = "https://www.sirius-iot.eu/Dev/Tlink/Android_API_Partner.php?signup_user_partner"
        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val jsonObject = jsonResponse.getJSONObject("USER_PARTNER_REGISTRATION")
                if (jsonObject.getString("error") == "false") {
                    handleRequest(jsonObject)
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
                params["name"] = name
                params["surname"] = surname
                params["mail"] = email
                params["tel1"] = phone1
                params["tel2"] = phone2
                params["descrp"] = ""
                params["password"] = password
                params["accept_cond_term"] = if (acceptTerms) "1" else "0"

                params["chassis_num_vehicule"] = chassisNum
                params["immatric_vehicule"] = identifNum
                params["brand_vehicule"] = brand
                params["model_vehicule"] = model
                params["descrp_vehicule"] = type
                return params;
            }

        }

        requestQueue.add(request)
    }

    private fun handleRequest(jsonObject: JSONObject) {
        Toast.makeText(
            requireContext()
            , "Bienvenu ${jsonObject.getString("name")}", Toast.LENGTH_LONG
        ).show()
        SharedPrefs.edit().putBoolean("IS_USER_LOGED", true).apply()
        SharedPrefs.edit().putString("id_user", jsonObject.getString("id")).apply()
        SharedPrefs.edit().putString("name", jsonObject.getString("name")).apply()
        SharedPrefs.edit().putString("surname", jsonObject.getString("surname")).apply()
        SharedPrefs.edit().putString("email", jsonObject.getString("mail")).apply()
        SharedPrefs.edit().putString("phone1", jsonObject.getString("tel1")).apply()
        SharedPrefs.edit().putString("phone2", jsonObject.getString("tel2")).apply()
        SharedPrefs.edit().putString("uid", jsonObject.getString("uid")).apply()
        SharedPrefs.edit().putString("id_vehicle", jsonObject.getString("id_vehicule")).apply()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent).also {
            requireActivity().finish()
        }
    }
}