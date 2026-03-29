package DI_container.Config;

import java.io.InputStream;


public final class XmlConfigTestSupport {

    public static final String EX1 = "/DI_container/BeanScope/TestClassesExamples/Simple/Ex1/";
    public static final String EX2 = "/DI_container/BeanScope/TestClassesExamples/Simple/Ex2/";

    private XmlConfigTestSupport() {}

    public static ContainerConfig load(String classpathAbsolutePath) {
        DiConfigParser parser = new DiConfigParser();
        InputStream in = XmlConfigTestSupport.class.getResourceAsStream(classpathAbsolutePath);
        if (in == null) {
            throw new IllegalArgumentException("Classpath resource not found: " + classpathAbsolutePath);
        }
        return parser.parse(in);
    }
}
