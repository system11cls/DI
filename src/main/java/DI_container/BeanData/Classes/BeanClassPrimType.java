package DI_container.BeanData.Classes;


public class BeanClassPrimType<T> extends BeanClassA<T> {
    public T value;
    public String name;

    BeanClassPrimType(Class<?> tClass, T obj, String name) {
        this.type = tClass;
        this.value = obj;
        this.name = name;
    }
}
