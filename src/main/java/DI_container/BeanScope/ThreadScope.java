package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadScope extends Scope {
    Map<String, Object> objects = new HashMap<>();

    @Override
    public Object getInstance() {
        return this.objects.get(java.lang.Thread.currentThread().toString());
    }

    @Override
    protected Object getOrCreateInstance(Class<?> tClass, List<ArgToCreateObjectDto> construction_args, List<ArgToCreateObjectDto> construction_args_to_generate_setters, List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws NotFoundException, CannotCompileException {
        if (this.objects.containsKey(Thread.currentThread().toString())) {
         return objects.get(Thread.currentThread().toString());
        }

        Object obj = this.getProxy(tClass, construction_args, construction_args_to_generate_setters, args_setters, metadata);
        this.objects.put(Thread.currentThread().toString(), obj);
        return obj;
    }

    @Override
    public boolean isNeededInCreation() {
        return this.objects.containsKey(Thread.currentThread().toString());
    }

    @Override
    public void deleteObject(Object obj) {
        this.objects.clear();
    }
}
