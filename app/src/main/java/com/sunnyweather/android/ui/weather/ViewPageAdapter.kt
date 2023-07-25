package com.sunnyweather.android.ui.weather

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Location

class ViewPageAdapter(val activity: WeatherActivity) : FragmentStateAdapter(activity) {

    val viewModel by lazy {
        ViewModelProvider(activity)[WeatherViewModel::class.java]
    }

    companion object {
        const val TAG = "ViewPageAdapter"
    }

    // 返回 item 数量
    override fun getItemCount(): Int {
        return viewModel.placeList.size
    }

    // 创建 fragment
    // 啊 要怎么创建啊
    // 1. 根据 position 在城市列表中获取城市的经纬度，以及城市名
    // 2. 根据经纬度创建 WeatherFragment 对象，在 WeatherFragment 中进行填充
    override fun createFragment(position: Int): Fragment {
        Log.d(TAG, "createFragment")
        val place = viewModel.placeList[position] // 获取当前 position 对应的城市
        val location = Location(place.lng, place.lat)
        return WeatherFragment(location)
    }
}