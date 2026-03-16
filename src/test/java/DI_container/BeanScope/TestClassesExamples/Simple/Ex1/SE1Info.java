package DI_container.BeanScope.TestClassesExamples.Simple.Ex1;

import DI_container.BeanData.ArgToCreateObjectDto;

import java.util.ArrayList;
import java.util.List;

public class SE1Info {

    public static List<ArgToCreateObjectDto> cargcEngine() {
        var res = new ArrayList<ArgToCreateObjectDto>();

        var typeArg = new ArgToCreateObjectDto(String.class, "type", "new");
        res.add(typeArg);

        var horsepower = new ArgToCreateObjectDto(int.class, "horsepower", 120);
        res.add(horsepower);

        return res;
    }

    public static List<ArgToCreateObjectDto> cargsCar(Object engine) {
        var res = new ArrayList<ArgToCreateObjectDto>();

        var modelArg = new ArgToCreateObjectDto(String.class, "model", "XM1");
        res.add(modelArg);

        var engineArg = new ArgToCreateObjectDto(Engine.class, "engine", engine);
        res.add(engineArg);

        return res;
    }
}
