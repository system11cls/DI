package DI_container.BeanScope;

import DI_container.Tools.Pair;

import java.util.List;

public class Singular<T> extends Scope<T> {
    T obj = null;

    @Override
    public T getInstance() {
        return obj;
    }

    @Override
    public T getOrCreateInstance(Class<T> tClass, List<Pair<Class<?>, Object>> construction_args,
                                 List<Pair<Class<?>, Object>> setters_args) {
        if (obj == null) {
            this.obj = this.getProxy(tClass, construction_args, setters_args);
        }

        return obj;
    }

    @Override
    public boolean isNeededInCreation() {
        return obj == null;
    }


    @Override
    public void deleteObject(T obj) {
        if (obj == this.obj) {
            this.obj = null;
        }
    }
}
