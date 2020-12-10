package com.sirius.net.ptaxi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.model.OffreTaxi

interface TaxiOffersClick {
    fun onClick(position: Int)
}

class TaxiOffersAdapter(offersList: ArrayList<OffreTaxi>?,var clickListener:TaxiOffersClick)
    :RecyclerView.Adapter<TaxiOffersAdapter.TaxiOffersHolder>(){

    private var adapterOffersList: ArrayList<OffreTaxi> = ArrayList()

    init {
        if(offersList != null)
            adapterOffersList = offersList
    }

    override fun getItemCount(): Int = 6 //adapterOffersList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaxiOffersHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TaxiOffersHolder(inflater,parent,clickListener)
    }

    override fun onBindViewHolder(holder: TaxiOffersHolder, position: Int) {
        //val item = adapterOffersList[position]

        //holder.price.text = item.price.toString()
    }



    class TaxiOffersHolder(inflater: LayoutInflater,parent: ViewGroup,var clickListener:TaxiOffersClick)
        :RecyclerView.ViewHolder(inflater.inflate(R.layout.offer_item,parent,false)),
    View.OnClickListener{

       // val price:TextView = itemView.findViewById(R.id.prix_offre)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener.onClick(adapterPosition)
        }
    }
}
