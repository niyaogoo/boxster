package la.xiaoxiao.boxster;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
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

        this.currentNode = zooKeeper.create(parentNode + "/" + getServiceUrl(getRegistryHost(), getRegistryPort()),
                null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
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
        return "rmi://" + ip + ":" + port;
    }
}
