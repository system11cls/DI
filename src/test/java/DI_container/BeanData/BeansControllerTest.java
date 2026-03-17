package DI_container.BeanData;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Car;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Engine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.*;

class BeansControllerTest {

    @Test
    void testControllerSimpleEx1Singleton() throws ClassNotFoundException {
        var infos = new HashMap<String, BeanInfo>();
        var engineInfo = new BeanInfo();
        engineInfo.name = "engine";
        engineInfo.setters_args = List.of();
        engineInfo.injected_classes = List.of();
        engineInfo.interfacesImplemented = List.of();
        engineInfo.constructor_args = List.of(
                new ArgInfo("type","string", "XM1"),
                new ArgInfo("horsepower","int", 120)
        );
        engineInfo.classPath = Engine.class.getCanonicalName();
        engineInfo.scope = "singleton";
        infos.put(engineInfo.name, engineInfo);

        var carInfo = new BeanInfo();
        carInfo.classPath = Car.class.getCanonicalName();
        carInfo.constructor_args = List.of(
                new ArgInfo("model","string", "XM1M"),
                new ArgInfo("engine","engine", null)
        );
        carInfo.injected_classes = List.of(Engine.class.getCanonicalName());
        carInfo.name = "car";
        carInfo.interfacesImplemented = List.of();
        carInfo.setters_args = List.of();
        carInfo.scope = "singleton";
        infos.put(carInfo.name, carInfo);

        var controller = new BeansController(
            infos, new ScopeFactoty(), new Metadata()
        );

        Car car = controller.getObject("car", Car.class);

        assertNotNull(car);
        assertNotNull(car.engine);
        assertEquals(120, car.engine.horsepower);
    }

    @Test
    void testSingletone() throws ClassNotFoundException {
        var infos = new HashMap<String, BeanInfo>();
        var engineInfo = new BeanInfo();
        engineInfo.name = "engine";
        engineInfo.setters_args = List.of();
        engineInfo.injected_classes = List.of();
        engineInfo.interfacesImplemented = List.of();
        engineInfo.constructor_args = List.of(
                new ArgInfo("type","string", "XM1"),
                new ArgInfo("horsepower","int", 120)
        );
        engineInfo.classPath = Engine.class.getCanonicalName();
        engineInfo.scope = "singleton";
        infos.put(engineInfo.name, engineInfo);

        var carInfo = new BeanInfo();
        carInfo.classPath = Car.class.getCanonicalName();
        carInfo.constructor_args = List.of(
                new ArgInfo("model","string", "XM1M"),
                new ArgInfo("engine","engine", null)
        );
        carInfo.injected_classes = List.of(Engine.class.getCanonicalName());
        carInfo.name = "car";
        carInfo.interfacesImplemented = List.of();
        carInfo.setters_args = List.of();
        carInfo.scope = "singleton";
        infos.put(carInfo.name, carInfo);

        var controller = new BeansController(
                infos, new ScopeFactoty(), new Metadata()
        );

        Car car = controller.getObject("car", Car.class);
        Car car2 = controller.getObject("car", Car.class);
        assertEquals(car2, car);
    }


    @Test
    void testSingletonPrototype() throws ClassNotFoundException {
        var infos = new HashMap<String, BeanInfo>();
        var engineInfo = new BeanInfo();
        engineInfo.name = "engine";
        engineInfo.setters_args = List.of();
        engineInfo.injected_classes = List.of();
        engineInfo.interfacesImplemented = List.of();
        engineInfo.constructor_args = List.of(
                new ArgInfo("type","string", "XM1"),
                new ArgInfo("horsepower","int", 120)
        );
        engineInfo.classPath = Engine.class.getCanonicalName();
        engineInfo.scope = "prototype";
        infos.put(engineInfo.name, engineInfo);

        var carInfo = new BeanInfo();
        carInfo.classPath = Car.class.getCanonicalName();
        carInfo.constructor_args = List.of(
                new ArgInfo("model","string", "XM1M"),
                new ArgInfo("engine","engine", null)
        );
        carInfo.injected_classes = List.of(Engine.class.getCanonicalName());
        carInfo.name = "car";
        carInfo.interfacesImplemented = List.of();
        carInfo.setters_args = List.of();
        carInfo.scope = "singleton";
        infos.put(carInfo.name, carInfo);

        var controller = new BeansController(
                infos, new ScopeFactoty(), new Metadata()
        );

        Car car = controller.getObject("car", Car.class);
        Engine engine = controller.getObject("engine", Engine.class);

        assertNotNull(car.engine);
        assertNotEquals(car.engine, engine);
    }

    @Test
    void testThread() throws ClassNotFoundException, ExecutionException, InterruptedException {
        var infos = new HashMap<String, BeanInfo>();
        var engineInfo = new BeanInfo();
        engineInfo.name = "engine";
        engineInfo.setters_args = List.of();
        engineInfo.injected_classes = List.of();
        engineInfo.interfacesImplemented = List.of();
        engineInfo.constructor_args = List.of(
                new ArgInfo("type","string", "XM1"),
                new ArgInfo("horsepower","int", 120)
        );
        engineInfo.classPath = Engine.class.getCanonicalName();
        engineInfo.scope = "thread";
        infos.put(engineInfo.name, engineInfo);

        var controller = new BeansController(
                infos, new ScopeFactoty(), new Metadata()
        );

        Engine engine = controller.getObject("engine", Engine.class);

        Engine engine1;
        Callable<Engine> ctask = () -> {
          return controller.getObject("engine", Engine.class);
        };
        FutureTask<Engine> future = new FutureTask<>(ctask);
        new Thread(future).start();

        engine1 = future.get();
        assertNotNull(engine);
        assertNotNull(engine1);
        assertNotEquals(engine1, engine);
    }

    @Test
    void testSingletonThread() throws ClassNotFoundException, ExecutionException, InterruptedException {
        var infos = new HashMap<String, BeanInfo>();
        var engineInfo = new BeanInfo();
        engineInfo.name = "engine";
        engineInfo.setters_args = List.of();
        engineInfo.injected_classes = List.of();
        engineInfo.interfacesImplemented = List.of();
        engineInfo.constructor_args = List.of(
                new ArgInfo("type","string", "XM1"),
                new ArgInfo("horsepower","int", 120)
        );
        engineInfo.classPath = Engine.class.getCanonicalName();
        engineInfo.scope = "thread";
        infos.put(engineInfo.name, engineInfo);

        var carInfo = new BeanInfo();
        carInfo.classPath = Car.class.getCanonicalName();
        carInfo.constructor_args = List.of(
                new ArgInfo("model","string", "XM1M"),
                new ArgInfo("engine","engine", null)
        );
        carInfo.injected_classes = List.of(Engine.class.getCanonicalName());
        carInfo.name = "car";
        carInfo.interfacesImplemented = List.of();
        carInfo.setters_args = List.of();
        carInfo.scope = "singleton";
        infos.put(carInfo.name, carInfo);

        var controller = new BeansController(
                infos, new ScopeFactoty(), new Metadata()
        );

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