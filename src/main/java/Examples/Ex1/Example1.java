package Examples.Ex1;


import DI_container.*;

public class Example1 {

    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/EX1/Ex1_1.xml");

        Garage garage = diContainer.get("garage", Garage.class);

        garage.showInfo();

        System.out.println(garage);
        System.out.println(garage.car);
        System.out.println(garage.car.engine);
    }

}
