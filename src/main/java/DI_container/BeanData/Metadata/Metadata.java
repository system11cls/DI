package DI_container.BeanData.Metadata;

import DI_container.BeanData.Objects.BeanObject;

import java.util.HashMap;
import java.util.Map;

public class Metadata {
    public Map<Object, String> objectToId = new HashMap<>();
    public Map<String, BeanObject<?>> IdThreadToBeanObject = new HashMap<>();
}
