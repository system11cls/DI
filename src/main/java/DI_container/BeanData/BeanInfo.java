package DI_container.BeanData;
import java.util.List;

public class BeanInfo {
    public String name;
    public String classPath;
    public String scope;
    public List<ArgInfo> constructor_args;
    public List<ArgInfo> setters_args;
    public List<String> injected_classes;
    public List<String> interfacesImplemented;
}
