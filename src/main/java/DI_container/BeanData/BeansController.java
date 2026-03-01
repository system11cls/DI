package DI_container.BeanData;

import DI_container.BeanData.Classes.BeanClass;
import DI_container.BeanData.Classes.BeanClassesController;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanData.Objects.BeanObject;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Tools.IdGen;

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
            var newObject = createObject(beanClass);
            this.addObjectToData(newObject);
            id = metadata.objectToId.get(newObject);
        }
        else {
            T obj = tClass.cast(beanClass.scope.getInstance());
            id = metadata.objectToId.get(obj);

            if (!metadata.IdThreadToBeanObject.containsKey(id + Thread.currentThread().getName())) {
                updateThreadObjects(beanClass, metadata.IdThreadToBeanObject.get(id));
            }
        }

        return tClass.cast(metadata.IdThreadToBeanObject.get(id + Thread.currentThread().getName()).object);
    }

    private <T> T createObject(BeanClass<T> beanClass) {
        //TODO:implement
        return null;
    }

    private <T> void addObjectToData(T object) {
        String newId = IdGen.generate();
        metadata.objectToId.put(object, newId);

    }

    private void updateThreadObjects(BeanClass<?> beanClass, BeanObject<?> obj) {

    }
}
