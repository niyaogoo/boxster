package com.boxster.test;

import com.boxster.support.MultiRmiServiceExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestConfiguration {

    @Bean
    public Bmw getBmw() {
        return new Bmw();
    }

    @Bean
    public MultiRmiServiceExporter multiRmiServiceExporter() {
        MultiRmiServiceExporter exporter = new MultiRmiServiceExporter();
        List<String> descriptions = new ArrayList<>();
        descriptions.add("com.boxster.test.Car:com.boxster.test.Benz:benz:Benz");
        descriptions.add("com.boxster.test.Car:com.boxster.test.Volkswagen:volkswagen:Volkswagen");
        descriptions.add("com.boxster.test.Car:com.boxster.test.Bmw:bmw:BMW");
        exporter.setServiceDescriptions(descriptions);
        return exporter;
    }

}
