package Examples.Ex4;

import DI_container.*;

public class Example4_1 {

    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/Ex4/Ex1.xml");

        ClassA a = diContainer.get("classA", ClassA.class);
    }
}
