package DI_container.Tools;

import java.util.UUID;

public class IdGen {

    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
