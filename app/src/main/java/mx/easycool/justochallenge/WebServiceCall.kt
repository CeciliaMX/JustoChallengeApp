package mx.pinturasosel.com

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WebServiceCall {
    fun WebServiceGetCall(webserviceUrl: String?, params: String): JSONObject {
        val url: URL
        var urlConnection: HttpURLConnection? = null
        var result = JSONObject()
        try {
            url = URL(webserviceUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Content-Type", "application/json")
            urlConnection!!.requestMethod = "GET"
            val postDataBytes = params.toByteArray(charset("UTF-8"))
            //urlConnection.outputStream.write(postDataBytes)
            val responseCode = urlConnection.responseCode
            val responseMessage = urlConnection.responseMessage
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = readStream(urlConnection.inputStream)
                val jObj = JSONObject(responseString)
                result = jObj
            } else {
                Log.v("", "Response message:$responseMessage")
            }
        } catch (e: Exception) {
            Log.v("", "Exception:" + e.message.toString())
        } finally {
            urlConnection?.disconnect()
        }
        return result
    }

    private fun readStream(`in`: InputStream): String {
        var reader: BufferedReader? = null
        val response = StringBuffer()
        try {
            reader = BufferedReader(InputStreamReader(`in`))
            var line: String? = ""
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return response.toString()
    }


}