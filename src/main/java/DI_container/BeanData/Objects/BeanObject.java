package DI_container.BeanData.Objects;

import DI_container.Tools.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BeanObject<T> {
    public final String id;
    public String workingId;
    public T object;
    public List<Pair<String, Map<String, BeanObject<?>>>> dependencies = new ArrayList<>();

    public BeanObject(String id) {
        this.id = id;
    }


}
