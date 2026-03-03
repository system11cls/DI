package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;

import java.util.List;

public class Singular extends Scope {
    Object obj = null;


    @Override
    protected Object getOrCreateInstance(Class<?> tClass, List<ArgToCreateObjectDto> construction_args, List<ArgToCreateObjectDto> setters_args) {
        if (obj == null) {
            this.obj = this.getProxy(tClass, construction_args, setters_args);
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
