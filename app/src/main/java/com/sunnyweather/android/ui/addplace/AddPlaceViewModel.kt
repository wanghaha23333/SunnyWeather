package com.sunnyweather.android.ui.addplace

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceManage

class AddPlaceViewModel : ViewModel() {

    private val _addPlaceViewModel = MutableLiveData<PlaceManage>()
    private val _loadPlaceLiveData = MutableLiveData<List<PlaceManage>>()
    private val placeLocationLiveData = MutableLiveData<Location>()

    var locationLng = ""
    var locationLat = ""
    var placeName = ""
    var skyInfo = ""
    var temperature = 0.0f
    var placeList: List<PlaceManage> ?= null
//        fun isPlaceListInitialized() = ::placeList.isInitialized

    var isFound = false
    var rowId = -1L

    // 添加城市
    val addPlaceViewModel = Transformations.switchMap(_addPlaceViewModel) { placeManage ->
        Repository.insertPlace(placeManage)
    }
    fun addPlace() {
        _addPlaceViewModel.value = PlaceManage(placeName, locationLng, locationLat, skyInfo, temperature)
    }

    // 加载城市列表
    val loadPlaceLiveData = Transformations.switchMap(_loadPlaceLiveData) {
        Repository.loadAllPlaces()
    }

    fun loadPlace() {
        _loadPlaceLiveData.value = _loadPlaceLiveData.value
        Log.d("AddPlaceViewModel", "loadPlace")
    }

    // 请求天气
    fun place7Weather(lng: String, lat: String) {
        placeLocationLiveData.value = Location(lng, lat)
    }

    val placeWeather = Transformations.switchMap(placeLocationLiveData) { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    // 在城市列表中查找城市
    fun findPlace(placeManage: PlaceManage): Boolean {
        for (element in placeList!!) {
            if (element.place == placeManage.place) {
                rowId = element.id.toLong()
                return true
            }
        }
        return false
//        return placeList?.contains(placeManage)
    }
//    fun findPlace(placeManage: PlaceManage) : Boolean {
//        for (i in 0..placeList.size) {
//            Log.d("AddPlaceViewModel", "${placeList[i]}")
//            if (placeName == placeList[i].place) return true
//        }
//        return false
//    }

    // 将当前城市备份
    fun savePlace(place: Place) = Repository.savePlace(place)
}