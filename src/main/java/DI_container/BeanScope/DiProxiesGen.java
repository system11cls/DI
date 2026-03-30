package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.Exceptions.DiProxiesGenException;
import DI_container.ProxiesGenerator.ProxiesGenerator;
import javassist.*;
import javassist.bytecode.ClassFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DiProxiesGen implements ProxiesGenerator {
    private final ClassPool classPool = ClassPool.getDefault();
    private final Map<String, Class<?>> generated = new HashMap<>();
    private final String generatedClassSuffix = "_" + UUID.randomUUID().toString().replace("-", "");

    private String generatedTypeName(Class<?> tClass, String beanName) {
        return tClass.getCanonicalName() + beanName + "Generated" + generatedClassSuffix;
    }

    public <T> T getProxy(String name, Class<T> tClass, List<ArgToCreateObjectDto> construction_args,
                          List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                          List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws DiProxiesGenException {

        try {
            Class<?> newClass = null;
            if (!generated.containsKey(name)) {
                CtClass original = classPool.getCtClass(tClass.getCanonicalName());
                CtClass ctClass = classPool.makeClass(generatedTypeName(tClass, name));
                ctClass.setSuperclass(original);
                ctClass.getClassFile().setMajorVersion(ClassFile.JAVA_8);


                CtClass[] params = new CtClass[construction_args.size() + 1];
                int it = 0;

                StringBuilder builder = new StringBuilder();
                builder.append("{\n");
                List<Class<?>> constrTypes = new ArrayList<>();
                List<Object> constrVars = new ArrayList<>();
                for (var arg : construction_args) {
                    if (!arg.isLazy) {
                        params[it++] = classPool.get(arg.type.getCanonicalName());
                        builder.append("this.").append(arg.name).append(" = ").append("$").append(it).append(";\n");
                        constrTypes.add(arg.type);
                        constrVars.add(arg.obj);
                    }
                }
                var metaClass = classPool.get(Metadata.class.getCanonicalName());
                constrTypes.add(Metadata.class);
                constrVars.add(metadata);

                params[it++] = metaClass;
                builder.append("this.metadata = $").append(it).append(";\n}");

                CtField metaField = new CtField(metaClass, "metadata", ctClass);
                ctClass.addField(metaField);

                CtConstructor newConstructor = CtNewConstructor.make(params, new CtClass[]{},
                        builder.toString(), ctClass);

                ctClass.addConstructor(newConstructor);
                CtMethod init = CtNewMethod.make(
                        "        public void init(java.util.List construction_args_to_generate_setters) {\n" +
                                "            try {\n" +
                                "                java.util.Iterator it = construction_args_to_generate_setters.iterator();\n" +
                                "                while (it.hasNext()) {\n" +
                                "                    DI_container.BeanData.ArgToCreateObjectDto arg = (DI_container.BeanData.ArgToCreateObjectDto) it.next();\n" +
                                "                    java.lang.reflect.Field field = this.getClass().getField(arg.name);\n" +
                                "                    field.set(this, arg.obj);\n" +
                                "                }\n" +
                                "            } catch (Exception e) {\n" +
                                "                throw new RuntimeException(e);\n" +
                                "            }\n" +
                                "        }\n", ctClass);

                ctClass.addMethod(init);

                CtMethod cap = CtNewMethod.make(
                        "        private String capitalize(String name) {\n" +
                                "            if (name == null || name.isEmpty()) return name;\n" +
                                "            return name.substring(0, 1).toUpperCase() + name.substring(1);\n" +
                                "        }",
                        ctClass
                );

                ctClass.addMethod(cap);



            CtMethod setSetters = CtNewMethod.make(
                    "public void setAllSetters() {\n" +
                            "\n" +
                            "            String id = (String) metadata.objectToId.get(this);\n" +
                            "            \n" +
                            "            DI_container.BeanData.Objects.BeanObject beanObject = (DI_container.BeanData.Objects.BeanObject)\n" +
                            "                    metadata.IdThreadToBeanObject.get(id);\n" +
                            "\n" +
                            "            if (beanObject == null) {\n" +
                            "                throw new RuntimeException(\"BeanObject not found\".concat(id.toString()));\n" +
                            "            }\n" +
                            "            \n" +
                            "            java.util.Set keySet = (java.util.Set) beanObject.dependecies.keySet();\n" +
                            "            java.util.Iterator iterator = (java.util.Iterator) keySet.iterator();\n" +
                            "\n" +
                            "            try {\n" +
                            "                id = beanObject.workingId;\n" +
                            "                while (iterator.hasNext()) {\n" +
                            "                    Object arg = iterator.next();\n" +
                            "\n" +
                            "                    Object depObj = beanObject.dependecies.get(arg);\n" +
                            "\n" +
                            "                    if (!(depObj instanceof java.util.Map)) {\n" +
                            "                        throw new RuntimeException(\n" +
                            "                                \"Expected Map, but got: \".concat(\n" +
                            "                                        (depObj != null ? depObj.getClass().getName() : \"null\"))\n" +
                            "                        );\n" +
                            "                    }\n" +
                            "                    java.util.Map threadMap = (java.util.Map) depObj;\n" +
                            "\n" +
                            "                    String threadKey = id.concat(Thread.currentThread().getName());\n" +
                            "                    Object objArg = threadMap.get(threadKey);\n" +
                            "\n" +
                            "                    if (objArg == null) {\n" +
                            "                        objArg = threadMap.get(id);\n" +
                            "                        if (objArg == null) throw new RuntimeException(\"Dependency not found for key: \".concat(threadKey).concat(\" object: \").concat(this.toString()));\n" +
                            "                    }\n" +
                            "\n" +
                            "                    if (!(objArg instanceof DI_container.BeanData.Objects.BeanObject)) {\n" +
                            "                        throw new RuntimeException(\n" +
                            "                                \"Expected BeanObject, but got: \".concat(objArg.getClass().getName())\n" +
                            "                        );\n" +
                            "                    }\n" +
                            "                    DI_container.BeanData.Objects.BeanObject beanObjArg =\n" +
                            "                            (DI_container.BeanData.Objects.BeanObject) objArg;\n" +
                            "                    beanObjArg.workingId = beanObject.workingId;\n" +
                            "\n" +
                            "                    try {\n" +
                            "                        String setterName = \"set\".concat(capitalize(beanObjArg.beanClass.name));\n" +
                            "                        java.lang.reflect.Method method = this.getClass().getMethod(\n" +
                            "                                setterName, new Class[] {beanObjArg.beanClass.type}\n" +
                            "                        );\n" +
                            "\n" +
                            "                        if (!beanObjArg.beanClass.type.isInstance(beanObjArg.object)) {\n" +
                            "                            throw new IllegalArgumentException();\n" +
                            "                        }\n" +
                            "\n" +
                            "                        method.invoke(this, new Object[] {beanObjArg.object});\n" +
                            "                    } catch (NoSuchMethodException e) {\n" +
                            "                        java.lang.reflect.Field field = this.getClass().getField(beanObjArg.beanClass.name);\n" +
                            "                        field.set(this, beanObjArg.object);\n" +
                            "                    }\n" +
                            "                }\n" +
                            "            } catch (IllegalAccessException e) {\n" +
                            "                throw new RuntimeException(e);\n" +
                            "            } catch (java.lang.reflect.InvocationTargetException e) {\n" +
                            "                throw new RuntimeException(e);\n" +
                            "            } catch (NoSuchFieldException e) {\n" +
                            "                throw new RuntimeException(e);\n" +
                            "            }\n" +
                            "        }",
                    ctClass
            );

            ctClass.addMethod(setSetters);




            for (var mm : original.getDeclaredMethods()) {
                if (mm.getName().startsWith("set")) continue;

                CtMethod newMethod = CtNewMethod.copy(mm, ctClass, null);
                newMethod.insertBefore("setAllSetters();");
                ctClass.addMethod(newMethod);
            }
                try {
                    //ctClass.writeFile();
                /*
                ClassLoader currentClassLoader = tClass.getClassLoader();
                GeneratedClassLoader loader = new GeneratedClassLoader(
                        currentClassLoader,
                        ctClass.getName(),
                        ctClass.toBytecode()
                );
                */

                    newClass = ctClass.toClass();
                    generated.put(name, newClass);

                    //System.out.println("Metadata ClassLoader: " + Metadata.class.getClassLoader());
                    //System.out.println("Current ClassLoader: " + newClass.getClassLoader());

                    var constr = newClass.getConstructor(constrTypes.toArray(new Class<?>[0]));

                    return tClass.cast(constr.newInstance(constrVars.toArray()));
                } catch (Exception e) {
                    throw new DiProxiesGenException(e.getMessage());
                }
            }
            else {
                newClass = generated.get(name);

                List<Class<?>> constrTypes = new ArrayList<>();
                List<Object> constrVars = new ArrayList<>();
                for (var arg : construction_args) {
                    if (!arg.isLazy) {
                        constrTypes.add(arg.type);
                        constrVars.add(arg.obj);
                    }
                }
                constrTypes.add(Metadata.class);
                constrVars.add(metadata);

                //System.out.println("Metadata ClassLoader: " + Metadata.class.getClassLoader());
                //System.out.println("Current ClassLoader: " + newClass.getClassLoader());

                try {
                    var constr = newClass.getConstructor(constrTypes.toArray(new Class<?>[0]));

                    return tClass.cast(constr.newInstance(constrVars.toArray()));
                }  catch (Exception e) {
                    Throwable detail = e.getCause() != null ? e.getCause() : e;
                    System.err.println("Причина ошибки: " + detail.getClass().getName());
                    System.err.println("Сообщение: " + detail.getMessage());
                    throw new DiProxiesGenException(e + "\nПричина ошибки: " + detail.getClass().getName() + "\nСообщение: " + detail.getMessage());
                }
            }


        } catch (Exception e) {
            System.err.println("Причина ошибки: " + ((Throwable) e).getClass().getName());
            System.err.println("Сообщение: " + e.getMessage());
            throw new DiProxiesGenException(e + "\nПричина ошибки: " + ((Throwable) e).getClass().getName() + "\nСообщение: " + e.getMessage());
        }
    }

    public class GeneratedClassLoader extends ClassLoader {
        private final String targetClassName;
        private final byte[] bytecode;


        public GeneratedClassLoader(ClassLoader parent, String targetClassName, byte[] bytecode) {
            super(parent);
            this.targetClassName = targetClassName;
            this.bytecode = bytecode;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            // Приоритет: сначала ищем в нашем загрузчике
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            if (name.equals(targetClassName)) {
                Class<?> clazz = defineClass(name, bytecode, 0, bytecode.length);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }

            // Для всех остальных классов делегируем родительскому загрузчику
            return super.loadClass(name, resolve);
        }
    }

}

