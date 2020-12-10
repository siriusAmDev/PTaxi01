package com.sirius.net.ptaxi.ui.confirmation

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.activities.Main.MainActivity
import com.sirius.net.ptaxi.databinding.ConfirmFragmentBinding

class ConfirmFragment : Fragment() {

    private val viewModel: ConfirmViewModel by activityViewModels()
    private lateinit var binding:ConfirmFragmentBinding
    private lateinit var confirmButton: Button
    private lateinit var forgetAccountButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.confirm_fragment, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        confirmButton = binding.confirmButton
        forgetAccountButton = binding.forgetUserAccount

        //TODO confirmer l'utilisateur et naviguer vers le main activity
        confirmButton.setOnClickListener {
            requireContext().startActivity(Intent(requireContext(),MainActivity::class.java)).also {
                requireActivity().finish()
            }
        }

        //TODO surprimmer le compte et retourner vers le login
        forgetAccountButton.setOnClickListener {
//            val navController = requireActivity().findNavController(R.id.nav_host_fragment_login)
//            navController.navigate(R.id.nav_delete_acc)
        }
    }

}