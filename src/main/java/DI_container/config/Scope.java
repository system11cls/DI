package DI_container.config;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Scope {
    @XmlEnumValue("singleton")
    SINGLETON,

    @XmlEnumValue("prototype")
    PROTOTYPE
}