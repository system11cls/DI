package DI_container.BeanData.Classes;

import DI_container.BeanData.Objects.BeanObject;

public class BeanClassPrimType<T> extends BeanClassA<T> {
    public T value;

    BeanClassPrimType(Class<?> tClass, T obj) {
        this.type = tClass;
        this.value = obj;
    }
}
