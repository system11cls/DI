package DI_container.BeanData;

public class ArgToCreateObjectDto {
    public Class<?> type;
    public String name;
    public Object obj;
    public boolean isLazy = false;


    public ArgToCreateObjectDto() {
    }

    public ArgToCreateObjectDto(Class<?> type, String name, Object obj) {
        this.type = type;
        this.name = name;
        this.obj = obj;
    }
}
