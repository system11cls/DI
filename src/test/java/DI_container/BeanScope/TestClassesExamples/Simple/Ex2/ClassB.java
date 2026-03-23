package DI_container.BeanScope.TestClassesExamples.Simple.Ex2;

public class ClassB {
    public ClassA classA;

    public ClassB() {}

    public ClassB(ClassA classA) {
        this.classA = classA;
        System.out.println("ClassB создан");
    }

    public void useClassA() {
        System.out.println("ClassB использует ClassA");
        classA.doSomething();
    }

    public void doSomething() {
        System.out.println("ClassB делает что-то");
    }
}