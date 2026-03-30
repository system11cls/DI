package Examples.Ex2;


import DI_container.DI_container;
import DI_container.DI_containerImpl;

public class Example2_1 {

    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/Ex2/Ex2_1.xml");

        Garage garage = diContainer.get("garage", Garage.class);

        garage.showInfo();

        System.out.println(garage);
        System.out.println(garage.car);
        System.out.println(garage.car.engine);
    }

}
