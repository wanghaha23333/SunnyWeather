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
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.databinding.ActivityWeatherBinding
import com.sunnyweather.android.logic.model.*
import com.sunnyweather.android.ui.place.PlaceSearchActivity
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    companion object {
        const val TAG = "WeatherActivity"
        const val PLACE_NOT_FOUND = -1
    }

    lateinit var binding: ActivityWeatherBinding

    var position = PLACE_NOT_FOUND

    val viewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 刷新ViewPager
        refreshViewPager(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {

            // 刷新ViewPager
            refreshViewPager(intent)
        }
    }

    fun refreshViewPager(intent: Intent) {
        viewModel.loadPlace()
        viewModel.loadPlaceLiveData.observe(this) { result ->
            val placeList = result.getOrNull()
            if (placeList != null) {
                Log.d("WeatherActivity", "load places")
                viewModel.placeList.clear()
                viewModel.placeList.addAll(placeList)

                val placeName = intent.getStringExtra("place_name")
                position = if (placeName != null && viewModel.placeList.size > 1) {
                    Log.d(TAG, "refreshViewPager: placeName = $placeName, placeList.size = ${viewModel.placeList.size}")
                    viewModel.getPositionInPlaceList(placeName)
                } else {
                    Log.d(TAG, "refreshViewPager: placeName is null, placeList.size = ${viewModel.placeList.size}")
                    PLACE_NOT_FOUND
                }

                Log.d(TAG, "refreshViewPager: position = $position")
                binding.weatherPager.adapter = ViewPageAdapter(this)
                if (position != -1) {
                    binding.weatherPager.setCurrentItem(position, false)
                }

                // 将 TabLayout 与 ViewPager2 进行绑定
                Log.d(TAG, "onCreate， 将 TabLayout 与 ViewPager2 进行绑定")
                TabLayoutMediator(binding.tagLayout, binding.weatherPager) { tab, position ->
                    tab.text = "Page $position"
                }.attach()
            }
        }
    }
}