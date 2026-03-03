package DI_container.BeanData.Objects;

import DI_container.BeanData.Classes.BeanClass;

import java.util.HashMap;
import java.util.Map;

public class BeanObject<T> {
    public final String id;
    public String workingId;
    public T object;
    public BeanClass<T> beanClass;
    public Map<String, Map<String, BeanObject<?>>> dependecies = new HashMap<>();

    public BeanObject(String id) {
        this.id = id;
    }

    public void setObject(Object object) {
        this.object = (T) this.beanClass.type.cast(object);
    }
}
