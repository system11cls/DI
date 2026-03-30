package Examples.Ex3;

import DI_container.*;

public class Example3_2 {
    public static void run() {
        DI_container diContainer = new DI_containerImpl("src/main/resources/Ex3/Ex3_1.xml");

        Engine engine = diContainer.getByInterface(Engine.class);

        engine.start();
    }
}
