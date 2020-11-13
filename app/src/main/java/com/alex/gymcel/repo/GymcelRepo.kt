package com.alex.gymcel.repo

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.alex.gymcel.dataclass.Appointment
import com.alex.gymcel.request.AppointmentRequest
import com.alex.gymcel.request.LoginRequest
import com.alex.gymcel.request.VerificationTokenRequest
import com.alex.gymcel.utils.networking.VolleyRequestSingleton
import com.alex.gymcel.utils.singleton.SingletonHolder
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.net.CookieStore


class GymcelRepo private constructor(private val applicationContext: Context) {
    companion object : SingletonHolder<GymcelRepo, Context>(::GymcelRepo)

    //  networking singletons
    private val requestQueue: RequestQueue by lazy {
        VolleyRequestSingleton.getInstance(applicationContext).requestQueue
    }
    private val persistentCookieStore: CookieStore by lazy {
        VolleyRequestSingleton.getInstance(applicationContext).persistentCookieStore
    }

    fun login(
        usr: String,
        pwd: String,
        loginListener: Response.Listener<Boolean>,
        errorListener: Response.ErrorListener
    ){

        // check if usr is logged in
        var usrIsLoggedIn = false
        for (cookie in persistentCookieStore.cookies){
            if (cookie.name == ".ASPXAUTH" && !cookie.hasExpired()){
                usrIsLoggedIn = true
                break
            }
        }

        // if usr is not logged in
        if (!usrIsLoggedIn){

            Log.d("here", "getting verification token")

            // get verification token
            requestQueue.add(
                VerificationTokenRequest(
                    { verificationToken ->
                        Log.d("here", "logging in usr")


                        // has verification token
                        // login
                        requestQueue.add(
                            LoginRequest(
                                usr,
                                pwd,
                                verificationToken,
                                loginListener,
                                errorListener
                            )
                        )
                    },
                    errorListener
                )
            )
        }
        else{

            // usr is logged in
            Log.d("here", "usr is logged in")
            loginListener.onResponse(true)
        }
    }

    fun getAppointments(
        pageNumber: Int,
        appointmentListener: Response.Listener<ArrayList<Appointment>>,
        errorListener: Response.ErrorListener
    ){

        val appointmentRequest = AppointmentRequest(pageNumber, appointmentListener, errorListener)
        appointmentRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(appointmentRequest)
    }

    fun deleteAppointment(appointmentURL: String){

        // add cancel appointment to cart
        requestQueue.add(StringRequest(
            Request.Method.GET,
            appointmentURL,
            { _ ->

                // checkout basket
                requestQueue.add(StringRequest(
                    Request.Method.GET,
                    "https://www.activityreg.ualberta.ca/UOFA/public/Basket/CheckoutBasket",
                    { _ ->
                        Toast.makeText(applicationContext, "Appointment Deleted", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Toast.makeText(applicationContext, "Unable to Delete Appointment", Toast.LENGTH_LONG).show()
                })

            }
        ) { error ->
            Toast.makeText(applicationContext, "Unable to Delete Appointment", Toast.LENGTH_LONG).show()
        })
    }

    fun clearCookies(){
        persistentCookieStore.removeAll()
    }
}