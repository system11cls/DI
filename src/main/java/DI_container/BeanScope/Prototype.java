package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.util.List;

public class Prototype extends Scope {
    @Override
    public Object getInstance() {
        throw new RuntimeException("getInstance() on Prototype");
    }

    @Override
    protected Object getOrCreateInstance(String name, Class<?> tClass, List<ArgToCreateObjectDto> construction_args, List<ArgToCreateObjectDto> construction_args_to_generate_setters, List<ArgToCreateObjectDto> args_setters, Metadata metadata) {
        return this.getProxy(name, tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);
    }

    @Override
    public boolean isNeededInCreation() {
        return true;
    }

    @Override
    public void deleteObject(Object obj) {
    }
}
