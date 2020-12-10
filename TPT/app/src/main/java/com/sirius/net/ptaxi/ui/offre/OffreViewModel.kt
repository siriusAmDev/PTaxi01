package com.sirius.net.ptaxi.ui.offre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OffreViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is offre Fragment"
    }
    val text: LiveData<String> = _text
}