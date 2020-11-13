package com.alex.gymcel.utils.recyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.alex.gymcel.R
import com.alex.gymcel.dataclass.Appointment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AppointmentAdapter(
    private val listener: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {

    private val appointments = ArrayList<Appointment>()

    interface OnItemClickListener {
        fun onItemClick(appointment: Appointment)
    }

    private val currentDate by lazy {
        val c = Calendar.getInstance().time

        val df = SimpleDateFormat("MMM-dd", Locale.US)
        df.format(c)
    }

    fun addAppointments(appointments: ArrayList<Appointment>){
        this.appointments.addAll(appointments)
        notifyDataSetChanged()
    }

    fun removeAppointment(appointment: Appointment){
        appointments.remove(appointment)
        notifyDataSetChanged()
    }

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val icon: ImageView = itemView.findViewById(R.id.appointment_icon)
        val location: TextView = itemView.findViewById(R.id.appointment_location)
        val time: TextView = itemView.findViewById(R.id.appointment_time)
        val date: TextView = itemView.findViewById(R.id.appointment_date)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.appointment_cardLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.appointment_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: AppointmentAdapter.ViewHolder, position: Int) {

        val appointment = appointments[position]

        viewHolder.itemView.setOnClickListener { listener(appointment) }

        viewHolder.location.text = appointment.location
        viewHolder.time.text = appointment.time
        viewHolder.date.text = appointment.date

        // swimming
        if (appointment.location.contains("Pool")){
            viewHolder.icon.setImageResource(R.drawable.pool)
        }
        // working out
        else{
            viewHolder.icon.setImageResource(R.drawable.dumbell)
        }

        // highlight current date
        if (appointment.date.contains(currentDate)) {
            viewHolder.constraintLayout.setBackgroundColor(Color.parseColor("#F9FCFF"));
        }
        else {
            viewHolder.constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }
}
