package com.sirius.net.ptaxi.adapters

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.model.DateTime
import com.sirius.net.ptaxi.model.OffreTaxi
import java.util.*

class OffresAdapter(val navController: NavController, context: Context, offers: ArrayList<OffreTaxi>)
    :RecyclerView.Adapter<OffresAdapter.OfferHolder>()
        , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var currentDateTime = DateTime()
    var savedDateTime = DateTime()
     var context = context
     var offers = offers


    override fun getItemCount(): Int = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferHolder {
       val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return OfferHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: OfferHolder, position: Int) {
        var offer = offers[position]
        if (offer.user != null)
            holder.customerName.text = offer.user!!.name + " " + offer.user!!.surname
        else
            holder.customerName.text ="/"
        holder.offerDate.text = offer.departDate
        holder.offerHeure.text = offer.departTime
        holder.offerDepart.text = offer.adrDeparture
        holder.offerDest.text = offer.adrDestination
        holder.offerPrice.text = offer.price.toString()
        holder.offerImage.setImageResource(R.drawable.ic_taxi)
        holder.offerContainer.setOnClickListener {

        }

    }

    private fun startDialogFlow() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.date_choice_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.window!!.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT
                , ConstraintLayout.LayoutParams.MATCH_PARENT)

        val todayButton:Button = dialog.findViewById(R.id.today_button)
        val dateButton:Button = dialog.findViewById(R.id.date_button)

        todayButton.setOnClickListener {
            //navController.navigate(R.id.nav_to_taxi)
            dialog.dismiss()
        }

        dateButton.setOnClickListener {
            val cal = Calendar.getInstance()
            currentDateTime.day = cal.get(Calendar.DAY_OF_MONTH)
            currentDateTime.month = cal.get(Calendar.MONTH)
            currentDateTime.year = cal.get(Calendar.YEAR)
            currentDateTime.hour = cal.get(Calendar.HOUR)
            currentDateTime.minute = cal.get(Calendar.MINUTE)
            DatePickerDialog(context,this,currentDateTime.year,currentDateTime.month,currentDateTime.day).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        //TODO post the info to the backend
        savedDateTime.day = dayOfMonth
        savedDateTime.month = month
        savedDateTime.year = year

        TimePickerDialog(context,this,currentDateTime.hour,currentDateTime.minute,true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        //TODO post the info to the backend and start the new fragment
        savedDateTime.hour = hourOfDay
        savedDateTime.minute = minute
      //  navController.navigate(R.id.nav_to_taxi)
    }

    class OfferHolder(inflater:LayoutInflater,parent:ViewGroup)
        :RecyclerView.ViewHolder(inflater.inflate(R.layout.offer_item,parent,false)) {
        val customerName:TextView = itemView.findViewById(R.id.customer)
        val offerDate:TextView = itemView.findViewById(R.id.date)
        val offerHeure:TextView = itemView.findViewById(R.id.heure)
        val offerDest:TextView = itemView.findViewById(R.id.dest)
        val offerDepart:TextView = itemView.findViewById(R.id.depart)
        val offerImage:ImageView = itemView.findViewById(R.id.imageViewOffer)
        val offerPrice:TextView = itemView.findViewById(R.id.price)
        val offerContainer:ConstraintLayout = itemView.findViewById(R.id.main_container)
    }


}