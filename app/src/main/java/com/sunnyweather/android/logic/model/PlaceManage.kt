package com.sunnyweather.android.logic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlaceManage(var place: String, var lng: String, var lat: String,
                       var skyInfo: String, var temperature: Float) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}