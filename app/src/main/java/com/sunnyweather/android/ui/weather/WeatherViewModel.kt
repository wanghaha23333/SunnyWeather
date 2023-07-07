package com.sunnyweather.android.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceManage

class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()
    private val _loadPlaceLiveData = MutableLiveData<List<PlaceManage>>()
    private val _deletePlaceLiveData = MutableLiveData<PlaceManage>()
    private val _findPlaceByLngLat = MutableLiveData<Location>()
    private val _updatePlaceViewModel = MutableLiveData<PlaceManage>()

    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    var skyInfo = ""
    var temperature = 0.0f
    var placeList = ArrayList<PlaceManage>()

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
        Repository.deletePlaceByLngLat(placeManage.lng, placeManage.lat)
    }

    fun deletePlace(place: PlaceManage) {
        _deletePlaceLiveData.value = place
    }

    // 添加城市
    val updatePlaceViewModel = Transformations.switchMap(_updatePlaceViewModel) { placeManage ->
        Repository.insertPlace(placeManage)
    }
    fun updatePlace() {
        _updatePlaceViewModel.value = PlaceManage(placeName, locationLng, locationLat, skyInfo, temperature)
    }

    // 根据id查询城市
    val findPlaceLiveData = Transformations.switchMap(_findPlaceByLngLat) { location ->
        Repository.findPlaceByLngLat(location.lng, location.lat )
    }
    fun findPlaceByLngLat(location: Location) {
        _findPlaceByLngLat.value = location
    }

    fun savePlace(place: Place) = Repository.savePlace(place)
}