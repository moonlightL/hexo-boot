package com.light.hexo.plugin.server.service;

import com.light.hexo.plugin.server.model.ServerInfo;

/**
 * @Author MoonlightL
 * @ClassName: ServerService
 * @ProjectName hexo-boot
 * @Description: 服务器
 * @DateTime 2022/4/20, 0020 11:11
 */
public interface ServerService {

    ServerInfo getServerInfo();
}
