package com.ny.boxster.test;

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
    @Qualifier("benz")
    Car benz;

    Object monitor = new Object();

    @Test
    public void testCar() throws InterruptedException {
        System.out.println(benz.getName());
    }
}
