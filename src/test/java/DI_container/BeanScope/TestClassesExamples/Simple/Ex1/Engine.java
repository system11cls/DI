package DI_container.BeanScope.TestClassesExamples.Simple.Ex1;

public class Engine {
    public String type;
    public int horsepower;

    public Engine(){};

    public Engine(String type, int horsepower) {
        this.type = type;
        this.horsepower = horsepower;
    }

    public void start() {
        System.out.println("Двигатель");
    }

    public void stop() {
        System.out.println("Двигатель остановлен");
    }
}
