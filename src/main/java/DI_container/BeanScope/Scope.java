package DI_container.BeanScope;

import DI_container.BeanData.Objects.BeanObject;
import DI_container.Tools.IdGen;
import DI_container.Tools.Pair;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scope<T> {
    Map<Object, BeanObject<T>> objectsInited = new HashMap<>();
    DiProxiesGen generator;
    private boolean isThreadDepended;

    public boolean isThreadDepended() {
        return isThreadDepended;
    }

    public void setThreadDepended(boolean threadDepended) {
        isThreadDepended = threadDepended;
    }

    Scope() {
        generator = new DiProxiesGen();
    }

    public abstract T getInstance();

    public final T getInstance(Class<T> tClass, List<Pair<Class<?>, Object>> construction_args,
                         List<Pair<Class<?>, Object>> setters_args) {
        T obj = getOrCreateInstance(tClass, construction_args, setters_args);

        return obj;
    }


    protected abstract T getOrCreateInstance(Class<T> tClass, List<Pair<Class<?>, Object>> construction_args,
                                             List<Pair<Class<?>, Object>> setters_args);

    public abstract boolean isNeededInCreation();

    public abstract void deleteObject(T obj);

    protected T getProxy(Class<T> tClass, List<Pair<Class<?>, Object>> construction_args,
                      List<Pair<Class<?>, Object>> setters_args) {
        return generator.getProxy(tClass, construction_args, setters_args);
    }
}
