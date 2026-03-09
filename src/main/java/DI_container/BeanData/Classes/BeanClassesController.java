package DI_container.BeanData.Classes;

import DI_container.BeanData.BeanInfo;
import DI_container.BeanScope.ScopeFactoty;

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
        PRIMITIVE_CLASSES.put("boolean", boolean.class);
        PRIMITIVE_CLASSES.put("byte", byte.class);
        PRIMITIVE_CLASSES.put("char", char.class);
        PRIMITIVE_CLASSES.put("short", short.class);
        PRIMITIVE_CLASSES.put("int", int.class);
        PRIMITIVE_CLASSES.put("long", long.class);
        PRIMITIVE_CLASSES.put("float", float.class);
        PRIMITIVE_CLASSES.put("double", double.class);
        PRIMITIVE_CLASSES.put("void", void.class);
        PRIMITIVE_CLASSES.put("string", String.class);
    }

    public BeanClassesController(Map<String, BeanInfo> infos, ScopeFactoty scopeFactoty) throws ClassNotFoundException {
        this.infos = infos;
        this.scopeFactoty = scopeFactoty;
        for (var info : infos.values()) {
            createBeanClass(info);
        }

        for (var info : infos.values()) {
            setSettersArgs(info, this.beanClasses.get(info.name));
        }

        for (var info : infos.values()) {
            setInjected(info, this.beanClasses.get(info.name));
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
            if (PRIMITIVE_CLASSES.containsKey(arg.classPath)) continue;
            if (!this.infos.containsKey(arg.classPath)) {
                throw new RuntimeException("No info about not primitive class: name == " + arg.classPath + "; during " +
                        info.name + " initialisation");
            }

            if (this.classesVisited.contains(arg.classPath) && !arg.isLazy) {
                throw new RuntimeException("Cycle found on " + info.name + " and " + arg.classPath);
            }

            if (!this.beanClasses.containsKey(arg.classPath)) {
                createBeanClass(this.infos.get(arg.classPath));
            }
        }
    }

    private void setConstructionArgs(BeanInfo info, BeanClass<?> beanClass) {
        for (var arg : info.constructor_args) {
            BeanClassA<?> newArg;
            if (PRIMITIVE_CLASSES.containsKey(arg.classPath)) {
                var primClass = PRIMITIVE_CLASSES.get(arg.classPath);
                var primClassInstance = primClass.cast(arg.obj);
                newArg = new BeanClassPrimType<>(primClass, primClassInstance, arg.classPath);
            }
            else {
                var argClass = this.beanClasses.get(arg.classPath);
                beanClass.injectedClasses.add(argClass);
                newArg = argClass;
            }
            if (arg.isLazy) {
                beanClass.construction_args_to_setters_gen.add(newArg);
            }
            beanClass.construction_args.add(newArg);
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

    private void setSettersArgs(BeanInfo info, BeanClass<?> beanClass) {
        for (var arg : info.setters_args) {
            BeanClassA<?> newArg;
            if (PRIMITIVE_CLASSES.containsKey(arg.classPath)) {
                var primClass = PRIMITIVE_CLASSES.get(arg.classPath);
                var primClassInstance = primClass.cast(arg.obj);
                newArg = new BeanClassPrimType<>(primClass, primClassInstance, arg.classPath);
            }
            else {
                if (!this.beanClasses.containsKey(arg.classPath)) {
                    throw new RuntimeException("unknown setters` arg: " + arg.classPath + " in " + info.name + " initialisation");
                }

                var argClass = this.beanClasses.get(arg.classPath);
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
