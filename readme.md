# Jasper

> 2023年3月31日 陕西西安

## intro
这个项目是个人的博客系统，后续将逐步迭代，把自己的博客做的越来越好。

## 设计思路

### 一阶段的设计（2023年4月18日）
* 在本地编辑博客内容，将文章的md文件存储到Github上面。
* 使用批量定时去Github拉取文章数据，将其持久化到数据库中。
* 使用前后端分离的设计。
* 图片的存储目前直接使用nginx代理了静态文件。

* Jasper的前端项目源代码在[depers/jasper-front](https://github.com/depers/jasper-front)。
