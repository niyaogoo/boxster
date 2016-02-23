package com.ny.boxster.finder;

import java.util.List;

/**
 * 远程服务器发现者
 */
public interface RemoteServiceFinder {

    /**
     * 获取可用的远程服务器主机地址列表
     * @return
     */
    List<String> getRemoteServiceHostWithPortList();

}
