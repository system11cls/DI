package DI_container.Config;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Scope {
    @XmlEnumValue("singleton")
    SINGLETON,

    @XmlEnumValue("prototype")
    PROTOTYPE
}