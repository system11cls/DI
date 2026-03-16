package DI_container.BeanScope;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Car;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Engine;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.SE1Info;
import DI_container.ProxiesGenerator.ProxiesGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiProxiesGenTest {
    public static ProxiesGenerator generator;
    public Metadata metadata = new Metadata();

    @BeforeAll
    static void setGen() {
        generator = new DiProxiesGen();
    }

    @BeforeEach
    public void clear() {
        metadata.IdThreadToBeanObject.clear();
        metadata.objectToId.clear();
    }


    @Test
    void getProxySimpleEx1Test() {
        try {
            Engine engine = generator.getProxy(
                    "engine",
                    Engine.class,
                    SE1Info.cargcEngine(),
                    List.of(),
                    List.of(),
                    metadata
            );

            assertNotNull(engine);
            assertInstanceOf(Engine.class, engine);
            assertEquals(120, engine.horsepower);

            Car car = generator.getProxy(
                    "car",
                    Car.class,
                    SE1Info.cargsCar(engine),
                    List.of(),
                    List.of(),
                    metadata
            );


            assertNotNull(car);
            assertInstanceOf(Car.class, car);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Test
    void getProxyTestDouble() {
        try {
            Engine engine = generator.getProxy(
                    "engine",
                    Engine.class,
                    SE1Info.cargcEngine(),
                    List.of(),
                    List.of(),
                    metadata
            );

            assertNotNull(engine);
            assertInstanceOf(Engine.class, engine);
            assertEquals(120, engine.horsepower);

            Engine engine2 = generator.getProxy(
                    "engine",
                    Engine.class,
                    SE1Info.cargcEngine(),
                    List.of(),
                    List.of(),
                    metadata
            );

            assertNotNull(engine2);
            assertInstanceOf(Engine.class, engine2);
            assertEquals(120, engine2.horsepower);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}