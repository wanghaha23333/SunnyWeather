package com.sunnyweather.android.logic.dao

import androidx.room.*
import com.sunnyweather.android.logic.model.PlaceManage

@Dao
interface PlaceManageDao {
    // 添加城市对象，并返回行数
    @Insert
    fun insertPlace(place: PlaceManage): Long

    // 删除城市对象
    @Delete
    fun deletePlace(place: PlaceManage)

    // 更新城市对象
    // Room 使用主键将传递的实体实例与数据库中的行进行匹配。如果没有具有相同主键的行，Room 不会进行任何更改
    @Update
    fun updatePlace(place: PlaceManage): Int

    // 查询所有城市
    @Query("select * from PlaceManage")
    fun loadAllPlaces(): List<PlaceManage>

    // 根据经纬度查询某个城市
    @Query("select * from PlaceManage where lng = :lng and lat = :lat")
    fun queryPlaceByLngLat(lng: String, lat: String): PlaceManage?

    // 根据id查找城市
    @Query("select * from PlaceManage where id = :id")
    fun queryPlaceById(id: Long): PlaceManage?

    // 根据经纬度删除某个城市
    @Query("delete from PlaceManage where lng = :lng and lat = :lat")
    fun deletePlaceByLngLat(lng: String, lat: String)
}