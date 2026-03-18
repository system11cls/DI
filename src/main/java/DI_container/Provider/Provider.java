package DI_container.Provider;

public interface Provider<T> {
    T get();

    boolean isInitialized();

    Class<T> getType();
}