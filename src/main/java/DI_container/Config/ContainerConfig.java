package DI_container.Config;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "container")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerConfig {

    @XmlElement(name = "bean")
    private List<BeanConfig> beans = new ArrayList<>();

    public ContainerConfig() {}

    public List<BeanConfig> getBeans() { return beans; }
    public void setBeans(List<BeanConfig> beans) { this.beans = beans; }
}