package com.example.sempertibi

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonRequest

class HtmlRequest(
    method: Int,
    url: String,
    requestBody: String?,
    listener: Response.Listener<String>,
    errorListener: Response.ErrorListener
) : JsonRequest<String>(method, url, requestBody, listener, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
        val parsed = String(response?.data ?: ByteArray(0))
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response))
    }

}
