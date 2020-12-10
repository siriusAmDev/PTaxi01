package com.sirius.net.ptaxi.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.databinding.RegisterFragmentBinding
import com.sirius.net.ptaxi.model.VehicleBrand
import com.sirius.net.ptaxi.tools.Constants
import org.json.JSONObject
import java.util.ArrayList

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by activityViewModels()
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var registerButton: Button
    private lateinit var nameInput: TextView
    private lateinit var surnameInput: TextView
    private lateinit var phone1Input: TextView
    private lateinit var phone2Input: TextView
    private lateinit var emailInput: TextView
    private lateinit var passwordInput: TextView
    private lateinit var passwordConfirm: TextView
    private lateinit var requestQueue: RequestQueue

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.register_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requestQueue = Volley.newRequestQueue(requireContext())

        registerButton = binding.registerButton
        nameInput = binding.registerNameInput
        surnameInput = binding.registerSurnameInput
        phone1Input = binding.registerPhone1Input
        phone2Input = binding.registerPhone2Input
        emailInput = binding.registerEmailInput
        passwordInput = binding.registerPasswordInput
        passwordConfirm = binding.registerPasswordConfirm

      /*  nameInput.text = "h"
        surnameInput.text = "a"
        phone1Input.text = "0777777777"
        passwordInput.text = "123"
        passwordConfirm.text = "123"*/

        registerButton.setOnClickListener {
            verifyInputs()
        }

        Constants.brands = ArrayList()
        Constants.brandsNames = ArrayList()
        getVehicleBrands()
    }


    private fun getVehicleBrands() {
        val url = "https://www.sirius-iot.eu/Dev/Tlink/Android_API_Partner.php?list_brands"
        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                val jsonResponse = JSONObject(response)
                val jsonObject = jsonResponse.getJSONObject("BRANDS_LIST")
                if (jsonObject.getString("error") == "false") {
                    val jsonArray = jsonObject.getJSONArray("LIST")
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject1: JSONObject = jsonArray.getJSONObject(i)
                        val id = jsonObject1.getString("id_mrq")
                        val name = jsonObject1.getString("desc_mrq")
                        Constants.brands.add(VehicleBrand(id, name))
                    }

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
        ) {}
        requestQueue.add(request)
    }

    /**
     * pour verifier les input est ce qu'il ne sont pas vides
     */
    private fun verifyInputs() {
        val name = nameInput.text.toString()
        val surname = surnameInput.text.toString()
        val phone1 = phone1Input.text.toString()
        val phone2 = phone2Input.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val passwordC = passwordConfirm.text.toString()

        if (password.isNotEmpty() && passwordC.isNotEmpty() && phone1.isNotEmpty() && name.isNotEmpty() && surname.isNotEmpty()) {
            if (password.equals(passwordC)) {
                val navController =
                    requireActivity().findNavController(R.id.nav_host_fragment_login)

                val bundle = bundleOf(
                    "name" to name,
                    "surname" to surname,
                    "phone1" to phone1,
                    "phone2" to phone2,
                    "email" to email,
                    "password" to password
                )
                navController.navigate(R.id.nav_confirm_register, bundle)
            }
        }
    }

    /**
     * faire la requet de login
     */

}