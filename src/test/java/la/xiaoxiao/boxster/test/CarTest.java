package la.xiaoxiao.boxster.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-boxster.xml")
public class CarTest {

    @Autowired
    @Qualifier("bmw")
    Car bmw;

    @Autowired
    @Qualifier("benz")
    Car benz;

    @Test
    public void testCar() {
        System.out.println(bmw.getName());
        System.out.println(benz.getName());
    }
}
