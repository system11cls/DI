package DI_container.Config;


import DI_container.BeanData.ArgInfo;
import DI_container.BeanData.BeanInfo;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.Map;

public class AnnotationAdder {

    public static void addAnnotations(Map<String, BeanInfo> infos) throws ClassNotFoundException {
        var loader = ClassLoader.getSystemClassLoader();

        for (var infoKey : infos.keySet()) {
            var info = infos.get(infoKey);
            var tClass = loader.loadClass(info.classPath);

            for (var inter : tClass.getInterfaces()) {
                info.interfacesImplemented.add(inter.getCanonicalName());
            }

            for (Field field : tClass.getFields()) {
                if (field.isAnnotationPresent(Inject.class) && field.isAnnotationPresent(Named.class)) {
                    var named = field.getAnnotation(Named.class).value();
                    info.setters_args.add(new ArgInfo(field.getName(), named, null));
                    info.injected_classes.add(named);
                } else if (field.isAnnotationPresent(Inject.class)) {
                    info.setters_args.add(new ArgInfo(field.getName(), null, field.getType().getCanonicalName()));
                }
            }
        }
    }

}
