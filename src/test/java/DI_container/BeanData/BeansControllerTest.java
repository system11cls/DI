package DI_container.BeanData;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Car;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Engine;
import DI_container.Config.BeanInfoMapper;
import DI_container.Config.XmlConfigTestSupport;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.*;

class BeansControllerTest {

    private BeansController controllerFromEx1(String xmlFileName) {
        var cfg = XmlConfigTestSupport.load(XmlConfigTestSupport.EX1 + xmlFileName);
        return new BeansController(
                BeanInfoMapper.toBeanInfoMap(cfg),
                new ScopeFactoty(),
                new Metadata()
        );
    }

    @Test
    void testControllerSimpleEx1Singleton() {
        var controller = controllerFromEx1("singleton.xml");

        Car car = controller.getObject("car", Car.class);

        assertNotNull(car);
        assertNotNull(car.engine);
        assertEquals(120, car.engine.horsepower);
    }

    @Test
    void testSingletone() {
        var controller = controllerFromEx1("singleton.xml");

        Car car = controller.getObject("car", Car.class);
        Car car2 = controller.getObject("car", Car.class);
        assertEquals(car2, car);
    }

    @Test
    void testSingletonPrototype() {
        var controller = controllerFromEx1("prototype-engine.xml");

        Car car = controller.getObject("car", Car.class);
        Engine engine = controller.getObject("engine", Engine.class);

        assertNotNull(car.engine);
        assertNotEquals(car.engine, engine);
    }

    @Test
    void testThread() throws ExecutionException, InterruptedException {
        var controller = controllerFromEx1("thread-engine.xml");

        Engine engine = controller.getObject("engine", Engine.class);

        Engine engine1;
        Callable<Engine> ctask = () -> controller.getObject("engine", Engine.class);
        FutureTask<Engine> future = new FutureTask<>(ctask);
        new Thread(future).start();

        engine1 = future.get();
        assertNotNull(engine);
        assertNotNull(engine1);
        assertNotEquals(engine1, engine);
    }

    @Test
    void testSingletonThread() throws ExecutionException, InterruptedException {
        var controller = controllerFromEx1("singleton-car-thread-engine.xml");

        Car car = controller.getObject("car", Car.class);
        Engine engine = controller.getObject("engine", Engine.class);

        Callable<Car> ctask = () -> {
            Car car1 = controller.getObject("car", Car.class);
            car1.startCar();
            return null;
        };
        FutureTask<Car> future = new FutureTask<>(ctask);
        new Thread(future).start();
        future.get();

        assertNotEquals(engine, car.engine);
        car.startCar();
        assertEquals(engine, car.engine);
    }
}
