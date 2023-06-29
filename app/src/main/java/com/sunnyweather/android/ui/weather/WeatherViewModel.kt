package com.sunnyweather.android.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location
import com.sunnyweather.android.logic.model.PlaceManage

class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()
    private val _loadPlaceLiveData = MutableLiveData<List<PlaceManage>>()
    private val _deletePlaceLiveData = MutableLiveData<PlaceManage>()
    private val _findPlaceById = MutableLiveData<Long>()

    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    var placeList: List<PlaceManage> ?= null
    var placeManage: PlaceManage ?= null

    // 请求天气数据
    val weatherLiveData = Transformations.switchMap(locationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
        Log.d("WeatherViewModel", "refreshWeather: ${Location(lng, lat)}")
    }

    // 加载城市列表
    val loadPlaceLiveData = Transformations.switchMap(_loadPlaceLiveData) {
        Repository.loadAllPlaces()
    }

    fun loadPlace() {
        _loadPlaceLiveData.value = _loadPlaceLiveData.value
    }

    // 删除城市
    val deletePlaceLivaData = Transformations.switchMap(_deletePlaceLiveData) { placeManage ->
        Repository.deletePlace(placeManage)
    }

    fun deletePlace(place: PlaceManage) {
        _deletePlaceLiveData.value = place
    }

    // 根据id查询城市
    val findPlaceById = Transformations.switchMap(_findPlaceById) { id ->
        Repository.findPlaceById(id)
    }
    fun findPlaceById(id: Long) {
        _findPlaceById.value = id
    }
}