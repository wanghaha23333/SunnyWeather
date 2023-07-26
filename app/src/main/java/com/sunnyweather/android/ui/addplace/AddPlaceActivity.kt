package com.sunnyweather.android.ui.addplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.databinding.ActivityAddplaceBinding
import com.sunnyweather.android.logic.model.*
import com.sunnyweather.android.ui.weather.WeatherActivity
import java.text.SimpleDateFormat
import java.util.*

class AddPlaceActivity : AppCompatActivity() {

    val viewModel by lazy {
        ViewModelProvider(this)[AddPlaceViewModel::class.java]
    }

    private lateinit var binding: ActivityAddplaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddplaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }

        binding.placeNameAp.text = viewModel.placeName
        binding.refreshWeather.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
        place7Weather()
        binding.refreshWeather.setOnRefreshListener {
            place7Weather()
        }

        viewModel.placeWeather.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showForecast(weather)
            } else {
                Toast.makeText(this, "无法获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.refreshWeather.isRefreshing = false
        }

        val placeManage = PlaceManage(viewModel.placeName, viewModel.locationLng, viewModel.locationLat,
            viewModel.skyInfo, viewModel.temperature)

        // 获取城市列表
        viewModel.loadPlace()
        viewModel.loadPlaceLiveData.observe(this) { result ->
            val placeList = result.getOrNull()
            if (placeList != null) {
                Log.d("AddPlaceActivity", "placeList is not null")
                viewModel.placeList.clear()
                viewModel.placeList.addAll(placeList)
                for (element in placeList) {
                    Log.d("AddPlaceActivity", "place:${element.id}, $element")
                }
            }

            // viewModel.placeList 初始化之后，查看当前城市是否在城市列表中
            if (viewModel.findPlace(placeManage)) {
                Log.d("AddPlaceActivity", "right enter")
                binding.controlPlaceBtn.setBackgroundResource(R.drawable.enter_weather_place)
                viewModel.isFound = true
            } else {
                Log.d("AddPlaceActivity", "plus")
                binding.controlPlaceBtn.setBackgroundResource(R.drawable.add_place_btn)
            }
        }

        viewModel.addPlaceViewModel.observe(this) { result ->
            val placeList = result.getOrNull()
            if (placeList != null) {
//                viewModel.rowId = rowId
//                SunnyWeatherApplication.rowId = rowId
                Log.d("AddPlaceActivity", "insert success")
            } else {
                Log.d("AddPlaceActivity", "insert failed")
            }
        }

        binding.controlPlaceBtn.setOnClickListener {
            if (!viewModel.isFound) {
                // 如果没有在城市列表中找到当前城市，点击按钮可将当前城市添加到城市列表中
                if (viewModel.placeList.size < 8) {
                    viewModel.addPlace()
                } else {
                    Toast.makeText(this, "城市数量已达上限，如要添加新的城市，请先删除已有城市", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // 点击按钮可进入当前城市的详细天气界面，但是 WeatherActivity 的启动模式是 SingleTask，无法通过intent传递信息
            val intent = Intent(this, WeatherActivity::class.java).apply {
                putExtra("place_name", viewModel.placeName)
            }
            Log.d("AddPlaceActivity", "placeName = ${viewModel.placeName}")
            startActivity(intent)
        }
    }

    private fun place7Weather() {
        viewModel.place7Weather(viewModel.locationLng, viewModel.locationLat)
        binding.refreshWeather.isRefreshing = true
    }

    private fun showForecast(weather: Weather) {
        val daily = weather.daily
        val realtime = weather.realtime
        // 填充 forecast.xml 布局中的数据
        binding.weatherForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            Log.d("WeatherActivity", "i = $i")
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(
                R.layout.forecast_item,
                binding.weatherForecast.forecastLayout, false)
            val dateInfo = view.findViewById<TextView>(R.id.dataInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDataFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
            Log.d("WeatherActivity", "skycon = $skycon")
            val dataInfoStr = when(i) {
                0 -> "今天 ${simpleDataFormat.format(skycon.date)}"
                1 -> "明天 ${simpleDataFormat.format(skycon.date)}"
                else -> "${getDayOfWeek(skycon.date)} ${simpleDataFormat.format(skycon.date)}"
            }
            dateInfo.text = dataInfoStr
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding.weatherForecast.forecastLayout.addView(view)

            viewModel.skyInfo = getSky(realtime.skycon).info
            viewModel.temperature = realtime.temperature
        }
    }

    private fun getDayOfWeek(date: Date): String {
        val sdf = SimpleDateFormat("E", Locale.getDefault())
        return sdf.format(date)
    }
}