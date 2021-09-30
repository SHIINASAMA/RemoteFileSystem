# RemoteFileSystem

## 简介

这是一个远程文件系统，能用，只能用一点点。包含简单的用户权限管理，我想对于个人用户而言已经足够了。本项目同时包含服务端与一个简单的客户端。

## 环境

软件采用 **JDK-17** 编译与运行。

中文乱码请手动设置<kbd>**JAVA_TOOL_OPTIONS = -Dfile.encoding=UTF-8**</kbd>环境变量

## 启动参数

- --launch-mode - 启动模式
  - client - 启动客户端（默认）
  - server - 启动服务端

- --config-path - 配置文件路径，该参数只对“server”模式有效
  - 配置文件路径，默认为“./server.json”

- --version - 查看版本

## 关于客户端

客户端配置文件对应<kbd>**client.json**</kbd>，通常长这样：

```json
{
    "lastName":"root",
    "download":"./Downloads",
    "lastHost":"localhost",
    "lastPort":8080
}
```

一般更改download就好，这是你的下载目录，另外几个字段由程序进行管理。这个文件不存在时程序会自动创建，默认下载目录就是“./Downloads”。注意：要下载目录生效需要重启程序。

## 关于服务端

服务端配置文件对应<kbd>**server.json**</kbd>，通常长这样：

```json
{
  "host": "localhost",
  "port": 8080,
  "backlog": 10,
  "workdirectory": "E:/",
  "threads": 8,
  "users": [
    {
      "username": "root",
      "password": "123",
      "permission": "rw"
    },
    {
      "username": "guest",
      "password": "123",
      "permission": "r"
    }
  ]
}
```



几个关键的字段的解释如下

| 字段          | 注释                                                       |
| ------------- | ---------------------------------------------------------- |
| host          | 本地访问填localhost就好，外部访问（比如局域网）使用0.0.0.0 |
| port          | 服务端口                                                   |
| workdirectory | 工作目录，也就是服务器提供客户端访问的根目录               |

***注意，墙裂建议不要让工作目录包含本程序。***

其中，**用户设置**的三个关键字段解释如下

| 字段       | 注释     |
| ---------- | -------- |
| username   | 用户名称 |
| password   | 用户密码 |
| permission | 用户权限 |

在实际传输过程中，用户密码使用MD5加密后进行传输，不用担心密码明文泄漏。

用户权限包含两种，读（r）和读写（rw）。其中读拥有查看、下载的权限，而读写则拥有完整权限。

## 文件名称限制

由于系统权限原因，现停止对特殊目录的访问。

对包含以下字段的所有请求，采取设置错误信息的操作（ILLEGAL_PATH）。

```txt
CON
COM1
PRN
NUL
LPT1
AUX
```

## 免责声明

**本项目**主要是**本人**在**局域网**下使用，做多平台文件传输。我**无法**保证数据的绝对安全，使用本软件带来的安全问题需要用户自行承担。若确实存在问题，也可以与我进行沟通和交流或者提issue（反正估计也就我自己当玩具玩玩了）。