package DI_container.BeanData.Classes;

import DI_container.BeanScope.Scope;


import java.util.List;
import java.util.Set;

public class BeanClass<T> extends BeanClassA<T> {
    public Scope scope;
    public String name;
    public List<BeanClassA<?>> construction_args;
    public List<BeanClassA<?>> setters_args;
    public Set<BeanClass<?>> injectedClasses;

    BeanClass(Class<?> tClass) {
        type = tClass;
    }
}
