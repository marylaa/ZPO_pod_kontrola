package com.example.myapp.pills_list

import android.os.AsyncTask
import android.util.Log
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONArray
import org.json.JSONObject

class DownloadPillsTask(private val pillName: String) :
    AsyncTask<String, Void, ArrayList<String>>() {
    override fun doInBackground(vararg urls: String): ArrayList<String> {
        val client = OkHttpClient()
        val url = "http://192.168.1.4:5000/search/" + pillName// IP na którym stoi serwer

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        var items: ArrayList<String> = arrayListOf()

        try {
            val response = client.newCall(request).execute()
            val responseData = response.body()?.string()

            val jsonObject = JSONObject(responseData)
            val resultArray = jsonObject["result"] as JSONArray

            for (i in 0 until resultArray.length()) {
                val item = resultArray[i]
                items.add(item.toString())
            }
        } catch (e: RuntimeException) {
            Log.d("Błąd", e.stackTrace.toString())
        }
        return items
    }
}