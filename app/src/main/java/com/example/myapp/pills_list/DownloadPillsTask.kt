package com.example.myapp.pills_list

import android.os.AsyncTask
import android.util.Log
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class DownloadPillsTask(private val pillName: String) :
    AsyncTask<String, Void, ArrayList<String>>() {
    override fun doInBackground(vararg urls: String): ArrayList<String> {
        var items: ArrayList<String> = arrayListOf()

        try {
            val client = OkHttpClient()
            client.setConnectTimeout(300, TimeUnit.MILLISECONDS) // Ustawienie limitu czasu połączenia
            client.setReadTimeout(300, TimeUnit.MILLISECONDS) // Ustawienie limitu czasu odczytu

            val url = "http://192.168.137.104:5000/search/" + pillName// IP na którym stoi serwer
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
            val responseData = response.body()?.string()

            val jsonObject = JSONObject(responseData)
            val resultArray = jsonObject["result"] as JSONArray

            for (i in 0 until resultArray.length()) {
                val item = resultArray[i]
                items.add(item.toString())
            }
            } else {
                Log.d("Błąd", "Błąd połączenia: ${response.code()}")
            }
        } catch (e: IOException) {
            Log.d("Błąd", "Błąd połączenia: ${e.message}")
        }
        return items
    }
}