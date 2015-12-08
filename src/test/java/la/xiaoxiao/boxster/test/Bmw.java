package la.xiaoxiao.boxster.test;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("bmw")
public class Bmw implements Car {
    @Override
    public String getName() {
        return "BMW";
    }
}
