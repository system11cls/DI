package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanData.Objects.BeanObject;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;
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
                                    List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                                    List<ArgToCreateObjectDto> args_setters, Metadata metadata) {
        Object obj = null;
        try {
            obj = getOrCreateInstance(tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);

        } catch (NotFoundException | CannotCompileException e) {
            throw new RuntimeException(e);
        }

        return obj;
    }

    public final void setSetters(Class<?> tClass, Object obj, List<ArgToCreateObjectDto> setters_args) {

    }

    protected abstract Object getOrCreateInstance(Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                                                  List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                                                  List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws NotFoundException, CannotCompileException;

    public abstract boolean isNeededInCreation();

    public abstract void deleteObject(Object obj);

    protected Object getProxy(Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                              List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                              List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws NotFoundException, CannotCompileException {
        try {
            return generator.getProxy(tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
