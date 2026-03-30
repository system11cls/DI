package Examples.Ex3;

import javax.inject.Inject;

public class Car {
    public String model;
    @Inject
    public Engine engine;

    public Car(String model) {
        this.model = model;
    }

    public Car() {}

    public void startCar() {
        System.out.println("Автомобиль готов к поездке");
        engine.start();
    }
}
