
## 项目介绍
木兰湾图片分享是基于人脸识别、人脸比对的图片分享系统。用户上传照片系统自动识别出人脸，并通过人脸比对搜索相似人脸的照片。
系统还可以通过爬虫模块爬取网络照片，以检索相似人脸照片的网页信息。

该项目是自己的一个业余爱好项目，本人平时喜欢旅行和拍照，现在随着手机拍照越来越方便，人们拍的照片就越来越多，同时也相互为陌生人拍了很多
照片，但是这些照片怎么能分享给被拍摄者，一直没有好的平台。如果现场用手机分享给对方很多人感觉有点唐突。如果网上有一个平台能通过人脸识别
来搜索照片，那么拍摄者和被拍摄者都能精确地搜索到自己的照片，实现半封闭的分享。

项目说明
* 人脸识别、比对采用的是虹软的人脸SDK
* 人脸识别率和比对准确度都还是不错的
* 基于分页的比对搜索方案目前是一张张比对，速度比较慢
* 目前完成了主体功能的实现：人脸识别、比对、上传
* 爬虫模块目前可以爬取8264论坛的帖子及照片，其他论坛的原理其实类似

### 项目模块

该系统是前后端分离的项目，当前项目mulanbay-face-server为后端API项目，只提供系统的api接口，整个系统必须要同时运行前端才能完整访问。

木兰湾管理系统前端项目：

* 后台管理(PC端) [mulanbay-face-admin](https://github.com/mulanbay-face/mulanbay-face-admin)
* 图片分享(移动端) [mulanbay-face-mobile](https://github.com/mulanbay-face/mulanbay-face-mobile)

[木兰湾项目说明](https://github.com/mulanbay-face)

### 功能简介

* 图片上传，系统自动识别人脸，也可以手动剪切人脸
* 人脸相似的分页搜索，可以指定最低匹配度
* 提供照片人脸与照片人脸、照片人脸与用户人脸、用户人脸与照片人脸、用户人脸与用户人脸的四种搜索模式
* 通过爬虫爬取网络照片，自动识别人脸
* 基于RBAC的用户权限管理
* 支持分布式运行的调度功能

### 文档地址

木兰湾文档[https://www.yuque.com/mulanbay/rgvt6k/uy08n4](https://www.yuque.com/mulanbay/rgvt6k/uy08n4)

### 所用技术

* 前端：Vue、Vant、Element UI、Echarts
* 后端：Spring Boot、Hibernate、Quartz、Redis & Jwt、虹软人脸识别

| 核心依赖                | 版本          |
| ---------------------- | ------------- |
| Spring Boot            | 2.3.4.RELEASE |
| Hibernate              | 5.4.21.Final  |
| Quartz                 | 2.3.2         |
| 虹软人脸识别             | 3.0.0        |

### 项目结构
``` lua
mulanbay-server
├── mulanbay-business    -- 通用业务类
├── mulanbay-common      -- 公共模块
├── mulanbay-persistent  -- 持久层基于hibernate的封装
├── mulanbay-api         -- 木兰湾API接口层
├── mulanbay-schedule    -- 调度模块封装
├── mulanbay-web         -- 基于SpringMVC的一些封装
├── mulanbay-spider      -- 爬虫模块

```

### 项目运行与部署
``` 
# Step 1：初始化数据库

1. 下载源代码
2. 在mysql中创建数据库，比如:mulanbay_face
3. 初始化数据库,执行mulanbay-api工程docs目录下的sql文件：mulanbay_init.sql

# Step 2：修改配置文件

## API项目:mulanbay-api

1. 在mulanbay-api/src/main/resources/目录下复制application-local-template.properties文件并重命名为application-local.properties，设置本地配置。
   其中Mysql数据库配置、Redis配置为必须配置，如果需要使用微信公众号的消息发送功能，需要配置.
2. 人脸识别采用虹软的sdk，目前系统采用的是3.0.0版本，jar文件在mulanbay-api/src/lib目录下
  * 虹软视觉开发平台文档地址：https://ai.arcsoft.com.cn/manual/docs#/211
  * 你可能需要下载自己的sdk所对应的jar文件
  * 你需要开通自己的虹软应用（免费），获取appId、sdkKey及dll文件，并且配置到application-local.properties

## 爬虫项目:mulanbay-spider

1. 在mulanbay-spider/src/main/resources/目录下复制application-local-template.properties文件并重命名为application-local.properties，设置本地配置。
   其中Mysql数据库配置、Redis配置为必须配置.
2. 目前爬虫的任务是在管理员页面手动添加进去，系统启动后，在页面添加

# Step 3：打包&运行

1. 开发环境

## API项目:mulanbay-api
  运行mulanbay-api子工程下的cn.mulanbay.api.web.Application

## 爬虫项目:mulanbay-spider
  运行mulanbay-spider子工程下的cn.mulanbay.spider.web.Application

2. 正式环境

## API项目:mulanbay-api
  * 进入到mulanbay-server目录，运行mvn clean package
  * 运行mulanbay-api/target下的mulanbay-api-1.0.jar文件

## 爬虫项目:mulanbay-spider
  * 进入到mulanbay-server目录，运行mvn clean package
  * 运行mulanbay-spider/target下的mulanbay-spider-1.0.jar文件


```

### 软件要求
| 软件                    | 版本          |
| ---------------------- | ------------- |
| JDK                    | 1.8+          |
| Nginx                  | 1.17+         |
| Redis                  | 6.0+          |
| Mysql                  | 8.0+          |

### 硬件要求
 内存4G+
 
## 系统架构

![系统流程](https://images.gitee.com/uploads/images/2021/0501/084458_ed74ab1e_352331.png "流程图.PNG")

## 在线演示
暂未提供

## 技术交流
* QQ群：562502224

## 参考/集成项目

木兰湾管理系统参考、集成了一些项目，有些功能自己也只是一个搬运工，先感谢大家的开源。

* 前端(PC)：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue)，[vue-element-admin](https://github.com/PanJiaChen/vue-element-admin)
* 前端(移动)：[vant](https://vant-contrib.gitee.io/vant/#/zh-CN/home)
* 图片瀑布流：[waterfall2](https://github.com/AwesomeDevin/vue-waterfall2)
* Tag组件：[vue-tags-input](https://github.com/JohMun/vue-tags-input)

## 项目展望

## 使用&授权

## 项目截图

### 移动端：
<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082328_20c45aa3_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082758_151d4955_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082453_39805147_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082511_dc9201f7_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082540_6d270e84_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082615_b3d0eb0e_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082737_597dc287_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082403_ab5609db_352331.png"/></td>
    </tr>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082825_2a80e578_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/082839_8dba7632_352331.png"/></td>
    </tr>
</table>

### PC端:

<table>
    <tr>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/083208_81b75cf1_352331.png"/></td>
        <td><img src="https://images.gitee.com/uploads/images/2021/0501/083222_d112a48a_352331.png"/></td>
    </tr>

</table>
