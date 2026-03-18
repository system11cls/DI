package DI_container.Provider;

import DI_container.BeanData.BeanInfo;
import DI_container.BeanData.BeansController;
import DI_container.BeanData.Classes.BeanClass;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Exceptions.ProviderException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ProviderFactory {

    private final Map<String, Provider<?>> providersCache = new ConcurrentHashMap<>();
    private final BeansController beansController;
    private final Metadata metadata;
    private final ScopeFactoty scopeFactoty;

    public ProviderFactory(BeansController beansController,
                           Metadata metadata,
                           ScopeFactoty scopeFactoty) {
        this.beansController = beansController;
        this.metadata = metadata;
        this.scopeFactoty = scopeFactoty;
    }

    @SuppressWarnings("unchecked")
    public <T> Provider<T> getProvider(String beanName, Class<T> beanType) {
        String cacheKey = beanName + ":" + beanType.getName();

        return (Provider<T>) providersCache.computeIfAbsent(cacheKey, key -> {
            try {
                BeanClass<T> beanClass =
                        (BeanClass<T>) beansController.getBeanClassesController().getBeanClassByName(beanName);

                return new ProviderImpl<>(
                        beanName,
                        beanType,
                        beanClass,
                        beanClass.scope,
                        metadata,
                        beansController
                );
            } catch (Exception e) {
                throw new ProviderException(
                        "Failed to create provider for: " + beanName + ". "
                        + e.getMessage()
                );
            }
        });
    }


    public void clearCache() {
        providersCache.clear();
    }
}