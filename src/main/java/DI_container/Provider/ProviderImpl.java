package DI_container.Provider;

import DI_container.BeanData.BeansController;
import DI_container.BeanData.Classes.BeanClass;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.Scope;
import DI_container.Exceptions.ProviderException;

import java.util.Objects;

public class ProviderImpl<T> implements Provider<T> {

    private final String beanName;
    private final Class<T> beanType;
    private final BeanClass<T> beanClass;
    private final Scope scope;
    private final Metadata metadata;
    private final BeansController beansController;

    private volatile boolean initialized = false;
    private final Object lock = new Object();

    public ProviderImpl(String beanName,
                        Class<T> beanType,
                        BeanClass<T> beanClass,
                        Scope scope,
                        Metadata metadata,
                        BeansController beansController) {
        this.beanName = Objects.requireNonNull(beanName, "beanName cannot be null");
        this.beanType = Objects.requireNonNull(beanType, "beanType cannot be null");
        this.beanClass = Objects.requireNonNull(beanClass, "beanClass cannot be null");
        this.scope = Objects.requireNonNull(scope, "scope cannot be null");
        this.metadata = Objects.requireNonNull(metadata, "metadata cannot be null");
        this.beansController = Objects.requireNonNull(beansController, "beansController cannot be null");
    }

    @Override
    public T get() {
        if (scope.isNeededInCreation()) {
            synchronized (lock) {
                if (!initialized) {
                    initialized = true;
                    return createInstance();
                }
            }
            return (T) getScope().getInstance();
        }
        return createInstance();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public Class<T> getType() {
        return beanType;
    }

    private T createInstance() {
        try {
            // через BeansController
            T instance = beansController.getObject(beanName, beanType);

            if (metadata != null && instance != null) {
                String id = metadata.objectToId.get(instance);
                if (id == null) {
                    id = DI_container.Tools.IdGen.generate();
                    metadata.objectToId.put(instance, id);
                }
            }

            return instance;
        } catch (Exception e) {
            throw new ProviderException(
                    "Failed to create bean: " + beanName +
                            " of type: " + beanType.getName() + ". "
                            + e.getMessage()
            );
        }
    }


    public void reset() {
        synchronized (lock) {
            initialized = false;
        }
    }

    public String getBeanName() {
        return beanName;
    }


    public Scope getScope() {
        return scope;
    }
}