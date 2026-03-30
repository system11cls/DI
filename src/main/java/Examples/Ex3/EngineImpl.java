package Examples.Ex3;

public class EngineImpl implements Engine {
    public String type;
    public Integer horsepower;

    public EngineImpl(){};

    public EngineImpl(String type, Integer horsepower) {
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
