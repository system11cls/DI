package DI_container.BeanData;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Car;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Engine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

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
}