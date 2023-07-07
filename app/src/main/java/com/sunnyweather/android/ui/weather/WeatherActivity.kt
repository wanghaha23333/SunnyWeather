package com.sunnyweather.android.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.*
import com.sunnyweather.android.ui.place.PlaceSearchActivity
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    lateinit var binding: ActivityWeatherBinding
    lateinit var adapter: PlaceManageAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        Log.d("WeatherActivity", "onCreate")
        decidePlaceOpen()

        val layoutManage = LinearLayoutManager(this)
        binding.includePlaceManage.placeManageReV.layoutManager = layoutManage
        adapter = PlaceManageAdapter(this, viewModel.placeList)
        binding.includePlaceManage.placeManageReV.adapter = adapter

        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false
        }
        binding.swipeRefresh.setColorSchemeResources(com.google.android.material.R.color.design_default_color_primary)
        refreshWeather()
        binding.swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        binding.includeNow.navBtn.setOnClickListener {
            Log.d("WeatherActivity", "home btn pressed")
            adapter.notifyDataSetChanged()
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.includePlaceManage.placeManageReV.layoutManager = LinearLayoutManager(this)
        viewModel.loadPlace()

        viewModel.loadPlaceLiveData.observe(this) { result ->
            val placeList = result.getOrNull()
            if (placeList != null) {
                Log.d("WeatherActivity", "load places")
                viewModel.placeList.clear()
                viewModel.placeList.addAll(placeList)
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.deletePlaceLivaData.observe(this) { result ->
            val placeList = result.getOrNull()
            if (placeList != null) {
                Log.d("WeatherActivity", "delete places")
                viewModel.placeList.clear()
                viewModel.placeList.addAll(placeList)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "无法删除该城市", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }

        viewModel.updatePlaceViewModel.observe(this) { result ->
            val placeList = result.getOrNull()
            if (placeList != null) {
                Log.d("WeatherActivity", "update places")
                viewModel.placeList.clear()
                viewModel.placeList.addAll(placeList)
                adapter.notifyDataSetChanged()
            }
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {
                adapter.notifyDataSetChanged()
            }

            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })

        binding.includePlaceManage.searchPlaceEntrance.setOnClickListener {
            val intent = Intent(this, PlaceSearchActivity::class.java)
            intent.putExtra("searchPlace", "searchPlace")
            startActivity(intent)
            binding.drawerLayout.closeDrawers()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("WeatherActivity", "onResume")
        decidePlaceOpen() // 需要定义一个变量，如果在 onCreate() 方法中执行过这个方法，onResume() 中就无须再执行这个方法
//        refreshWeather()
    }

    fun decidePlaceOpen() {
        Log.d("WeatherActivity", "Location = ${SunnyWeatherApplication.locationDes}")
        viewModel.loadPlace()
        viewModel.findPlaceByLngLat(SunnyWeatherApplication.locationDes)
        viewModel.findPlaceLiveData.observe(this) { result ->
            val placeManage = result.getOrNull()
            if (placeManage != null) {
                Log.d("WeatherActivity", "place name = ${placeManage.place}")
                viewModel.locationLng = placeManage.lng
                viewModel.locationLat = placeManage.lat
                viewModel.placeName = placeManage.place
                refreshWeather()
            }
        }
    }

    private fun refreshWeather() {
        Log.d("WeatherActivity", "refreshWeather, place = ${viewModel.placeName}")
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binding.swipeRefresh.isRefreshing = true

        // 备份当前城市
        val place = Place(viewModel.placeName, Location(viewModel.locationLng, viewModel.locationLat), "")
        viewModel.savePlace(place)
        Log.d("WeatherActivity", "back up the place, place = $place")
    }

    private fun showWeatherInfo(weather: Weather) {
        binding.includeNow.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

        // 填充 now.xml 布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.includeNow.currentTemp.text = currentTempText
        binding.includeNow.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.includeNow.currentAQI.text = currentPM25Text
        binding.includeNow.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        // 填充 forecast.xml 布局中的数据
        binding.includeForecast.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                binding.includeForecast.forecastLayout, false)
            val dateInfo = view.findViewById<TextView>(R.id.dataInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDataFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
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
            binding.includeForecast.forecastLayout.addView(view)
        }

        // 填充 life_index.xml 布局中的数据
        val lifeIndex = daily.lifeIndex
        binding.includeLifeIndex.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.includeLifeIndex.dressingText.text = lifeIndex.dressing[0].desc
        binding.includeLifeIndex.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.includeLifeIndex.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = View.VISIBLE

        // 填充当前 place 对应的 placeManage 对象
        viewModel.skyInfo = getSky(realtime.skycon).info
        viewModel.temperature = realtime.temperature
        viewModel.updatePlace()
    }

    private fun getDayOfWeek(date: Date): String {
        val sdf = SimpleDateFormat("E", Locale.getDefault())
        return sdf.format(date)
    }
}