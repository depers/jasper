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