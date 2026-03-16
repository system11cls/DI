package DI_container.BeanScope.TestClassesExamples.Simple.Ex1;

public class Car {
    public String model;
    public Engine engine; // Зависимость

    public Car(String model, Engine engine) {
        this.model = model;
        this.engine = engine;
    }

    public Car() {}

    public void startCar() {
        System.out.println("Автомобиль готов к поездке");
        engine.start();
    }

    public void stopCar() {
        engine.stop();
        System.out.println("Автомобиль остановлен");
    }
}
