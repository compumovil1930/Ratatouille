package edu.javeriana.ratatouille_chef_app.client_requests.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Request
import edu.javeriana.ratatouille_chef_app.core.distanceTo
import kotlin.math.round

class RequestAdapter(
    private val context: Context,
    private val items: List<Request>,
    private val locationAddress: LocationAddress
) : BaseAdapter() {

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return items.size
    }


    override fun getView(position: Int, contView: View?, parent: ViewGroup): View {
        var convertView = contView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.request_list_item, null, true)

            holder.clientName = convertView!!.findViewById(R.id.requestClientName) as TextView
            holder.address = convertView.findViewById(R.id.requestAddress) as TextView
            holder.description = convertView.findViewById(R.id.requestDescription) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }


        items[position].clientId?.get()?.addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            holder.clientName!!.text = user?.fullName
        }

        val distance = distanceTo(
            items[position].clientLocation.latitude,
            items[position].clientLocation.longitude,
            locationAddress.latitude,
            locationAddress.longitude
        ) / 1000.0



        val distanceString = "${String.format("$.2f", distance)} km"

        holder.address!!.text = distanceString

        items[position].clientLocation
        holder.description!!.text = items[position].description

        return convertView
    }

    private inner class ViewHolder {

        var clientName: TextView? = null
        var address: TextView? = null
        var description: TextView? = null

    }
}