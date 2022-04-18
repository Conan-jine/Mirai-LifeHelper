# LifeHelper

> 本插件基于[mirai](https://github.com/mamoe/mirai)
>
> [![Release](https://img.shields.io/badge/release-1.0-pre--green)](https://img.shields.io/badge/release-1.0-pre--green)

## 概览

**这是一个致力于增强QQ使用体验的插件（总得有点高大上的追求不是）**

## 功能

- 群实时反馈上传文件名（不需要下载文件就能看全文件名）
- 获取Epic Game周免信息
- 转发所有发给第一个机器人的消息给admin用户（见安装第7步）
- 第一个机器人自动同意好友请求和被邀入群请求


## 指令

| 指令                | 效果             |
| ------------------- | ---------------- |
| 白嫖 | 列出当前Epic周免游戏信息 |
| 开启白嫖推送 | 为群或用户每周五晚七点推送Epic周免游戏信息 |
| 关闭白嫖推送 | 取消群或用户的每周五晚七点推送 |

## 安装

1. 按照[mirai](https://github.com/mamoe/mirai-console)的步骤安装mirai机器人
2. 从[发布页](https://github.com/Pmx990/Mirai-Steam-Plugin/releases/tag/v0.1)下载jar文件
3. 将jar文件置入mirai安装文件夹下的plugins文件夹
4. 启动一次mirai机器人，mirai会自动生成插件数据目录(data/org.lifehelper.LifeHelper)(~~不行就两次~~)
5. 在**data/org.lifehelper.LifeHelper**目录下创建**admin.txt**,**userData.txt**和**groupData.txt**三个文件
6. 再次启动mirai机器人即安装成功
7. **admin.txt**内第一行需输入admin用户QQ号

## 补充说明

编写插件时只考虑了只有一个机器QQ的情况，功能只能被已登录的QQ机器人位列第一的机器人触发。当然如果你只登录了一个QQ机器人那没事了。
