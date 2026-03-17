package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.Exceptions.ScopeException;
import DI_container.Exceptions.SingletonException;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.util.List;

public class Singleton extends Scope {
    Object obj = null;


    @Override
    protected Object getOrCreateInstance(String name, Class<?> tClass, List<ArgToCreateObjectDto> construction_args,
                                         List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                                         List<ArgToCreateObjectDto> args_setters, Metadata metadata) {
        if (obj == null) {
            this.obj = this.getProxy(name, tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);
        }

        return obj;
    }

    @Override
    public Object getInstance() {
        return obj;
    }

    @Override
    public boolean isNeededInCreation() {
        return obj == null;
    }

    @Override
    public void deleteObject(Object obj) {
        this.obj = null;
    }

}
