package DI_container.BeanData;

import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex2.ClassA;
import DI_container.BeanScope.TestClassesExamples.Simple.Ex2.ClassB;
import DI_container.Exceptions.BeanClassesControllerException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BeansControllerCycleTest {

    @Test
    public void testException() {
        var infos = new HashMap<String, BeanInfo>();
        var AInfo = new BeanInfo();
        AInfo.name = "classA";
        AInfo.setters_args = List.of();
        AInfo.injected_classes = List.of(
                ClassB.class.getCanonicalName()
        );
        AInfo.interfacesImplemented = List.of();
        AInfo.constructor_args = List.of(
                new ArgInfo("classB","classB", null)
        );
        AInfo.classPath = ClassA.class.getCanonicalName();
        AInfo.scope = "singleton";
        infos.put(AInfo.name, AInfo);

        var BInfo = new BeanInfo();
        BInfo.classPath = ClassB.class.getCanonicalName();
        BInfo.constructor_args = List.of(
                new ArgInfo("classA","classA", null)
        );
        BInfo.injected_classes = List.of(ClassA.class.getCanonicalName());
        BInfo.name = "classB";
        BInfo.interfacesImplemented = List.of();
        BInfo.setters_args = List.of();
        BInfo.scope = "singleton";
        infos.put(BInfo.name, BInfo);

        assertThrows(BeanClassesControllerException.class,
                () -> {
                    var controller = new BeansController(
                            infos, new ScopeFactoty(), new Metadata()
                    );
                });
    }

    @Test
    public void testLaziness() {
        var infos = new HashMap<String, BeanInfo>();
        var AInfo = new BeanInfo();
        AInfo.name = "classA";
        AInfo.setters_args = List.of();
        AInfo.injected_classes = List.of(
                ClassB.class.getCanonicalName()
        );
        AInfo.interfacesImplemented = List.of();
        AInfo.constructor_args = List.of(
                new ArgInfo("classB","classB", null)
        );
        AInfo.classPath = ClassA.class.getCanonicalName();
        AInfo.scope = "singleton";
        infos.put(AInfo.name, AInfo);

        var BInfo = new BeanInfo();
        BInfo.classPath = ClassB.class.getCanonicalName();
        BInfo.constructor_args = List.of(
                new ArgInfo("classA","classA", null, true)
        );
        BInfo.injected_classes = List.of(ClassA.class.getCanonicalName());
        BInfo.name = "classB";
        BInfo.interfacesImplemented = List.of();
        BInfo.setters_args = List.of();
        BInfo.scope = "singleton";
        infos.put(BInfo.name, BInfo);



        assertDoesNotThrow(() -> {
            var controller = new BeansController(
                    infos, new ScopeFactoty(), new Metadata()
            );

            var a = controller.getObject("classA", ClassA.class);
            a.useClassB();
        });
    }

    @Test
    public void testLazinessBack() {
        var infos = new HashMap<String, BeanInfo>();
        var AInfo = new BeanInfo();
        AInfo.name = "classA";
        AInfo.setters_args = List.of();
        AInfo.injected_classes = List.of(
                ClassB.class.getCanonicalName()
        );
        AInfo.interfacesImplemented = List.of();
        AInfo.constructor_args = List.of(
                new ArgInfo("classB","classB", null)
        );
        AInfo.classPath = ClassA.class.getCanonicalName();
        AInfo.scope = "singleton";
        infos.put(AInfo.name, AInfo);

        var BInfo = new BeanInfo();
        BInfo.classPath = ClassB.class.getCanonicalName();
        BInfo.constructor_args = List.of(
                new ArgInfo("classA","classA", null, true)
        );
        BInfo.injected_classes = List.of(ClassA.class.getCanonicalName());
        BInfo.name = "classB";
        BInfo.interfacesImplemented = List.of();
        BInfo.setters_args = List.of();
        BInfo.scope = "singleton";
        infos.put(BInfo.name, BInfo);



        assertDoesNotThrow(() -> {
            var controller = new BeansController(
                    infos, new ScopeFactoty(), new Metadata()
            );

            var b = controller.getObject("classB", ClassB.class);
            b.useClassA();
        });
    }
}
