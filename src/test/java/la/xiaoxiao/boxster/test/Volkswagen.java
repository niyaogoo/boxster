package la.xiaoxiao.boxster.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("volkswagen")
public class Volkswagen implements Car {
    @Override
    public String getName() {
        return "Volkswagen";
    }
}
