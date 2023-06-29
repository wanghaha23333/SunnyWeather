package com.sunnyweather.android.logic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sunnyweather.android.logic.dao.PlaceManageDao
import com.sunnyweather.android.logic.model.PlaceManage

@Database(version = 1, entities = [PlaceManage::class])
abstract class PlaceDatabase : RoomDatabase() {

    // 获取 PlaceManageDao 的实例
    abstract fun PlaceManageDao(): PlaceManageDao

    // 原则上全局应该只存在一个 database 实例，因此这里编写一个单例模式
    companion object {
        private var instance: PlaceDatabase ?= null

        @Synchronized
        fun getDatabase(context: Context) : PlaceDatabase {

            // 如果 instance 不为空，则直接返回
            instance?.let {
                return it
            }

            // instance 为空，则调用 Room.databaseBuilder() 来构建一个 PlaceDatabase 实例
            return Room.databaseBuilder(context.applicationContext,
                PlaceDatabase::class.java, "place_database")
                .build().apply {
                    instance = this
                }
        }
    }
}