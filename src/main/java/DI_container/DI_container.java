package DI_container;

import DI_container.Provider.Provider;

public interface DI_container {

    public <T> T get(String name, Class<T> tClass);

    public int getObjectsCnt(String beanName);

    public <T> T getByInterface(Class<T> tInterface);

    public <T> Provider<T> getProvider(String beanName, Class<T> tClass);
}
