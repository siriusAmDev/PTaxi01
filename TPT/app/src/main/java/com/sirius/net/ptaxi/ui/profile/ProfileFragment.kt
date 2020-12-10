package com.sirius.net.ptaxi.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.ui.AppBarConfiguration
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.databinding.FragmentProfileBinding
import org.json.JSONObject

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var requestQueue: RequestQueue
    private lateinit var SharedPrefs: SharedPreferences
    private lateinit var nameInput: TextView
    private lateinit var surnameInput: TextView
    private lateinit var phone1Input: TextView
    private lateinit var phone2Input: TextView
    private lateinit var emailInput: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SharedPrefs = requireContext().getSharedPreferences("TLINK", Context.MODE_PRIVATE)
        requestQueue = Volley.newRequestQueue(requireContext())

        nameInput = binding.profileNameInput
        surnameInput = binding.profileSurnameInput
        phone1Input = binding.profilePhoneInput
        phone2Input = binding.profilePhone2Input
        emailInput = binding.profileMailInput3

        getProfile()
    }

    private fun getProfile() {

        val url = "https://www.sirius-iot.eu/Dev/Tlink/Android_API_Partner.php?profil_user_partner"
        val request = object : StringRequest(
            Method.POST, url,
            {response ->
                val jsonResponse = JSONObject(response)
                val jsonObject = jsonResponse.getJSONObject("PARTNER_USER_PROFIL")
                if(jsonObject.getString("error") == "false"){
                    handleRequest(jsonObject)
                }else{
                    Toast.makeText(requireContext()
                        , jsonObject.getString("msg"), Toast.LENGTH_LONG).show()
                }
            },
            {error ->
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_LONG).show()
                requestQueue.stop()
            }
        ){
            override fun getParams():Map<String, String> {
                val params:HashMap<String,String> = HashMap()
                //Adding parameters to request
                params["id_user_partner"] = SharedPrefs.getString("id_user","").toString()
                //returning parameter
                return params;
            }

        }

        requestQueue.add(request)
    }

    private fun handleRequest(jsonObject: JSONObject) {
        nameInput.text = jsonObject.getString("name")
        surnameInput.text = jsonObject.getString("surname")
        phone1Input.text = jsonObject.getString("tel1")
        phone2Input.text = jsonObject.getString("tel2")
        emailInput.text = jsonObject.getString("mail")
    }
}

