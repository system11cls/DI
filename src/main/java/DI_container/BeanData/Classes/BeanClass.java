package DI_container.BeanData.Classes;

import DI_container.BeanData.BeanInfo;
import DI_container.BeanData.Objects.BeanObject;
import DI_container.BeanScope.Scope;
import DI_container.BeanScope.ScopeFactoty;

import java.util.List;
import java.util.Set;

public class BeanClass<T> extends BeanClassA<T> {
    public Scope<T> scope;
    public String name;
    public List<BeanClassA<?>> construction_args;
    public List<BeanClassA<?>> setters_args;
    public Set<BeanClass<?>> injectedClasses;

    BeanClass(Class<T> tClass) {
        type = tClass;
    }
}
