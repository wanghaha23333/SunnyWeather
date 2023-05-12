package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.databinding.FragmentPlaceBinding
import com.sunnyweather.android.ui.weather.WeatherActivity
class PlaceFragment : Fragment() {

    // 使用懒加载技术来获取 PlaceViewModel 的实例
    // 这种写法允许在整个类中随时使用 viewModel 这个变量，而不用关心它何时初始化、是否为空等前提条件
    val viewModel by lazy {
        ViewModelProvider(this).get(PlaceViewModel::class.java)
    }

    private lateinit var binding: FragmentPlaceBinding

    private lateinit var adapter: PlaceAdapter

    // 加载 fragment_place 布局
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("PlaceFragment", "onCreateView load fragment_place")
        binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "FragmentLiveDataObserve")
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.d("PlaceFragment", "onViewStateRestored")

        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }


        // 给 RecyclerView 设置了 LayoutManager 和适配器
        // 并使用 PlaceViewModel 中的 placeList 集合作为数据源
        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.recyclerView.adapter = adapter

        // 监听搜索框内容的变化情况
        // 每当搜索框中的内容发生了变化，就获取新的内容
        // 然后传递给 PlaceViewModel 的 searchPlaces() 方法，这样就可以发起搜索城市数据的网络请求了
        Log.d("PlaceFragment", "add text changed Listener")
        binding.searchPlaceEdit.addTextChangedListener { editable ->
            Log.d("PlaceFragment", "edit listener")
            val content = editable.toString()
            if (content.isNotEmpty()) {
                Log.d("PlaceFragment", "content is not empty")
                viewModel.searchPlaces(content)
            } else {

                // 当输入搜索框中的内容为空时，将 RecyclerView 隐藏起来
                // 同时将背景图显示出来
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        // 获取服务器响应的数据，这个需要借助 LiveData 来完成
        // 这里对 PlaceViewModel 中的 placeLiveData 对象进行观察
        // 当有任何数据变化时，就会回调到传入的 Observer 接口实现中
        // 然后对回调的数据进行判断
        viewModel.placeLiveData.observe(this, Observer { result ->
            val places = result.getOrNull()

            // 如果数据不为空，将数据添加到 PlaceViewModel 的 placeList 集合中 并通知 PlaceAdapter 刷新界面
            if (places != null) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {

                // 如果数据为空 则说明发生了异常，此时弹出一个 Toast 提示，并打印具体的异常原因
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}