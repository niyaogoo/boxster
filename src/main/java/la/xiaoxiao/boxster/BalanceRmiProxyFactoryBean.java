package la.xiaoxiao.boxster;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.rmi.RmiClientInterceptor;
import org.springframework.util.ClassUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 基于Spring的RMI调用
 * 可请求多个服务器
 */
public class BalanceRmiProxyFactoryBean implements
        FactoryBean<Object>, BeanClassLoaderAware, MethodInterceptor, InitializingBean {

    Logger logger = LoggerFactory.getLogger(BalanceRmiProxyFactoryBean.class);

    private List<String> serviceUrls;

    private Class<?> serviceInterface;

    private boolean refreshStubOnConnectFailure = true;

    private boolean lookupStubOnStartup = false;

    private List<RmiClientInterceptor> rmiClientInterceptors = new ArrayList<>();

    private Random random = new Random();

    private Object serviceProxy;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        int idx = random.nextInt(rmiClientInterceptors.size());
        RmiClientInterceptor stub = rmiClientInterceptors.get(idx);
        try {
            return doInvoke(stub, invocation);
        } catch (RemoteAccessException e) {
            // try to find an other one remote service, if it has more than one nodes
            logger.error("Could not access remote service, serviceUrl [{}]", stub.getServiceUrl());
            if (rmiClientInterceptors.size() > 1) {
                for (int i = 0; i < rmiClientInterceptors.size(); i++) {
                    if (i == idx) {
                        continue;
                    }
                    RmiClientInterceptor retryStub = rmiClientInterceptors.get(i);
                    try {
                        return doInvoke(retryStub, invocation);
                    } catch (RemoteAccessException ex) {
                        logger.error("Could not access remote service, serviceUrl [{}]", retryStub.getServiceUrl());
                    }
                }
            }
            throw e;
        }
    }

    protected Object doInvoke(RmiClientInterceptor stub, MethodInvocation invocation) throws Throwable {
        if (logger.isTraceEnabled()) {
            logger.trace("Try to invoke rmi method, serviceUrl [{}]", stub.getServiceUrl());
        }
        try {
            return stub.invoke(invocation);
        } catch (RemoteException ex) {
            if (logger.isErrorEnabled()) {
                logger.error("Invoke rmi method failure, method:{}, serviceInterface:{}, serviceUrl",
                        invocation.getMethod(), stub.getServiceInterface(), stub.getServiceUrl(), ex);
            }
            throw ex;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prepare();
        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
    }

    public void prepare() {
        for (String serviceUrl : serviceUrls) {
            if (checkExist(serviceUrl)) {
                logger.debug("Remote serviceUrl:{} exists already, continue", serviceUrl);
                continue;
            }
            RmiClientInterceptor rmiClientInterceptor = new RmiClientInterceptor();
            rmiClientInterceptor.setServiceInterface(this.serviceInterface);
            rmiClientInterceptor.setServiceUrl(serviceUrl);
            rmiClientInterceptor.setRefreshStubOnConnectFailure(refreshStubOnConnectFailure);
            rmiClientInterceptor.setLookupStubOnStartup(lookupStubOnStartup);
            rmiClientInterceptor.prepare();
            this.rmiClientInterceptors.add(rmiClientInterceptor);
        }
        cleanExists();
    }

    // clean RmiClientInterceptor if it not exists in serviceUrls
    private void cleanExists() {
        List<RmiClientInterceptor> toBeCleanList = new ArrayList<>();
        for (RmiClientInterceptor i : rmiClientInterceptors) {
            boolean exist = false;
            for (String serviceUrl : serviceUrls) {
                if (serviceUrl.equals(i.getServiceUrl())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                toBeCleanList.add(i);
            }
        }
        synchronized (toBeCleanList) {
            toBeCleanList.forEach(rmiClientInterceptors::remove);
        }
    }

    // check serviceUrl if exists already
    private boolean checkExist(String serviceUrl) {
        for (RmiClientInterceptor i : rmiClientInterceptors) {
            if (serviceUrl.equals(i.getServiceUrl())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Object getObject() {
        return this.serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public List<String> getServiceUrls() {
        return serviceUrls;
    }

    public void setServiceUrls(List<String> serviceUrls) {
        this.serviceUrls = serviceUrls;
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public boolean isRefreshStubOnConnectFailure() {
        return refreshStubOnConnectFailure;
    }

    public void setRefreshStubOnConnectFailure(boolean refreshStubOnConnectFailure) {
        this.refreshStubOnConnectFailure = refreshStubOnConnectFailure;
    }

    public boolean isLookupStubOnStartup() {
        return lookupStubOnStartup;
    }

    public void setLookupStubOnStartup(boolean lookupStubOnStartup) {
        this.lookupStubOnStartup = lookupStubOnStartup;
    }
}
