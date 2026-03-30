package Examples.Ex3;


import DI_container.DI_container;
import DI_container.DI_containerImpl;
import Examples.Ex2.Garage;

public class Example3_1 {

    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/Ex3/Ex3_1.xml");

        Car car = diContainer.get("car", Car.class);

        car.startCar();

        System.out.println(car);
    }

}
