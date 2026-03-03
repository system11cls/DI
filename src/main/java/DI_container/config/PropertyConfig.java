package DI_container.config;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class PropertyConfig {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "arg")
    private ArgumentConfig argument;

    public PropertyConfig() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ArgumentConfig getArgument() { return argument; }
    public void setArgument(ArgumentConfig argument) { this.argument = argument; }
}