package Examples.Ex1;

import DI_container.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Example3 {

    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/EX1/Ex1_3.xml");

        Garage garage = diContainer.get("garage", Garage.class);

        garage.showInfo();
        Callable<Car> ctask = () -> {
            Garage garage1 = diContainer.get("garage", Garage.class);
            garage.showInfo();
            return null;
        };
        FutureTask<Car> future = new FutureTask<>(ctask);
        new Thread(future).start();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        garage.showInfo();

        System.out.println(diContainer.getObjectsCnt("engine"));
        System.out.println(diContainer.getObjectsCnt("car"));
    }
}
