package com.sirius.net.ptaxi.ui.login

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.activities.Main.MainActivity
import com.sirius.net.ptaxi.databinding.LoginFragmentBinding
import org.json.JSONObject

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var binding: LoginFragmentBinding
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var phoneNumberInput: TextView
    private lateinit var passwordInput: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var SharedPrefs:SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
                .inflate(layoutInflater, R.layout.login_fragment,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        SharedPrefs = requireContext().getSharedPreferences("TLINK",MODE_PRIVATE)
        if(SharedPrefs.getBoolean("IS_USER_LOGED",false)){
            val intent  = Intent(requireActivity(),MainActivity::class.java)
            startActivity(intent).also {
                requireActivity().finish()
            }
        }
        requestQueue = Volley.newRequestQueue(requireContext())

        loginButton = binding.loginButton
        registerButton = binding.registerLaunchButton
        phoneNumberInput = binding.loginPhoneInput
        passwordInput = binding.loginPasswordInput

        loginButton.setOnClickListener {
            verifyInputs()
        }

        registerButton.setOnClickListener {
            val navController = requireActivity()
                    .findNavController(R.id.nav_host_fragment_login)
            navController.navigate(R.id.action_register)
        }
    }

    /**
     * pour verifier les input est ce qu'il ne sont pas vides
     */
    private fun verifyInputs() {
        val phoneNumber = phoneNumberInput.text.toString()
        val password = passwordInput.text.toString()

        if(password.isNotEmpty() && phoneNumber.isNotEmpty()){
            signIn(phoneNumber,password)
        }
    }

    /**
     * faire la requet de login
     */
    private fun signIn(userPhone: String, password: String) {

        val url = "https://www.sirius-iot.eu/Dev/Tlink/Android_API_Partner.php?login_user_partner"
        val request = object : StringRequest(Method.POST, url,
                {response ->
                    val jsonResponse = JSONObject(response)
                    val jsonObject = jsonResponse.getJSONObject("LOGGED_IN_USER")
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
                params["telephone"] = userPhone
                params["password"] = password
                //returning parameter
                return params;
            }

        }

        requestQueue.add(request)
    }

    private fun handleRequest(jsonObject: JSONObject) {
        Toast.makeText(requireContext()
                , "Bienvenu ${jsonObject.getString("name")}", Toast.LENGTH_LONG).show()
        SharedPrefs.edit().putBoolean("IS_USER_LOGED",true).apply()
        SharedPrefs.edit().putString("id_user", jsonObject.getString("id")).apply()
        SharedPrefs.edit().putString("name",jsonObject.getString("name")).apply()
        SharedPrefs.edit().putString("surname",jsonObject.getString("surname")).apply()
        SharedPrefs.edit().putString("mail",jsonObject.getString("mail")).apply()
        SharedPrefs.edit().putString("tel1",jsonObject.getString("tel1")).apply()
        SharedPrefs.edit().putString("tel2",jsonObject.getString("tel2")).apply()
        SharedPrefs.edit().putString("uid",jsonObject.getString("uid")).apply()
        SharedPrefs.edit().putString("id_vehicle", jsonObject.getString("id_vehicule")).apply()

        val intent  = Intent(requireActivity(),MainActivity::class.java)
        startActivity(intent).also {
            requireActivity().finish()
        }
    }

}