package DI_container;

import DI_container.BeanData.BeansController;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Config.BeanInfoMapper;
import DI_container.Config.DiConfigParser;

import java.io.File;

public class DI_containerImpl implements DI_container {

    private final BeansController beansController;

    public DI_containerImpl(String configFilePath) {
        DiConfigParser parser = new DiConfigParser();
        var cfg = parser.parse(new File(configFilePath));
        this.beansController = new BeansController(
                BeanInfoMapper.toBeanInfoMap(cfg),
                new ScopeFactoty(),
                new Metadata()
        );
    }

    @Override
    public <T> T get(String name, Class<T> tClass) {
        return beansController.getObject(name, tClass);
    }
}
