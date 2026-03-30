package Examples.Ex2;

import javax.inject.Inject;
import javax.inject.Named;

public class Garage {
    @Inject
    @Named("car")
    public Car car;

    public Garage() {}

    public void showInfo() {
        System.out.println("======");
        car.startCar();
    }
}
