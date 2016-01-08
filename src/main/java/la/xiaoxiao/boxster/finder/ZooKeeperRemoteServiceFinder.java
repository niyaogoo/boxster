package la.xiaoxiao.boxster.finder;

import la.xiaoxiao.common.zookeeper.ChildrenMonitor;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 通过ZooKeeper获取远程服务器信息
 */
public class ZooKeeperRemoteServiceFinder
        implements RemoteServiceFinder, InitializingBean, RefreshableRemoteServiceFinder {

    Logger logger = LoggerFactory.getLogger(ZooKeeperRemoteServiceFinder.class);

    private String watchNode;

    private ZooKeeper zooKeeper;

    private ChildrenMonitor childrenMonitor;

    private List<String> serviceUrls = new ArrayList<>();

    private List<RefreshHandler> refreshHandlers = new ArrayList<>();

    @Override

    public List<String> getRemoteServiceHostWithPortList() {
        return serviceUrls;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prepare();
    }

    public void prepare() {
        Assert.notNull(zooKeeper, "zooKeeper is null");
        Assert.notNull(watchNode, "watchNode is null");
        childrenMonitor = new ChildrenMonitor(zooKeeper, watchNode, null, list -> {
            logger.debug("Remote Services updated, new Services:{}", list);
            if (list != null && list.size() > 0 && !list.equals(serviceUrls)) {
                synchronized (serviceUrls) {
                    serviceUrls = list;
                }
                if (refreshHandlers != null) {
                    onRefresh(refreshHandlers);
                }
            }
        });
    }

    @Override
    public void addRefreshHandler(RefreshHandler handler) {
        this.refreshHandlers.add(handler);
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public String getWatchNode() {
        return watchNode;
    }

    public void setWatchNode(String watchNode) {
        this.watchNode = watchNode;
    }
}
