package DI_container.BeanData;

import DI_container.BeanData.Classes.BeanClass;
import DI_container.BeanData.Classes.BeanClassA;
import DI_container.BeanData.Classes.BeanClassPrimType;
import DI_container.BeanData.Classes.BeanClassesController;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanData.Objects.BeanObject;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Tools.IdGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeansController {
    private final BeanClassesController beanClassesController;
    private final Metadata metadata;


    BeansController(Map<String, BeanInfo> infos, ScopeFactoty scopeFactoty, Metadata metadata) throws ClassNotFoundException {
        this.beanClassesController = new BeanClassesController(infos, scopeFactoty);
        this.metadata = metadata;
    }

    public <T> T getObject(String name, Class<T> tClass) {
        var beanClass = beanClassesController.getBeanClassByName(name);
        String id;
        if (beanClass.scope.isNeededInCreation()) {
            var newObject = createBeanObject(beanClass);
            id = metadata.objectToId.get(newObject);
        }
        else {
            var obj = beanClass.scope.getInstance();
            id = metadata.objectToId.get(obj);
        }

        return tClass.cast(metadata.IdThreadToBeanObject.get(id).object);
    }

    private <T> BeanObject<T> createBeanObject(BeanClass<T> beanClass) {
        String chainId = IdGen.generate();
        var newBeanObject = new BeanObject<T>(chainId);
        for (var injectedClass : beanClass.injectedClasses) {
            var depend = createBeanObject(injectedClass, chainId);
            newBeanObject.dependecies.put(injectedClass.name, new HashMap<>());
            dependInDependencies(newBeanObject, depend, chainId, injectedClass.name);
        }
        newBeanObject.workingId = chainId;
        newBeanObject.beanClass = beanClass;
        createObject(beanClass, newBeanObject, chainId);
        metadata.IdThreadToBeanObject.put(newBeanObject.id, newBeanObject);

        return newBeanObject;
    }

    private <T> BeanObject<?> createBeanObject(BeanClass<T> beanClass, String chainId) {
        if (!beanClass.scope.isNeededInCreation()) {
            var objId = metadata.objectToId.get(beanClass.scope.getInstance());
            var beanObject = this.getObjectByCommonIdOrDefaultFromMetadata(objId);
            updateThreadObjects(beanClass, beanObject, chainId);
            return beanObject;
        }

        var newBeanObject = new BeanObject<T>(IdGen.generate());
        for (var injectedClass : beanClass.injectedClasses) {
            var depend = createBeanObject(injectedClass, chainId);
            newBeanObject.dependecies.put(injectedClass.name, new HashMap<>());
            dependInDependencies(newBeanObject, depend, newBeanObject.id, injectedClass.name);
            dependInDependencies(newBeanObject, depend, chainId, injectedClass.name);
        }
        newBeanObject.workingId = chainId;
        newBeanObject.beanClass = beanClass;
        createObject(beanClass, newBeanObject, chainId);
        metadata.objectToId.put(newBeanObject.object, newBeanObject.id);
        metadata.IdThreadToBeanObject.put(newBeanObject.id, newBeanObject);

        return newBeanObject;
    }

    private void createObject(BeanClass<?> beanClass, BeanObject<?> beanObject, String id) {
        List<ArgToCreateObjectDto> constructorArgs = getListOfArgs(beanClass.construction_args,
                beanObject, id);
        List<ArgToCreateObjectDto> settersArgs = getListOfArgs(beanClass.setters_args,
                beanObject, id);

        beanObject.setObject(beanClass.scope.getInstance(beanClass.type, constructorArgs, settersArgs));
    }

    private List<ArgToCreateObjectDto> getListOfArgs(List<BeanClassA<?>> listOfClasses,
                                                     BeanObject<?> beanObject, String id) {
        List<ArgToCreateObjectDto> argList = new ArrayList<>();
        for (var arg : listOfClasses) {
            ArgToCreateObjectDto newArg = new ArgToCreateObjectDto();
            if (arg instanceof BeanClass<?> argClass) {
                newArg.obj = beanObject.dependecies.get(argClass.name).get(id);
                newArg.name = argClass.name;
                newArg.type = argClass.type;
            }
            else {
                BeanClassPrimType<?> argClass = (BeanClassPrimType<?>) arg;
                newArg.type = argClass.type;
                newArg.name = argClass.name;
                newArg.obj = argClass.value;
            }
            argList.add(newArg);
        }

        return argList;
    }


    private void dependInDependencies(BeanObject<?> parent, BeanObject<?> depend, String id, String dependName) {
        parent.dependecies.get(dependName).put(id, depend);
        parent.dependecies.get(dependName).put(id + Thread.currentThread().getName(), depend);
    }

    private void updateThreadObjects(BeanClass<?> beanClass, BeanObject<?> obj, String id) {
        for (var injectedClass : beanClass.injectedClasses) {
            if (injectedClass.scope.isThreadDepended() && injectedClass.scope.isNeededInCreation()) {
                obj.dependecies.get(injectedClass.name).put(id, createBeanObject(injectedClass, id));
            }
            else if (!injectedClass.scope.isThreadDepended()) {
                var nextObj = getObjectByCommonIdOrDefault(obj, id, injectedClass.name);
                updateThreadObjects(injectedClass, nextObj, id);
            }
        }
    }

    private BeanObject<?> getObjectByCommonIdOrDefault(BeanObject<?> object, String id, String dependName) {
        if (object.dependecies.get(dependName).containsKey(id)) {
            return object.dependecies.get(dependName).get(id);
        }

        return object.dependecies.get(dependName).get(object.id);
    }

    private BeanObject<?> getObjectByCommonIdOrDefaultFromMetadata(String id) {
        return metadata.IdThreadToBeanObject.get(id);
    }
}
