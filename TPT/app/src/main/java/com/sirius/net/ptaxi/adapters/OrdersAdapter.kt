package com.sirius.net.ptaxi.adapters

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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.model.DateTime
import com.sirius.net.ptaxi.model.DemandTaxi
import com.sirius.net.ptaxi.model.OffreTaxi
import org.json.JSONObject
import java.util.*

class OrdersAdapter(val navController: NavController, context: Context, orders: ArrayList<DemandTaxi>)
    :RecyclerView.Adapter<OrdersAdapter.OrderHolder>()
        , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    var currentDateTime = DateTime()
    var savedDateTime = DateTime()
    var context = context
    var orders = orders
    var requestQueue = Volley.newRequestQueue(context)

    override fun getItemCount(): Int = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
       val inflater = LayoutInflater.from(parent.context)
        context = parent.context
        return OrderHolder(inflater,parent)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        var order = orders[position]
        if (order.user != null)
            holder.customerName.text = order.user!!.name + " " + order.user!!.surname
        else
            holder.customerName.text ="/"
        holder.orderDate.text = order.departDate
        holder.orderHeure.text = order.departTime
        holder.orderDepart.text = order.adrDeparture
        holder.orderDest.text = order.adrDestination
//        holder.orderImage.setImageResource(R.drawable.ic_taxi)

        holder.orderContainer.setOnClickListener {
        }
        
        holder.newOfferButton.setOnClickListener {
            navController.navigate(R.id.nav_new_offer)
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

    class OrderHolder(inflater:LayoutInflater,parent:ViewGroup)
        :RecyclerView.ViewHolder(inflater.inflate(R.layout.order_item,parent,false)) {
        val customerName:TextView = itemView.findViewById(R.id.customer)
        val orderDate:TextView = itemView.findViewById(R.id.date)
        val orderHeure:TextView = itemView.findViewById(R.id.heure)
        val orderDest:TextView = itemView.findViewById(R.id.dest)
        val orderDepart:TextView = itemView.findViewById(R.id.depart)
        val orderImage:ImageView = itemView.findViewById(R.id.imageVieworder)
        val newOfferButton:TextView = itemView.findViewById(R.id.faire_offre)
        val orderContainer:ConstraintLayout = itemView.findViewById(R.id.main_container)
    }


}