package DI_container.BeanData.Metadata;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Objects.BeanObject;

import java.util.HashMap;
import java.util.Map;


public class Metadata {
    public Map<Object, String> objectToId = new HashMap<>();
    public Map<String, BeanObject<?>> IdThreadToBeanObject = new HashMap<>();

    public class Ex {
        Metadata metadata;
        Ex(Metadata metadata) {
            this.metadata = metadata;
        }



        public void setAllSetters() {

            String id = metadata.objectToId.get(this);
            id = id.concat(Thread.currentThread().getName());

            DI_container.BeanData.Objects.BeanObject beanObject =
                    metadata.IdThreadToBeanObject.get(id);

            if (beanObject == null) {
                throw new RuntimeException("BeanObject not found");
            }
            /*
            java.util.Set keySet = beanObject.dependecies.keySet();
            java.util.Iterator iterator = keySet.iterator();

            try {
                while (iterator.hasNext()) {
                    Object arg = iterator.next();

                    Object depObj = beanObject.dependecies.get(arg);

                    if (!(depObj instanceof java.util.Map)) {
                        throw new RuntimeException(
                                "Expected Map, but got: ".concat(
                                        (depObj != null ? depObj.getClass().getName() : "null"))
                        );
                    }
                    java.util.Map threadMap = (java.util.Map) depObj;

                    String threadKey = id.concat(Thread.currentThread().toString());
                    Object objArg = threadMap.get(threadKey);

                    if (objArg == null) {
                        throw new RuntimeException("Dependency not found for key: ".concat(threadKey));
                    }

                    if (!(objArg instanceof DI_container.BeanData.Objects.BeanObject)) {
                        throw new RuntimeException(
                                "Expected BeanObject, but got: ".concat(objArg.getClass().getName())
                        );
                    }
                    DI_container.BeanData.Objects.BeanObject beanObjArg =
                            (DI_container.BeanData.Objects.BeanObject) objArg;
                    beanObjArg.workingId = beanObject.workingId;

                    try {
                        String setterName = "set".concat(capitalize(beanObjArg.beanClass.name));
                        java.lang.reflect.Method method = this.getClass().getMethod(
                                setterName, new Class[] {beanObjArg.beanClass.type}
                        );

                        if (!beanObjArg.beanClass.type.isInstance(beanObjArg.object)) {
                            throw new IllegalArgumentException();
                        }

                        method.invoke(this, new Object[] {beanObjArg.object});
                    } catch (NoSuchMethodException e) {
                        java.lang.reflect.Field field = this.getClass().getField(beanObject.beanClass.name);
                        field.set(this, beanObjArg.object);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }*/
        }

        private String capitalize(String name) {
            if (name == null || name.isEmpty()) return name;
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public void init(java.util.List construction_args_to_generate_setters) {
            try {
                java.util.Iterator it = construction_args_to_generate_setters.iterator();
                while (it.hasNext()) {
                    ArgToCreateObjectDto arg = (ArgToCreateObjectDto) it.next();
                    java.lang.reflect.Field field = this.getClass().getField(arg.name);
                    field.set(this, arg.obj);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
