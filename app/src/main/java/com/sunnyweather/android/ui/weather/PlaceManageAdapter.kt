package com.sunnyweather.android.ui.weather

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.databinding.PlaceManageItemBinding
import com.sunnyweather.android.logic.model.PlaceManage

class PlaceManageAdapter(val weatherActivity: WeatherActivity, val placeManageList: List<PlaceManage>) :
    RecyclerView.Adapter<PlaceManageAdapter.ViewHolder>() {

    companion object {
        const val TAG = "PlaceManageAdapter"
    }

    inner class ViewHolder(binding: PlaceManageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val placeName = binding.placeNamePm
        val skyInfo = binding.skyInfoPm
        val temperature = binding.temperaturePm
    }

    // 加载item
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceManageAdapter.ViewHolder {
        val binding = PlaceManageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding)

        // 子项的点击事件
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val placeManage = placeManageList[position]
            Log.d(TAG, "position = $position, placeName = ${placeManage.place}")
            Toast.makeText(parent.context, "position = $position, placeName = ${placeManage.place}", Toast.LENGTH_SHORT).show()
            // 暂不处理
            SunnyWeatherApplication.rowId = (position + 1).toLong()
            val intent = Intent(parent.context, WeatherActivity::class.java)
            weatherActivity.startActivity(intent)
            weatherActivity.binding.drawerLayout.closeDrawers()
        }
        return holder
    }

    // 给 item 进行赋值
    override fun onBindViewHolder(holder: PlaceManageAdapter.ViewHolder, position: Int) {
        val placeManage = placeManageList[position]
        holder.placeName.text = placeManage.place
        holder.skyInfo.text = placeManage.skyInfo
        holder.temperature.text = placeManage.temperature.toString()
    }

    override fun getItemCount() = placeManageList.size
}