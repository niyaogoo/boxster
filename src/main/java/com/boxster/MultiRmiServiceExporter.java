package com.boxster;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.rmi.RmiServiceExporter;
import org.springframework.util.Assert;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class MultiRmiServiceExporter implements InitializingBean, ApplicationContextAware {

    // 服务描述, 格式为{serviceInterface}:{service}:{qualifierName}:{serviceName}
    private List<String> serviceDescriptions;

    private int registryPort = 1099;

    private List<RmiServiceExporter> rmiServiceExporters = new ArrayList<>();

    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        prepare();
    }

    public void prepare() throws RemoteException {
        Assert.notEmpty(serviceDescriptions, "ServiceInterfaces is empty");
        for (String serviceDescription : serviceDescriptions) {
            ServiceDescription description = this.extractServiceDescription(serviceDescription);
            // acquire service service from application context
            Object impl;
            if (description.qualifierName == null || "".equals(description.qualifierName.trim())) {
                // get bean by type
                impl = context.getBean(description.service);
            } else {
                impl = context.getBean(description.qualifierName, description.service);
            }

            RmiServiceExporter serviceExporter = new RmiServiceExporter();
            serviceExporter.setRegistryPort(this.registryPort);
            serviceExporter.setServiceInterface(description.serviceInterface);
            serviceExporter.setService(impl);
            serviceExporter.setServiceName(description.serviceName);

            serviceExporter.prepare();
            rmiServiceExporters.add(serviceExporter);
        }
    }

    public List<String> getServiceDescriptions() {
        return serviceDescriptions;
    }

    public void setServiceDescriptions(List<String> serviceDescriptions) {
        this.serviceDescriptions = serviceDescriptions;
    }

    public int getRegistryPort() {
        return registryPort;
    }

    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private ServiceDescription extractServiceDescription(String serviceDescription) {

        Assert.notNull(serviceDescription, "serviceDescription is null");
        String[] description = serviceDescription.split(":");
        if (description.length != 4) {
            throw new IllegalArgumentException(
                    "serviceDescription must make up with {serviceInterface}:{service}:{qualifierName}:{serviceName}");
        }
        try {
            Class<?> serviceInterface = Class.forName(description[0]);
            Class<?> service = Class.forName(description[1]);
            String qualifierName = description[2];
            String serviceName = description[3];
            return new ServiceDescription(serviceInterface, service, qualifierName, serviceName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not find interface or class", e);
        }
    }

    class ServiceDescription {
        Class<?> serviceInterface;
        Class<?> service;
        String qualifierName;
        String serviceName;

        public ServiceDescription(Class<?> serviceInterface, Class<?> service,
                                  String qualifierName, String serviceName) {
            this.serviceInterface = serviceInterface;
            this.service = service;
            this.qualifierName = qualifierName;
            this.serviceName = serviceName;
        }

    }

}