package Examples.Ex1;

public class Garage {
    public Car car;

    public Garage() {}

    public Garage(Car car) {
        this.car = car;
    }

    public void showInfo() {
        System.out.println("======");
        car.startCar();
    }
}
