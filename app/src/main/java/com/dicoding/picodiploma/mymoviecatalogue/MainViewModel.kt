package com.dicoding.picodiploma.mymoviecatalogue

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.dicoding.picodiploma.mymoviecatalogue.entity.Data
import com.dicoding.picodiploma.mymoviecatalogue.entity.DataReview
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask


class MainViewModel : ViewModel() {

    companion object {
        private const val URL_POSTER = "https://image.tmdb.org/t/p/w500"
        private const val URL_BACKDROP = "https://image.tmdb.org/t/p/w500"
        private const val TAG = "MainViewModel"
    }

    val listData = MutableLiveData<ArrayList<Data>>()
    val listReview = MutableLiveData<ArrayList<DataReview>>()
    val dataDetail = MutableLiveData<HashMap<String, String?>>()

    internal fun setDataDiscover(context: Context, hashMap: HashMap<String, String>) {
        AndroidNetworking.initialize(context)
        val listItems = ArrayList<Data>()
        AndroidNetworking.get(hashMap["url"])
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(response: JSONObject) {
                    kotlin.run {
                        try {
                            val json = response.getJSONArray("results")
                            for (position in 0 until json.length()) {
                                val x = json.getJSONObject(position)
                                val release: String
                                if (hashMap["category"] == "movie") {
                                    release = if (x.getString("release_date") != "") {
                                        LocalDate.parse(x.getString("release_date")).format(
                                            DateTimeFormatter.ofPattern("yyyy")
                                        )
                                    } else {
                                        "Not Available"
                                    }
                                    val data =
                                        Data(
                                            x.getString("id"),
                                            URL_POSTER + "" + x.getString("poster_path"),
                                            URL_BACKDROP + "" + x.getString("backdrop_path"),
                                            x.getString("title"),
                                            release,
                                            x.getString("vote_average")
                                        )
                                    listItems.add(data)
                                } else {
                                    release = if (x.getString("first_air_date") != "") {
                                        LocalDate.parse(x.getString("first_air_date")).format(
                                            DateTimeFormatter.ofPattern("yyyy")
                                        )
                                    } else {
                                        "Not Available"
                                    }
                                    val data =
                                        Data(
                                            x.getString("id"),
                                            URL_POSTER + "" + x.getString("poster_path"),
                                            URL_BACKDROP + "" + x.getString("backdrop_path"),
                                            x.getString("original_name"),
                                            release,
                                            x.getString("vote_average")
                                        )
                                    listItems.add(data)
                                }
                            }
                            listData.postValue(listItems)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(anError: ANError) {
                    Timer().schedule(timerTask {

                        Looper.prepare()

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(hashMap["title"])
                        builder.setMessage(hashMap["message"])

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        Log.e(TAG, "onError: $anError") //untuk log pada onerror

                        Looper.loop()
                    }, 10000)
                }
            })
    }

    internal fun getDataDiscover(): LiveData<ArrayList<Data>> {
        return listData
    }

    internal fun setDataDetail(context: Context, hashMap: HashMap<String, String>) {
        AndroidNetworking.initialize(context)
        val param = HashMap<String, String?>()
        AndroidNetworking.get(hashMap["url"])
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(response: JSONObject) {
                    kotlin.run {
                        try {
                            val jsonGenre = response.getJSONArray("genres")
                            val genre = ArrayList<String>()
                            for (i in 0 until jsonGenre.length()) {
                                genre.add(jsonGenre.getJSONObject(i).getString("name"))
                            }

                            val overview: String?
                            overview = if (response.getString("overview") == "")
                                hashMap["null_overview"]
                            else {
                                response.getString("overview")
                            }

                            val country: String
                            if (hashMap["category"] == "Movies" || hashMap["category"] == "Film") {
                                country =
                                    if (response.getJSONArray("production_countries").length() != 0) {
                                        val jsonCountries =
                                            response.getJSONArray("production_countries")
                                        val countries = ArrayList<String>()
                                        for (j in 0 until jsonCountries.length()) {
                                            countries.add(jsonCountries.getJSONObject(j).getString("name"))
                                        }
                                        countries.toString().replace("[", "").replace("]", "")
                                    } else {
                                        "-"
                                    }

                                param["id"] = response.getString("id")
                                param["backdrop"] =
                                    URL_BACKDROP + "" + response.getString("backdrop_path")
                                param["homepage"] = response.getString("homepage")
                                param["rating"] = response.getString("vote_average")
                                param["judul"] = response.getString("original_title")
                                param["poster_path"] =
                                    URL_POSTER + "" + response.getString("poster_path")
                                param["overview"] = overview
                                param["genre"] = genre.toString().replace("[", "").replace("]", "")
                                param["durasi"] =
                                    response.getString("runtime") + " " + hashMap["menit"]
                                param["tahun"] = LocalDate.parse(response.getString("release_date"))
                                    .format(DateTimeFormatter.ofPattern("yyyy"))
                                param["popularity"] = response.getString("popularity")
                                param["country"] = country
                                param["category"] = "Movies"
                            } else {
                                country =
                                    if (response.getJSONArray("origin_country").length() != 0) {
                                        response.getJSONArray("origin_country").toString()
                                            .replace("[\"", "").replace("\"]", "")
                                    } else {
                                        "-"
                                    }

                                param["id"] = response.getString("id")
                                param["backdrop"] =
                                    URL_BACKDROP + "" + response.getString("backdrop_path")
                                param["homepage"] = response.getString("homepage")
                                param["rating"] = response.getString("vote_average")
                                param["judul"] = response.getString("original_name")
                                param["poster_path"] =
                                    URL_POSTER + "" + response.getString("poster_path")
                                param["overview"] = overview
                                param["genre"] = genre.toString().replace("[", "").replace("]", "")
                                param["durasi"] =
                                    response.getString("number_of_seasons") + " ${hashMap["season"]} : ${response.getString(
                                        "number_of_episodes"
                                    )} ${hashMap["episode"]}"
                                param["tahun"] =
                                    LocalDate.parse(response.getString("first_air_date"))
                                        .format(DateTimeFormatter.ofPattern("yyyy"))
                                param["popularity"] = response.getString("popularity")
                                param["country"] = country
                                param["category"] = "Tv"
                            }
                            dataDetail.postValue(param)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e(TAG, "onError: $anError") //untuk log pada onerror
                }
            })
    }

    internal fun getDataDetail(): LiveData<HashMap<String, String?>> {
        return dataDetail
    }

    internal fun setDataReview(context: Context, hashMap: HashMap<String, String>) {
        AndroidNetworking.initialize(context)
        val listItems = ArrayList<DataReview>()
        AndroidNetworking.get(hashMap["url"])
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(response: JSONObject) {
                    kotlin.run {
                        try {
                            if (response.getString("total_results").toInt() == 0) {
                                val data =
                                    DataReview(
                                        "null",
                                        "null",
                                        "null",
                                        "null"
                                    )
                                listItems.add(data)
                            } else {
                                val json = response.getJSONArray("results")
                                for (position in 0 until json.length()) {
                                    val x = json.getJSONObject(position)
                                    val data =
                                        DataReview(
                                            x.getString("id"),
                                            x.getString("author"),
                                            x.getString("content"),
                                            x.getString("url")
                                        )
                                    listItems.add(data)
                                }
                            }
                            listReview.postValue(listItems)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(anError: ANError) {
                    Timer().schedule(timerTask {
                        Looper.prepare()

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle(hashMap["title"])
                        builder.setMessage(hashMap["message"])

                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        Log.e(TAG, "onError: $anError") //untuk log pada onerror

                        Looper.loop()
                    }, 10000)
                }
            })
    }

    internal fun getDataReview(): LiveData<ArrayList<DataReview>> {
        return listReview
    }
}