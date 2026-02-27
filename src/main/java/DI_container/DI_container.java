package DI_container;

public interface DI_container {

    public <T> T get(String name, Class<T> tClass);

}
