package com.sirius.net.ptaxi.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.sirius.net.ptaxi.R
import com.sirius.net.ptaxi.model.VehicleBrand
import java.util.*


class BrandsAdapter(context: Activity, viewResourceId:Int, brands: ArrayList<VehicleBrand>) :
    ArrayAdapter<VehicleBrand>(context!!,  viewResourceId!!, brands!!) {
    private lateinit var view: View
    private var brands: ArrayList<VehicleBrand> = brands!!
    private var brandsAll: ArrayList<VehicleBrand> = brands!!.clone() as ArrayList<VehicleBrand>
    var suggestions = ArrayList<VehicleBrand>()
    private val viewResourceId:Int = viewResourceId!!


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        if (convertView == null) {
            val vi = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = vi.inflate(viewResourceId, parent, false)
        }
        val name: TextView = view.findViewById(R.id.brand_name)

        val brand: VehicleBrand? = getItem(position)
        if (brand != null) {
            name.text = brand.name
        }
        return view
    }

    override fun getCount(): Int {
        return brands.size
    }

    override fun getItemId(position: Int): Long {
        val brand : VehicleBrand? = getItem(position)
        return brand!!.id.toLong()
    }

    override fun getItem(position: Int): VehicleBrand {
        return brands[position]
    }

    override fun getFilter(): Filter {
        return nameFilter!!
    }

    var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return (resultValue as VehicleBrand).name
        }

        override fun performFiltering(constraint: CharSequence): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                for (brand in brandsAll) {
                    if (brand.name.toLowerCase()
                            .startsWith(constraint.toString().toLowerCase())
                    ) {
                        suggestions.add(brand)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(
            constraint: CharSequence?,
            results: FilterResults?
        ) {
            if (results != null && results.count > 0) {
                val filteredList: ArrayList<VehicleBrand> =
                    results!!.values as ArrayList<VehicleBrand>
                clear()
                for (c in filteredList) {
                    add(c)
                }
                notifyDataSetChanged()
            }
        }
    }

}