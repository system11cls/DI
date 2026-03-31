package Examples.Ex4;

import DI_container.DI_container;
import DI_container.DI_containerImpl;

public class Example4_3 {
    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/Ex4/Ex2.xml");

        ClassB b = diContainer.get("classB", ClassB.class);

        b.useClassA();
    }

}
