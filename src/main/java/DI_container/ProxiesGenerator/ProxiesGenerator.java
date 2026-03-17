package DI_container.ProxiesGenerator;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import DI_container.Exceptions.ProxiesGeneratorException;

import java.util.List;

public interface ProxiesGenerator {

    public <T> T getProxy(String name, Class<T> tClass, List<ArgToCreateObjectDto> construction_args,
                         List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                         List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws ProxiesGeneratorException;
}
