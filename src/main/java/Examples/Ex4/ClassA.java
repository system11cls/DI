package Examples.Ex4;

public class ClassA {
    public ClassB classB;

    public ClassA() {};

    public ClassA(ClassB classB) {
        this.classB = classB;
        System.out.println("ClassA создан");
    }

    public void useClassB() {
        System.out.println("ClassA использует ClassB");
        classB.doSomething();
    }

    public void doSomething() {
        System.out.println("ClassA делает что-то");
    }
}
