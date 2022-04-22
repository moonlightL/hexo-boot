package com.light.hexo.plugin.server.service;

import com.light.hexo.plugin.server.model.ServerInfo;
import org.springframework.stereotype.Service;

/**
 * @Author MoonlightL
 * @ClassName: ServerServiceImpl
 * @ProjectName hexo-boot
 * @Description:
 * @DateTime 2022/4/20, 0020 11:11
 */
@Service
public class ServerServiceImpl implements ServerService {

    @Override
    public ServerInfo getServerInfo() {
        return new ServerInfo();
    }
}
