package DI_container.Config;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class BeanConfig {

    @XmlAttribute(name = "id")
    private String id;

    @XmlAttribute(name = "class")
    private String className;

    @XmlAttribute(name = "scope")
    private Scope scope = Scope.SINGLETON;

    @XmlElementWrapper(name = "constructor-args")
    @XmlElement(name = "arg")
    private List<ArgumentConfig> constructorArgs = new ArrayList<>();

    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    private List<PropertyConfig> properties = new ArrayList<>();

    public BeanConfig() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Scope getScope() { return scope; }
    public void setScope(Scope scope) { this.scope = scope; }

    public List<ArgumentConfig> getConstructorArgs() { return constructorArgs; }
    public void setConstructorArgs(List<ArgumentConfig> constructorArgs) { this.constructorArgs = constructorArgs; }

    public List<PropertyConfig> getProperties() { return properties; }
    public void setProperties(List<PropertyConfig> properties) { this.properties = properties; }
}