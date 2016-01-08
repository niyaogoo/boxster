package la.xiaoxiao.boxster;

import la.xiaoxiao.boxster.finder.RefreshableRemoteServiceFinder;
import la.xiaoxiao.boxster.finder.RemoteServiceFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动组装RMI服务器地址
 */
public class AutoBalanceRmiProxyFactoryBean extends BalanceRmiProxyFactoryBean {

    private RemoteServiceFinder remoteServiceFinder;

    private String remoteServiceName;

    private RefreshableRemoteServiceFinder.RefreshHandler refreshHandler;

    Logger logger = LoggerFactory.getLogger(AutoBalanceRmiProxyFactoryBean.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(remoteServiceFinder, "The remoteServiceFinder is null, " +
                "Should inject a remoteServiceFinder implementation, ex. PresetRemoteServiceFinder.");
        Assert.notNull(remoteServiceName, "The remoteServiceName is null");

        List<String> remoteServiceHosts = remoteServiceFinder.getRemoteServiceHostWithPortList();
        // zookeeper may be find later
//        Assert.notEmpty(remoteServiceHosts, "Get remoteServiceHosts return empty list.");

        List<String> serviceUrls = new ArrayList<>();
        if (remoteServiceHosts != null) {
            for (String host : remoteServiceHosts) {
                String serviceUrl = "rmi://" + host + "/" + remoteServiceName;
                serviceUrls.add(serviceUrl);
            }
        }
        setServiceUrls(serviceUrls);

        // refresh supports
        if (remoteServiceFinder instanceof RefreshableRemoteServiceFinder) {
            logger.info("RemoteServiceFinder is a RefreshableRemoteServiceFinder, register the RefreshHandler");
            refreshHandler = () -> {
                List<String> newRemoteServiceUrls = remoteServiceFinder.getRemoteServiceHostWithPortList();
                logger.info("Remote Service Urls updated, refresh clients");
                List<String> newServiceUrls = new ArrayList<>();
                for (String host : newRemoteServiceUrls) {
                    String serviceUrl = "rmi://" + host + "/" + remoteServiceName;
                    newServiceUrls.add(serviceUrl);
                }
                setServiceUrls(newServiceUrls);
                prepare();
            };
            ((RefreshableRemoteServiceFinder) remoteServiceFinder).addRefreshHandler(refreshHandler);
        }
        super.afterPropertiesSet();
    }

    public RemoteServiceFinder getRemoteServiceFinder() {
        return remoteServiceFinder;
    }

    public void setRemoteServiceFinder(RemoteServiceFinder remoteServiceFinder) {
        this.remoteServiceFinder = remoteServiceFinder;
    }

    public String getRemoteServiceName() {
        return remoteServiceName;
    }

    public void setRemoteServiceName(String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

}
