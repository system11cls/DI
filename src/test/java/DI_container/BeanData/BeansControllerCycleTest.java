package DI_container.BeanData;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex2.ClassA;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex2.ClassB;
import DI_container.Config.BeanInfoMapper;
import DI_container.Config.XmlConfigTestSupport;
import DI_container.Exceptions.BeanClassesControllerException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeansControllerCycleTest {

    @Test
    public void testException() {
        var cfg = XmlConfigTestSupport.load(XmlConfigTestSupport.EX2 + "cycle-exception.xml");

        assertThrows(BeanClassesControllerException.class,
                () -> new BeansController(
                        BeanInfoMapper.toBeanInfoMap(cfg),
                        new ScopeFactoty(),
                        new Metadata()
                ));
    }

    @Test
    public void testLaziness() {
        var cfg = XmlConfigTestSupport.load(XmlConfigTestSupport.EX2 + "cycle-lazy.xml");

        assertDoesNotThrow(() -> {
            var controller = new BeansController(
                    BeanInfoMapper.toBeanInfoMap(cfg),
                    new ScopeFactoty(),
                    new Metadata()
            );

            var a = controller.getObject("classA", ClassA.class);
            a.useClassB();
            a.classB.useClassA();
            assertNotNull(a.classB.classA);
        });
    }

    @Test
    public void testLazinessBack() {
        var cfg = XmlConfigTestSupport.load(XmlConfigTestSupport.EX2 + "cycle-lazy-back.xml");

        assertDoesNotThrow(() -> {
            var controller = new BeansController(
                    BeanInfoMapper.toBeanInfoMap(cfg),
                    new ScopeFactoty(),
                    new Metadata()
            );

            var b = controller.getObject("classB", ClassB.class);
            b.useClassA();
            b.classA.useClassB();
            assertNotNull(b.classA.classB);
        });
    }
}
