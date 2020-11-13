package com.alex.gymcel.request

import android.util.Log
import com.alex.gymcel.dataclass.Appointment
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import org.jsoup.Jsoup
import java.nio.charset.Charset

class AppointmentRequest(
    val pageNumber: Int,
    private val appointmentListener: Response.Listener<ArrayList<Appointment>>,
    errorListener: Response.ErrorListener
) : Request<ArrayList<Appointment>>(
    Method.GET,
    "https://www.activityreg.ualberta.ca/UOFA/members/Bookings/AppointmentsPage?Page=$pageNumber",
    errorListener
) {

    override fun deliverResponse(response: ArrayList<Appointment>) = appointmentListener.onResponse(
        response
    )

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ArrayList<Appointment>> {

        Log.d(
            "here",
            "https://www.activityreg.ualberta.ca/UOFA/members/Bookings/Appointments?Page=$pageNumber"
        )
        val rawHtml = String(
            response?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
        )

        val tableElement =  Jsoup.parseBodyFragment(rawHtml).select("table[class=table table-striped]")

        val appointmentsElements = tableElement.select("tr")

        val appointments = ArrayList<Appointment>()

        for (x in 1 until appointmentsElements.size){

            val dateAndTime = appointmentsElements[x].select("td")[1].text()

            val dateTimeArray = dateAndTime.split("(?<=\\s\\S{1,100})\\s".toRegex()).toTypedArray()

            appointments.add(
                Appointment(
                    dateAndTime.substring(0, 11),
                    dateAndTime.substring(12),
                    "https://www.activityreg.ualberta.ca${appointmentsElements[x].select("a[class=btn btn-danger]").attr("href").replace("CancelConfirmation","Cancel")}",
                    appointmentsElements[x].select("td")[5].text()
                )
            )
        }

        return Response.success(
            appointments,
            HttpHeaderParser.parseCacheHeaders(response)
        )
    }
}
