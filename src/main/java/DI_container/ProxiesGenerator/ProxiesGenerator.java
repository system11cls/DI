package DI_container.ProxiesGenerator;

import DI_container.Tools.Pair;

import java.lang.reflect.Type;
import java.util.List;

public interface ProxiesGenerator {

    public <T> T generateClassProxy(Class<T> tClass, List<Pair<Type, Object>> construction_args,
                                    List<Pair<Type, Object>> setters_args);
}
