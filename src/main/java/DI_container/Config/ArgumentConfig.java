package DI_container.Config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class ArgumentConfig {

    @XmlAttribute(name = "ref")
    private String ref;

    @XmlAttribute(name = "value")
    private String value;

    @XmlAttribute(name = "type")
    private String type = "java.lang.String";

    @XmlAttribute(name = "lazy")
    private boolean lazy = false;

    public ArgumentConfig() {}

    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    @XmlTransient
    public boolean isReference() {
        return ref != null && !ref.isEmpty();
    }

    @XmlTransient
    public boolean isLazy() {
        return lazy;
    }
}