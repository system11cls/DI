package Examples.Ex2;

public class Engine {
    public String type;
    public Integer horsepower;

    public Engine(){};

    public Engine(String type, Integer horsepower) {
        this.type = type;
        this.horsepower = horsepower;
    }

    public void start() {
        System.out.println("Двигатель: ".concat(this.toString()));
    }

    public void stop() {
        System.out.println("Двигатель остановлен");
    }
}
