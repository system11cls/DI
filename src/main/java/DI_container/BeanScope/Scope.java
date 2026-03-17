package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.Exceptions.DiProxiesGenException;
import DI_container.Exceptions.ScopeException;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class Scope {
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

    public final Object getInstance(String name, Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                                    List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                                    List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws ScopeException {
        Object obj;
        try {
            obj = getOrCreateInstance(name, tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);
        } catch (ScopeException e) {
            throw new ScopeException(e.getMessage());
        }

        return obj;
    }

    protected abstract Object getOrCreateInstance(String name, Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                                                  List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                                                  List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws ScopeException;

    public abstract boolean isNeededInCreation();

    public abstract void deleteObject(Object obj);

    protected Object getProxy(String name, Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                              List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                              List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws ScopeException {
        try {
            return generator.getProxy(name, tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);
        } catch (DiProxiesGenException e) {
            throw new ScopeException(e.getMessage());
        }
    }
}
