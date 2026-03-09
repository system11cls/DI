package DI_container.BeanData.Metadata;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Objects.BeanObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
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
            BeanObject<?> beanObject = metadata.IdThreadToBeanObject.get(id + Thread.currentThread());
            for (var arg : beanObject.dependecies.keySet()) {
                var beanObjArg = beanObject.dependecies.get(arg).get(id + Thread.currentThread());
                beanObjArg.workingId = beanObject.workingId;
                String setterName = "set" + capitalize(beanObjArg.beanClass.name);
                Method method = null;
                try {
                    method = this.getClass().getMethod(setterName, beanObjArg.beanClass.type);
                    method.invoke(this, beanObjArg.object);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private String capitalize(String name) {
            if (name == null || name.isEmpty()) return name;
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public void init(List<ArgToCreateObjectDto> construction_args_to_generate_setters) {
            try {
                for (var arg : construction_args_to_generate_setters) {
                    var field = this.getClass().getField(arg.name);
                    field.set(this, arg.obj);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
