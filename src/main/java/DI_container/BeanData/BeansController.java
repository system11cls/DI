package DI_container.BeanData;

import DI_container.BeanData.Classes.BeanClass;
import DI_container.BeanData.Classes.BeanClassA;
import DI_container.BeanData.Classes.BeanClassPrimType;
import DI_container.BeanData.Classes.BeanClassesController;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanData.Objects.BeanObject;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Exceptions.BeanClassesControllerException;
import DI_container.Provider.Provider;
import DI_container.Provider.ProviderFactory;
import DI_container.Tools.IdGen;
import DI_container.Tools.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeansController {
    private final BeanClassesController beanClassesController;
    private final Metadata metadata;
    private final Map<String, List<String>> interfacesToNames = new HashMap<>();
    private final ProviderFactory providerFactory;

    public BeansController(Map<String, BeanInfo> infos, ScopeFactoty scopeFactoty, Metadata metadata) {
        this.beanClassesController = new BeanClassesController(infos, scopeFactoty);
        setInterfaces(infos);
        this.metadata = metadata;
        this.providerFactory = new ProviderFactory(this, metadata, scopeFactoty);
    }

    private void setInterfaces(Map<String, BeanInfo> infos) {
        for (var info : infos.values()) {
            for (var inter : info.interfacesImplemented) {
                if (interfacesToNames.containsKey(inter)) {
                    interfacesToNames.get(inter).add(info.name);
                }
                else {
                    var l = new ArrayList<String>();
                    l.add(info.name);
                    interfacesToNames.put(inter, l);
                }
            }
        }
    }

    public <T> T getObject(String name, Class<T> tClass) {
        var beanClass = beanClassesController.getBeanClassByName(name);
        String id;
        List<BeanObject<?>> objectsToInit = new ArrayList<>();
        if (beanClass.scope.isNeededInCreation()) {
            var newObject = createBeanObject(beanClass, objectsToInit);
            id = metadata.objectToId.get(newObject.object);
            initObjects(objectsToInit);
        }
        else {
            var obj = beanClass.scope.getInstance();
            id = metadata.objectToId.get(obj);
            updateThreadObjects(beanClass, metadata.IdThreadToBeanObject.get(id), id, objectsToInit);
        }

        return tClass.cast(metadata.IdThreadToBeanObject.get(id).object);
    }

    private void initObjects(List<BeanObject<?>> objectsToInit) {
        for (var bObj : objectsToInit) {
            var args = getInitObjects(bObj);
            try {
                Method method = bObj.object.getClass().getMethod("init", java.util.List.class);
                method.invoke(bObj.object, args);
                Method setters = bObj.object.getClass().getMethod("setAllSetters");
                setters.invoke(bObj.object);

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }



        objectsToInit.clear();
    }

    private List<ArgToCreateObjectDto> getInitObjects(BeanObject<?> beanObject) {
        List<ArgToCreateObjectDto> res = new ArrayList<>();

        for (var argA : beanObject.beanClass.construction_args_to_setters_gen) {
            var arg = (BeanClass) argA;
            var beanClass = beanClassesController.getBeanClassByName(arg.name);
            String id;
            Object argObj;
            if (beanClass.scope.isNeededInCreation()) {
                List<BeanObject<?>> objectsToInit = new ArrayList<>();
                var newObject = createBeanObject(beanClass, objectsToInit);
                id = metadata.objectToId.get(newObject.object);
                initObjects(objectsToInit);
                argObj = newObject.object;
            }
            else {
                List<BeanObject<?>> objectsToInit = new ArrayList<>();
                var obj = beanClass.scope.getInstance();
                id = metadata.objectToId.get(obj);
                updateThreadObjects(beanClass, metadata.IdThreadToBeanObject.get(id), id, objectsToInit);
                argObj = obj;
            }
            res.add(new ArgToCreateObjectDto(
                arg.type, arg.name, argObj
            ));
        }

        return res;
    }

    private <T> BeanObject<T> createBeanObject(BeanClass<T> beanClass, List<BeanObject<?>> objectsToInit) {
        String chainId = IdGen.generate();
        var newBeanObject = new BeanObject<T>(chainId);
        for (var injectedClass : beanClass.injectedClasses) {
            if (injectedClass == null) continue;
            var depend = createBeanObject(injectedClass, chainId, objectsToInit);
            newBeanObject.dependecies.put(injectedClass.name, new HashMap<>());
            dependInDependencies(newBeanObject, depend, chainId, injectedClass.name);
        }
        newBeanObject.workingId = chainId;
        newBeanObject.beanClass = beanClass;
        createObject(beanClass, newBeanObject, chainId);
        metadata.objectToId.put(newBeanObject.object, newBeanObject.id);
        metadata.IdThreadToBeanObject.put(newBeanObject.id, newBeanObject);

        objectsToInit.add(newBeanObject);
        return newBeanObject;
    }

    private <T> BeanObject<?> createBeanObject(BeanClass<T> beanClass, String chainId, List<BeanObject<?>> objectsToInit) {
        if (!beanClass.scope.isNeededInCreation()) {
            var objId = metadata.objectToId.get(beanClass.scope.getInstance());
            var beanObject = this.getObjectByCommonIdOrDefaultFromMetadata(objId);
            updateThreadObjects(beanClass, beanObject, chainId, objectsToInit);
            return beanObject;
        }

        var newBeanObject = new BeanObject<T>(IdGen.generate());
        for (var injectedClass : beanClass.injectedClasses) {
            if (injectedClass == null) continue;
            var depend = createBeanObject(injectedClass, chainId, objectsToInit);
            newBeanObject.dependecies.put(injectedClass.name, new HashMap<>());
            dependInDependencies(newBeanObject, depend, newBeanObject.id, injectedClass.name);
            dependInDependencies(newBeanObject, depend, chainId, injectedClass.name);
        }
        newBeanObject.workingId = chainId;
        newBeanObject.beanClass = beanClass;
        createObject(beanClass, newBeanObject, chainId);
        metadata.objectToId.put(newBeanObject.object, newBeanObject.id);
        metadata.IdThreadToBeanObject.put(newBeanObject.id, newBeanObject);

        objectsToInit.add(newBeanObject);
        return newBeanObject;
    }

    private void createObject(BeanClass<?> beanClass, BeanObject<?> beanObject, String id) {
        List<ArgToCreateObjectDto> constructorArgs = getListOfArgs(beanClass.construction_args,
                beanObject, id);
        List<ArgToCreateObjectDto> setters_args = getListOfArgs(beanClass.setters_args,
                beanObject, id);

        beanObject.setObject(beanClass.scope.getInstance(beanClass.name, beanClass.type, constructorArgs,
                null, setters_args, metadata));
    }

    private List<ArgToCreateObjectDto> getListOfArgs(List<BeanClassA<?>> listOfClasses,
                                                     BeanObject<?> beanObject, String id) {
        List<ArgToCreateObjectDto> argList = new ArrayList<>();
        for (var arg : listOfClasses) {
            ArgToCreateObjectDto newArg = new ArgToCreateObjectDto();
            if (arg instanceof BeanClass<?> argClass) {
                newArg.obj = beanObject.dependecies.get(argClass.name).get(id).object;
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

    private void updateThreadObjects(BeanClass<?> beanClass, BeanObject<?> obj, String id, List<BeanObject<?>> objectsToInit) {
        for (var injectedClass : beanClass.injectedClasses) {
            if (injectedClass == null) continue;
            if (injectedClass.scope.isThreadDepended() && injectedClass.scope.isNeededInCreation()) {
                obj.dependecies.get(injectedClass.name).put(id + Thread.currentThread().getName(), createBeanObject(injectedClass, id, objectsToInit));
            }
            else if (!injectedClass.scope.isThreadDepended()) {
                var nextObj = getObjectByCommonIdOrDefault(obj, id, injectedClass.name);
                updateThreadObjects(injectedClass, nextObj, id, objectsToInit);
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

    public BeanClassesController getBeanClassesController() {
        return beanClassesController;
    }

    public int getCntCreated(String beanName) {
        var beanClass = beanClassesController.getBeanClassByName(beanName);
        return beanClass.scope.getCntCreated();
    }

    public String getNameByInterface(String interfaceName) {
        if (this.interfacesToNames.containsKey(interfaceName)) {
            var interfaceList =  interfacesToNames.get(interfaceName);
            if (interfaceList.size() != 1) {
                throw new RuntimeException("Two or more classes implement interface");
            }

            return interfaceList.getFirst();
        }
        throw new BeanClassesControllerException("No class implementing interface");
    }

    public <T> Provider<T> getProvider(String beanName, Class<T> tClass) {
        return providerFactory.getProvider(beanName, tClass);
    }

}
