package DI_container.BeanData.Classes;

import DI_container.BeanData.BeanInfo;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Exceptions.BeanClassesControllerException;
import DI_container.Exceptions.DiProxiesGenException;

import java.util.*;

public class BeanClassesController {
    private final Map<String, BeanClass<?>> beanClasses = new HashMap<>();
    private final Map<String, BeanInfo> infos;
    private final Map<String, List<BeanClass<?>>> beanClassesByInterface = new HashMap<>();
    private final Set<String> classesVisited = new HashSet<>();
    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private final ScopeFactoty scopeFactoty;
    private static final Map<String, Class<?>> PRIMITIVE_CLASSES = new HashMap<>();


    static {
        PRIMITIVE_CLASSES.put("boolean", Boolean.class);
        PRIMITIVE_CLASSES.put("byte", Byte.class);
        PRIMITIVE_CLASSES.put("char", Character.class);
        PRIMITIVE_CLASSES.put("short", Short.class);
        PRIMITIVE_CLASSES.put("int", Integer.class);
        PRIMITIVE_CLASSES.put("long", Long.class);
        PRIMITIVE_CLASSES.put("float", Float.class);
        PRIMITIVE_CLASSES.put("double", Double.class);
        PRIMITIVE_CLASSES.put("void", Void.class);
        PRIMITIVE_CLASSES.put("string", String.class);
    }

    public BeanClassesController(Map<String, BeanInfo> infos, ScopeFactoty scopeFactoty) throws BeanClassesControllerException {
        this.infos = infos;
        this.scopeFactoty = scopeFactoty;
        try {
            for (var info : infos.values()) {
                classesVisited.clear();
                createBeanClass(info);
            }

            for (var info : infos.values()) {
                setLazyArgs(info, this.beanClasses.get(info.name));
            }

            for (var info : infos.values()) {
                setSettersArgs(info, this.beanClasses.get(info.name));
            }

            for (var info : infos.values()) {
                setInjected(info, this.beanClasses.get(info.name));
            }
        } catch (Exception e) {
            System.err.println("Причина ошибки: " + ((Throwable) e).getClass().getName());
            System.err.println("Сообщение: " + e.getMessage());
            throw new BeanClassesControllerException(e + "\nПричина ошибки: " + ((Throwable) e).getClass().getName() + "\nСообщение: " + e.getMessage());
        }
    }


    private void createBeanClass(BeanInfo info) throws ClassNotFoundException {
        this.classesVisited.add(info.name);
        if (beanClasses.containsKey(info.name)) return;

        createUncreatedConstructionArgs(info);
        Class<?> tClass = classLoader.loadClass(info.classPath);
        BeanClass<?> beanClass = new BeanClass<>(tClass);
        beanClass.name = info.name;

        setConstructionArgs(info, beanClass);

        beanClasses.put(info.name, beanClass);

        setScope(info, beanClass);

        setInterfaces(info, beanClass);
    }

    private void createUncreatedConstructionArgs(BeanInfo info) throws ClassNotFoundException {
        for (var arg : info.constructor_args) {
            if (PRIMITIVE_CLASSES.containsKey(arg.nameOfBean)) continue;
            if (!this.infos.containsKey(arg.nameOfBean)) {
                throw new RuntimeException("No info about not primitive class: name == " + arg.nameOfBean + "; during " +
                        info.name + " initialisation");
            }

            if (this.classesVisited.contains(arg.nameOfBean) && !arg.isLazy) {
                throw new RuntimeException("Cycle found on " + info.name + " and " + arg.nameOfBean);
            }

            if (!this.beanClasses.containsKey(arg.nameOfBean) && !arg.isLazy) {
                createBeanClass(this.infos.get(arg.nameOfBean));
            }
        }
    }

    private void setConstructionArgs(BeanInfo info, BeanClass<?> beanClass) {
        beanClass.construction_args = new ArrayList<>();
        beanClass.injectedClasses = new HashSet<>();
        beanClass.construction_args_to_setters_gen = new ArrayList<>();
        for (var arg : info.constructor_args) {
            BeanClassA<?> newArg;
            if (PRIMITIVE_CLASSES.containsKey(arg.nameOfBean)) {
                var primClass = PRIMITIVE_CLASSES.get(arg.nameOfBean);
                var primClassInstance = primClass.cast(arg.obj);
                newArg = new BeanClassPrimType<>(primClass, primClassInstance, arg.name);
            }
            else {
                var argClass = this.beanClasses.get(arg.nameOfBean);
                beanClass.injectedClasses.add(argClass);
                newArg = argClass;
            }
            if (!arg.isLazy) beanClass.construction_args.add(newArg);
        }
    }

    private void setScope(BeanInfo info, BeanClass<?> beanClass) {
        beanClass.scope = scopeFactoty.getScope(info.scope);
    }

    private void setInterfaces(BeanInfo info, BeanClass<?> beanClass) {
        for (var inter : info.interfacesImplemented) {
            if (this.beanClassesByInterface.containsKey(inter)) {
                this.beanClassesByInterface.get(inter).add(beanClass);
            }
            else {
                List<BeanClass<?>> newList = new ArrayList<>();
                newList.add(beanClass);
                this.beanClassesByInterface.put(inter, newList);
            }
        }
    }

    private void setLazyArgs(BeanInfo info, BeanClass<?> beanClass) {
        for (var arg : info.constructor_args) {
            if (arg.isLazy) {
                beanClass.construction_args_to_setters_gen.add(beanClasses.get(arg.nameOfBean));
            }
        }
    }

    private void setSettersArgs(BeanInfo info, BeanClass<?> beanClass) {
        beanClass.setters_args = new ArrayList<>();
        for (var arg : info.setters_args) {
            BeanClassA<?> newArg;
            if (PRIMITIVE_CLASSES.containsKey(arg.nameOfBean)) {
                var primClass = PRIMITIVE_CLASSES.get(arg.nameOfBean);
                var primClassInstance = primClass.cast(arg.obj);
                newArg = new BeanClassPrimType<>(primClass, primClassInstance, arg.nameOfBean);
            }
            else {
                if (!this.beanClasses.containsKey(arg.nameOfBean)) {
                    throw new RuntimeException("unknown setters` arg: " + arg.nameOfBean + " in " + info.name + " initialisation");
                }

                var argClass = this.beanClasses.get(arg.nameOfBean);
                beanClass.injectedClasses.add(argClass);
                newArg = argClass;
            }
            beanClass.setters_args.add(newArg);
        }
    }


    private void setInjected(BeanInfo info, BeanClass<?> beanClass) {
        for (var arg : info.injected_classes) {
            beanClass.injectedClasses.add(beanClasses.get(arg));
        }
    }

    public BeanClass<?> getBeanClassByName(String name) {
        if (beanClasses.containsKey(name)) {
            return this.beanClasses.get(name);
        } else {
            throw new RuntimeException("No bean with this name == " + name);
        }
    }
}
