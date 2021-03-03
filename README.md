## 一、Hexo Boot

Hexo Boot 是基于 Spring Boot + MySQL 开发的一套开源的博客系统。前身是 ml-blog 博客系统，在此基础上演变和扩展而来。

[![](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/moonlightL/ml-blog/blob/master/LICENSE)
![](https://img.shields.io/badge/language-Java-blue.svg)

## 二、扩展功能

除了继承 ml-blog 中的功能（文章、分类、标签、全局参数）外，Hexo Boot 还扩展了一下功能

### 2.1 评论、留言功能

```
轻松查看网友的评论与留言，及时互动，同时还附带表情功能，丰富回复内容
```

### 2.2 友链功能

```
与网友互换主页，友好分享
```

### 2.3 主题功能

```
支持前端页面主题动态变换以及在线编辑源码，让页面色彩丰富起来，同时支持自定义主题
```

### 2.4 黑名单功能

```
设置 ip 黑名单，防御网络小人恶意攻击系统
```

### 2.5 附件功能

```
支持本地、七牛云、OSS 3种附件管理
```

### 2.6 备份功能

```
支持自动和手动备份SQL数据，防患数据丢失
```

## 三、预览效果

[网站演示1](https://www.extlight.com/) 

[网站演示2](https://www.liuyj.top/) 

### 3.1 后台管理预览图

![](https://images.extlight.com/hexo-boot-00.jpg)

![](https://images.extlight.com/hexo-boot-01.jpg)

![](https://images.extlight.com/hexo-boot-02.jpg)

![](https://images.extlight.com/hexo-boot-03.jpg)

![](https://images.extlight.com/hexo-boot-04.jpg)

![](https://images.extlight.com/hexo-boot-05.jpg)

![](https://images.extlight.com/hexo-boot-06.jpg)

![](https://images.extlight.com/hexo-boot-07.jpg)

![](https://images.extlight.com/hexo-boot-08.jpg)

![](https://images.extlight.com/hexo-boot-09.jpg)


### 3.2 前端预览图(默认主题)

![](https://images.extlight.com/hexo-boot-theme-default.jpg)

## 四、启动与部署

### 4.1 启动

下载源码，通过 Idea 工具打开项目，修改 resources 目录下的 application.yml 中的数据库配置（用户名和密码），运行项目即可。

前端主页访问地址： 
```
http://127.0.0.1:8080
```

后端管理访问地址
```
http://127.0.0.1:8080/admin/login.html
```

### 4.2 部署

该项目支持 war 包和 jar 包两种方式运行

#### 4.2.1 war 包形式

1.修改 pom.xml 文件的 2 处地方：

```
将 <packaging>war</packaging> 注释放开

排除 spring-boot-starter-web 的内置 tomcat
```

2.``mvn clean package``，打出名为 ROOT.war 文件，将其上传至 tomcat 的 webapps 目录下（如已有 ROOT 文件，将其删掉），启动 tomcat 即可

#### 4.2.2 jar 包形式

1.创建博客配置文件夹 ``mkdir ~/.hexo-boot``

2.mvn clean package，打出 jar 包后上传至 **~/.hexo-boot**

3.将 application.yml 文件上传至 **~/.hexo-boot** 目录中，根据自己的情况修改**application.yml**的数据库信息

4.创建 Service 服务

```
vim /etc/systemd/system/hexo-boot.service

# 编辑内容如下：

[Unit]
Description=hexo-boot
After=syslog.target

[Service]
User=root
ExecStart=/usr/java/jdk8/bin/java -server -Xms512m -Xmx1024m -jar /root/.hexo-boot/hexo-boot.jar --spring.config.additional-location=/root/.hexo-boot/
Restart=always

[Install]
WantedBy=multi-user.target
```

**注意：内存分配和路径根据自己的情况进行修改，且路径必须是绝对路径！**

**注意：内存分配和路径根据自己的情况进行修改，且路径必须是绝对路径！**

**注意：内存分配和路径根据自己的情况进行修改，且路径必须是绝对路径！**

5. 服务命令

```
# 启动
systemctl start hexo-boot
# 重启
systemctl restart hexo-boot
# 关闭
systemctl stop hexo-boot
```

6. 补充

步骤1 和 步骤2 可以使用如下命令代替

【GitHub】资源

```
# 下载安装包
curl -L https://github.com/moonlightL/hexo-boot/releases/download/1.4.0/hexo-boot-1.4.0.jar --output ~/.hexo-boot/hexo-boot.jar

# 下载 spring boot 配置文件，记得要修改数据库配置
curl -L -o ~/.hexo-boot/application.yml --create-dirs https://github.com/moonlightL/hexo-boot/releases/download/1.4.0/application.yml
```

【码云】资源

```
# 下载安装包
curl -L https://gitee.com/moonlightL/hexo-boot/attach_files/537531/download/hexo-boot-1.4.0.jar --output ~/.hexo-boot/hexo-boot.jar

# 下载 spring boot 配置文件，记得要修改数据库配置
curl -L -o ~/.hexo-boot/application.yml --create-dirs https://gitee.com/moonlightL/hexo-boot/attach_files/537532/download/application.yml
```

## 五、添加主题

### 5.1 方式一

下载主题源码，修改名称（比如 hexo-boot-theme-abc 改成 abc），然后将整个文件夹复制到项目的 resources/templates/theme 下（与 default 目录同级），启动项目即可。

如若项目已经启动运行，也可复制到 classes/templates/theme 下即可

### 5.2 方式二

进入博客后端管理界面 -> 更换主题 -> 拉取

输入主题的 git 地址，点击“拉取”按钮，即可等待下载安装

**目前已开源的主题:**

[hexo-boot-theme-vCard](https://github.com/moonlightL/hexo-boot-theme-vCard)

[hexo-boot-theme-breezyCV](https://github.com/moonlightL/hexo-boot-theme-breezyCV)

[hexo-boot-theme-next](https://github.com/moonlightL/hexo-boot-theme-next)

如果网络不佳，无法访问 GitHub，也可以访问 [码云](https://gitee.com/moonlightL) ，找到主题进行下载。

### 5.3 自定义主题

篇幅较大，请查看 Wiki

[GitHub Wiki](https://github.com/moonlightL/hexo-boot/wiki/%E8%87%AA%E5%AE%9A%E4%B9%89%E4%B8%BB%E9%A2%98)

[码云 Wiki](https://gitee.com/moonlightL/hexo-boot/wikis/%E8%87%AA%E5%AE%9A%E4%B9%89%E4%B8%BB%E9%A2%98?sort_id=3151185)

## 六、更新日志

2021-02-03 默认主题支持 pjax 请求，同时新增音乐播放功能

2020-12-16 调整默认主题，新增自定义页面功能

2020-12-02 支持 jar 方式部署运行

2020-11-12 新增在线下载拉取主题功能

2020-10-30 扩展主题配置，在线编辑主题文件，调整评论区插件的展示列表

2020-10-22 上传开源
