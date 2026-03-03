package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Objects.BeanObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scope {
    Map<Object, BeanObject<?>> objectsInited = new HashMap<>();
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

    public abstract Object getInstance();

    public final Object getInstance(Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                         List<ArgToCreateObjectDto> setters_args) {
        Object obj = getOrCreateInstance(tClass, construction_args, setters_args);

        return obj;
    }

    protected abstract Object getOrCreateInstance(Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                                             List<ArgToCreateObjectDto> setters_args);

    public abstract boolean isNeededInCreation();

    public abstract void deleteObject(Object obj);

    protected Object getProxy(Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                         List<ArgToCreateObjectDto> setters_args) {
        return generator.getProxy(tClass, construction_args, setters_args);
    }
}
