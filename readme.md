# jasper

> 2023年3月31日 陕西西安

## intro
这个项目是个人的博客系统，后续将逐步迭代，把自己的博客做的越来越好。

## 设计思路

### 一阶段
* 将文章的md文件存储到Github上面
* 使用批量定时去Github拉取文章数据，将其持久化到数据库中
* 采用Mybatis来做ORM框架
* 采用自己编写的MVC框架实现博客前端页面和后端数据的请求
* 采用Log4j2实现日志记录
* 采用jackson来做json的处理
* 采用MySQL来做数据库


#### 尚未完成的工作
1. ~~后端的异常处理，包括500和404的处理~~
2. ~~评论的参数长度校验~~
3. ~~评论接口的防止频繁刷接口的问题~~
4. ~~前端部分接口的联调~~
5. ~~mybatis打印sql的日志配置和耗时~~
6. 批量任务入库的逻辑
7. 前后端分离的话，nginx的静态资源配置脚本
8. 服务器的购买
9. 前端404页面和500的页面配置