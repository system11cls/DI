package DI_container.BeanData;

import DI_container.Tools.Pair;

import java.util.List;

public class BeanInfo {
    public String name;
    public String classPath;
    public String scope;
    public List<Pair<String, Object>> constructor_args;
    public List<Pair<String, Object>> setters_args; // if Class is not Define set Object null
    public List<String> injected_classes;
    public List<String> interfacesImplemented;
}
