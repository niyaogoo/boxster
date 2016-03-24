package com.ny.boxster.finder;

import java.util.List;

/**
 * 预设置的远程服务器发现者
 */
public class PresetRemoteServiceFinder implements RemoteServiceFinder {

    List<String> remoteServiceHosts;

    @Override
    public List<String> getRemoteServiceHostWithPortList() {
        return remoteServiceHosts;
    }

    public List<String> getRemoteServiceHosts() {
        return remoteServiceHosts;
    }

    public void setRemoteServiceHosts(List<String> remoteServiceHosts) {
        this.remoteServiceHosts = remoteServiceHosts;
    }
}
