package Examples.Ex4;

import DI_container.*;

public class Example4_2 {
    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/Ex4/Ex2.xml");

        ClassA a = diContainer.get("classA", ClassA.class);

        a.useClassB();
    }

}
