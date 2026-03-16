package DI_container.BeanData;

public class ArgInfo {
    public String classPath;
    public Object obj; // if Class is not Define set Object null
    public String name;
    public boolean isLazy = false;

    public ArgInfo() {
    }

    public ArgInfo(String name, String classPath, Object obj) {
        this.name = name;
        this.obj = obj;
        this.classPath = classPath;
    }
}
