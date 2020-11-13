package com.alex.gymcel.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alex.gymcel.R
import com.alex.gymcel.dataclass.Appointment
import com.alex.gymcel.utils.recyclerview.AppointmentAdapter
import com.alex.gymcel.viewmodel.GymcelViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val gymcelViewModel : GymcelViewModel by lazy {
        ViewModelProvider(this).get(GymcelViewModel::class.java)
    }

    var pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0F;


        createRecyclerView()

        gymcelViewModel.appointmentList.observe(this,
            { appointments ->

                progressBar.visibility = View.GONE
                viewAdapter.addAppointments(appointments)
            })

        gymcelViewModel.login()
    }

    private lateinit var viewAdapter: AppointmentAdapter
    private fun createRecyclerView(){

        // on click listener
        viewAdapter = AppointmentAdapter { appointment ->
            showCancelPopup(appointment)
        }

        appointmentRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = viewAdapter
        }

        // scrolling listener
        var recyclerIsLoading = false
        appointmentRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!recyclerIsLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == gymcelViewModel.appointmentList.value!!.size - 1) {

                        // bottom of recyclerview
                        pageNumber++
                        gymcelViewModel.getAppointments(pageNumber)
                        recyclerIsLoading = true
                    }
                }
            }
        })
    }




    private fun showCancelPopup(appointment: Appointment){

        AlertDialog.Builder(this)
            .setTitle("Delete Appointment")
            .setMessage("${appointment.location}\n${appointment.date} at ${appointment.time}")
            .setPositiveButton("Delete"
            ) { _, _ ->
                gymcelViewModel.deleteAppointment(appointment.cancelLink)
                viewAdapter.removeAppointment(appointment)
            }
            .setNegativeButton("Cancel"
            ) { _, _ ->}.apply {
                create()
                show()
            }
    }
}