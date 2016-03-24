package com.ny.boxster.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("benz")
public class Benz implements Car {

    @Override
    public String getName() {
        return "Mercedes Benz";
    }
}
