package com.sunnyweather.android.ui.weather

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.AlertDialog
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

    val scaleXDown = PropertyValuesHolder.ofFloat("scaleX", 0.9f)
    val scaleYDown = PropertyValuesHolder.ofFloat("scaleY", 0.9f)
    val scaleXUp = PropertyValuesHolder.ofFloat("scaleX", 1.0f)
    val scaleYUp = PropertyValuesHolder.ofFloat("scaleY", 1.0f)

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

            SunnyWeatherApplication.locationDes.apply {
                lng = placeManage.lng
                lat = placeManage.lat
            }

            val intent = Intent(parent.context, WeatherActivity::class.java)
            weatherActivity.startActivity(intent)
            weatherActivity.binding.drawerLayout.closeDrawers()

        }

        // 长按删除
        holder.itemView.setOnLongClickListener {
            val position = holder.adapterPosition
            val placeManage = placeManageList[position]

            ObjectAnimator.ofPropertyValuesHolder(it, scaleXDown, scaleYDown).apply {
                duration = 200
                start()
            }

            AlertDialog.Builder(parent.context).apply {
                setTitle("删除地点")
                setMessage("是否删除地点：${placeManage.place}？")
                setCancelable(false)
                setPositiveButton("是") { dialog, which ->
                    weatherActivity.viewModel.deletePlace(placeManage)
                    dialog.dismiss()
                    it.scaleX = 1.0f
                    it.scaleY = 1.0f
                }
                setNegativeButton("否") { dialog, which ->
                    dialog.dismiss()
                    ObjectAnimator.ofPropertyValuesHolder(it, scaleXUp, scaleYUp).apply {
                        duration = 200
                        start()
                    }
                }
                show()
            }

            true
        }
        return holder
    }

    // 给 item 进行赋值
    override fun onBindViewHolder(holder: PlaceManageAdapter.ViewHolder, position: Int) {
        val placeManage = placeManageList[position]
        val currentTemp = "${placeManage.temperature.toInt()}°C"
        holder.placeName.text = placeManage.place
        holder.skyInfo.text = placeManage.skyInfo
        holder.temperature.text = currentTemp
    }

    override fun getItemCount() = placeManageList.size
}