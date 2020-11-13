package com.alex.gymcel.request

import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.nio.charset.Charset

class VerificationTokenRequest (
    private val listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : Request<String> (Method.GET, "https://www.activityreg.ualberta.ca/UOFA/public/Logon/Logon", errorListener) {

    override fun deliverResponse(response: String) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {

        val rawHtml = String(
            response?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
        )

        val inputElements: Elements = Jsoup.parseBodyFragment(rawHtml).select("input[name=__RequestVerificationToken]")

        return if (inputElements.isNotEmpty() && inputElements.first().attr("value") != null)
            Response.success(inputElements.first().attr("value"), HttpHeaderParser.parseCacheHeaders(response))
        else
            Response.error(ParseError())

    }
}