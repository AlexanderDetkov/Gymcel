package com.alex.gymcel.request

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class LoginRequest (
    private val usr: String,
    private val pwd: String,
    private val responseListener: Response.Listener<Boolean>,
    errorListener: Response.ErrorListener) : Request<Boolean>
    (Method.POST, "https://www.activityreg.ualberta.ca/UOFA/public/Logon/Logon", errorListener) {

    override fun getParams(): MutableMap<String, String>
            = mutableMapOf("EmailAddress" to usr, "Password" to pwd, "ReturnUrl" to "")

    override fun deliverResponse(response: Boolean) = responseListener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<Boolean>{

        var loginSuccess = true



        return Response.success(
            loginSuccess,
            HttpHeaderParser.parseCacheHeaders(response))

    }


}