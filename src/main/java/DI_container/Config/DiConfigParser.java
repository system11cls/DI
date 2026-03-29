package DI_container.Config;


import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.InputStream;


public class DiConfigParser {

    private final JAXBContext jaxbContext;

    public DiConfigParser() {
        try {
            this.jaxbContext = JAXBContext.newInstance(ContainerConfig.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to initialize JAXBContext", e);
        }
    }

    /**
     * Парсинг конфигурации из файла
     */
    public ContainerConfig parse(File file) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (ContainerConfig) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new RuntimeException("Error parsing DI configuration file: " + file.getPath(), e);
        }
    }

    /**
     * Парсинг конфигурации из InputStream
     */
    public ContainerConfig parse(InputStream inputStream) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (ContainerConfig) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException("Error parsing DI configuration from stream", e);
        }
    }
}