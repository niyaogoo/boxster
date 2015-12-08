package la.xiaoxiao.boxster;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.rmi.RmiClientInterceptor;
import org.springframework.util.ClassUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于Spring的RMI调用
 * 可请求多个服务器
 */
public class BalanceRmiProxyFactoryBean implements
        FactoryBean<Object>, BeanClassLoaderAware, MethodInterceptor, InitializingBean {

    Logger logger = LoggerFactory.getLogger(BalanceRmiProxyFactoryBean.class);

    private List<String> serviceUrls;

    private Class<?> serviceInterface;

    private boolean refreshStubOnConnectFailure = false;

    // the number of retry times while catch RemoteException
    private int retryTimesOnRemoteException = 0;

    private List<RmiClientInterceptor> rmiClientInterceptors = new ArrayList<>();

    private int next = 0;

    private Object serviceProxy;

    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return doInvoke(invocation);
        } catch (RemoteException e) {
            if (retryTimesOnRemoteException > 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Retry to invoke method, the number of retry times:{}", retryTimesOnRemoteException);
                }
                return retryToInvoke(invocation, e);
            } else {
                throw e;
            }
        }
    }

    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        if (next++ > rmiClientInterceptors.size() - 1) {
            next = 0;
        }
        RmiClientInterceptor stub = rmiClientInterceptors.get(next);
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

    protected Object retryToInvoke(MethodInvocation invocation, RemoteException e) throws Throwable {
        for (int i = 0; i < retryTimesOnRemoteException; i++) {
            try {
                return doInvoke(invocation);
            } catch (RemoteException ex) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Retry to invoke rmi method failed, try the number of time {}", i + 1);
                }
            }
        }
        throw e;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prepare();
        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
    }

    public void prepare() {
        for (String serviceUrl : serviceUrls) {
            RmiClientInterceptor rmiClientInterceptor = new RmiClientInterceptor();
            rmiClientInterceptor.setServiceInterface(this.serviceInterface);
            rmiClientInterceptor.setServiceUrl(serviceUrl);
            rmiClientInterceptor.setRefreshStubOnConnectFailure(refreshStubOnConnectFailure);
            rmiClientInterceptor.prepare();
            this.rmiClientInterceptors.add(rmiClientInterceptor);
        }
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

    public int getRetryTimesOnRemoteException() {
        return retryTimesOnRemoteException;
    }

    public void setRetryTimesOnRemoteException(int retryTimesOnRemoteException) {
        this.retryTimesOnRemoteException = retryTimesOnRemoteException;
    }
}
