package DI_container.BeanData;

public class ArgInfo {
    public String nameOfBean;
    public Object obj; // if Class is not Define set Object null
    public String name;
    public boolean isLazy = false;

    public ArgInfo() {
    }

    public ArgInfo(String name, String nameOfBean, Object obj) {
        this.name = name;
        this.obj = obj;
        this.nameOfBean = nameOfBean;
    }

    public ArgInfo(String nameOfBean, Object obj, String name, boolean isLazy) {
        this.nameOfBean = nameOfBean;
        this.obj = obj;
        this.name = name;
        this.isLazy = isLazy;
    }
}
