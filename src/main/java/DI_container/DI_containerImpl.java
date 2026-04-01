package DI_container;

import DI_container.BeanData.BeansController;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.BeanScope.ScopeFactoty;
import DI_container.Config.BeanInfoMapper;
import DI_container.Config.DiConfigParser;
import DI_container.Provider.Provider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

    public DI_containerImpl(String configFilepath, ScopeFactoty scopeFactoty) {
        DiConfigParser parser = new DiConfigParser();
        var cfg = parser.parse(new File(configFilepath));
        this.beansController = new BeansController(
                BeanInfoMapper.toBeanInfoMap(cfg),
                scopeFactoty,
                new Metadata()
        );
    }

    @Override
    public <T> T get(String name, Class<T> tClass) {
        return beansController.getObject(name, tClass);
    }

    @Override
    public int getObjectsCnt(String beanName) {
        return beansController.getCntCreated(beanName);
    }

    @Override
    public <T> T getByInterface(Class<T> tInterface) {
        var name = beansController.getNameByInterface(tInterface.getName());
        return beansController.getObject(name, tInterface);
    }

    @Override
    public <T> Provider<T> getProvider(String beanName, Class<T> tClass) {
        return beansController.getProvider(beanName, tClass);
    }
}
