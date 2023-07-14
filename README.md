# SunnyWeather
### 介绍  
书籍《第一行代码 Android 第三版》 天气预报APP SunnyWeather。项目主要功能：查询城市天气，显示城市7天内的天气详情，以及当下所查询城市的天气状况  
App 界面如下：

<img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/searchPlace.jpg" width=25%> <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/showSearchPlace.jpg" width=25%> <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/weather.jpg" width=25%> <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/searchPlaceWeather.jpg" width=25%> <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/weatherDetails.jpg" width=25%>

   #### 功能：
     1. 城市搜索功能：发起网络请求并解析收到的json数据；使用RecyclerView显示相关城市
     2. 获取实时天气：发起网络请求并解析收到的json数据
     3. 获取天气预报：发起网络请求并解析收到的json数据

### 项目架构示意图：

<img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/SunnyWeather 架构示意图.jpg" width=40%>

### 新增城市管理功能
   城市管理功能提供城市的添加与长按删除功能：搜索城市后进入城市天气预报界面，可选择是否添加到城市管理列表；城市管理列表中的城市可长按进行删除（若表中只剩一个城市，则不能删除）
   新增功能界面：

   <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/AddPlace-Add.png" width=25%> <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/AddPlace-Open.png" width=25%> <img src="https://github.com/wanghaha23333/SunnyWeather/blob/main/AppImage/PlaceManage.png" width=25%>
   #### 功能点：
     1. 使用SQLite数据库对城市天气数据进行存储：使用Room关系对象映射
     2. 对城市列表中的城市进行添加/删除
     3. 使用RecyclerView显示城市列表：对item设置长按监听
