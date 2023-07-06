package com.sunnyweather.android.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.database.PlaceDatabase
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceManage
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

object Repository {

    val placeManageDao = PlaceDatabase.getDatabase(SunnyWeatherApplication.context).PlaceManageDao()

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                Log.d("Repository", "${dailyResponse.result.daily.skycon}")
                val weather = Weather(realtimeResponse.result.realtime,
                    dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }


    fun insertPlace(place: PlaceManage) = fire(Dispatchers.IO) {
        Log.d("Repository", "PlaceManage: insert Place 1, place.lng = ${place.lng}, place.lat = ${place.lat}")
        val rowId: Long
        val queryPlace = placeManageDao.queryPlaceByLngLat(place.lng, place.lat)
        Log.d("Repository", "PlaceManage: insert Place 2")
        if (queryPlace == null) {
            Log.d("Repository", "PlaceManage: insert Place 3")
            rowId = placeManageDao.insertPlace(place)
        } else {
            Log.d("Repository", "PlaceManage: insert Place 4")
            queryPlace.temperature = place.temperature
            queryPlace.skyInfo = place.skyInfo
            rowId = placeManageDao.updatePlace(queryPlace).toLong()
        }
        Log.d("Repository", "PlaceManage: insert id = $rowId")
        Result.success(rowId)
    }

    fun findPlaceById(id: Long) = fire(Dispatchers.IO) {
        Log.d("Repository", "PlaceManage: find place by id, id = $id")
        val placeManage = placeManageDao.queryPlaceById(id)
        Result.success(placeManage)
    }

    fun deletePlaceByLngLat(lng: String, lat: String) = fire(Dispatchers.IO){
        Log.d("Repository", "PlaceManage: delete Place")
        placeManageDao.deletePlaceByLngLat(lng, lat)
        val placeList = placeManageDao.loadAllPlaces()
        Result.success(placeList)
    }

    fun loadAllPlaces() = fire(Dispatchers.IO) {
        Log.d("Repository", "PlaceManage: load all Places")
        val placeList = placeManageDao.loadAllPlaces()
        Result.success(placeList)
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}