package la.xiaoxiao.boxster.test;

import la.xiaoxiao.boxster.MultiRmiServiceExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestConfiguration {

//    @Bean
//    public MultiRmiServiceExporter multiRmiServiceExporter() {
//        MultiRmiServiceExporter exporter = new MultiRmiServiceExporter();
//        List<String> descriptions = new ArrayList<>();
//        descriptions.add("la.xiaoxiao.boxster.test.Car:la.xiaoxiao.boxster.test.Benz:benz:Benz");
//        descriptions.add("la.xiaoxiao.boxster.test.Car:la.xiaoxiao.boxster.test.Volkswagen:volkswagen:Volkswagen");
//        descriptions.add("la.xiaoxiao.boxster.test.Car:la.xiaoxiao.boxster.test.Bmw:bmw:BMW");
//        exporter.setServiceDescriptions(descriptions);
//        return exporter;
//    }


}
