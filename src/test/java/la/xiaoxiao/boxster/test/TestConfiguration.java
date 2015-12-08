package la.xiaoxiao.boxster.test;

import la.xiaoxiao.boxster.MultiRmiServiceExporter;
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
        descriptions.add("Car:Benz:benz:Benz");
        descriptions.add("Car:Volkswagen:volkswagen:Volkswagen");
        descriptions.add("Car:Bmw:bmw:BMW");
        exporter.setServiceDescriptions(descriptions);
        return exporter;
    }

}
