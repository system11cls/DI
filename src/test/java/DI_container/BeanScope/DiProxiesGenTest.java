package DI_container.BeanScope;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Car;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex1.Engine;
import DI_container.Config.BeanConfig;
import DI_container.Config.BeanInfoMapper;
import DI_container.Config.ContainerConfig;
import DI_container.Config.XmlConfigTestSupport;
import DI_container.ProxiesGenerator.ProxiesGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            ContainerConfig cfg = XmlConfigTestSupport.load(XmlConfigTestSupport.EX1 + "singleton.xml");
            Map<String, BeanConfig> byId = BeanInfoMapper.indexById(cfg);

            Engine engine = generator.getProxy(
                    "engine",
                    Engine.class,
                    BeanInfoMapper.toProxyConstructionArgs(byId.get("engine"), Engine.class, byId, Map.of()),
                    List.of(),
                    List.of(),
                    metadata
            );

            assertNotNull(engine);
            assertInstanceOf(Engine.class, engine);
            assertEquals(120, engine.horsepower);

            Map<String, Object> resolved = new HashMap<>();
            resolved.put("engine", engine);

            Car car = generator.getProxy(
                    "car",
                    Car.class,
                    BeanInfoMapper.toProxyConstructionArgs(byId.get("car"), Car.class, byId, resolved),
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
            ContainerConfig cfg = XmlConfigTestSupport.load(XmlConfigTestSupport.EX1 + "singleton.xml");
            Map<String, BeanConfig> byId = BeanInfoMapper.indexById(cfg);

            Engine engine = generator.getProxy(
                    "engine",
                    Engine.class,
                    BeanInfoMapper.toProxyConstructionArgs(byId.get("engine"), Engine.class, byId, Map.of()),
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
                    BeanInfoMapper.toProxyConstructionArgs(byId.get("engine"), Engine.class, byId, Map.of()),
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
