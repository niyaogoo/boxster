package com.ny.boxster;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 自动发布到ZooKeeper
 */
public class ZooKeeperMultiRmiServiceExport extends MultiRmiServiceExporter implements InitializingBean {

    private ZooKeeper zooKeeper;

    private String currentNode;

    private String parentNode;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(zooKeeper, "zooKeeper is null");

        //ensure parent exists
        Stat stat = zooKeeper.exists(parentNode, false);
        if (stat == null) {
            logger.info("ZooKeeper rmi export parent node doesn't exists, create one, path:{}", parentNode);
            zooKeeper.create(parentNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        this.currentNode = zooKeeper.create(parentNode + "/" + getServiceUrl(getRegistryHost(), getRegistryPort()),
                null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);

        logger.info("ZooKeeper rmi export child node create success, path:{}", currentNode);
        super.afterPropertiesSet();
    }

    public String getParentNode() {
        return parentNode;
    }

    public void setParentNode(String parentNode) {
        this.parentNode = parentNode;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    private String getServiceUrl(String ip, int port) {
        if (ip == null) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new IllegalStateException("Could not get local host address");
            }
        }
        return ip + ":" + port;
    }
}
