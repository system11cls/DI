package DI_container.BeanScope;

import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.Metadata.Metadata;
import javassist.*;
import javassist.expr.ExprEditor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DiProxiesGen {
    ClassPool classPool = ClassPool.getDefault();

    public <T> T getProxy(Class<T> tClass, List<ArgToCreateObjectDto> construction_args,
                          List<ArgToCreateObjectDto> construction_args_to_generate_setters,
                          List<ArgToCreateObjectDto> args_setters, Metadata metadata) throws NotFoundException, CannotCompileException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        CtClass ctClass = classPool.getCtClass(tClass.getPackageName());
        CtClass[] params  = new CtClass[construction_args.size() + 1];
        int it = 0;

        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        List<Class<?>> constrTypes = new ArrayList<>();
        List<Object> constrVars = new ArrayList<>();
        for (var arg : construction_args) {
            if (!arg.isLazy) {
                params[it++] = classPool.get(arg.type.getPackageName());
                builder.append("this.").append(arg.name).append(" = ").append("$").append(it).append(";\n");
                constrTypes.add(arg.type);
                constrVars.add(arg.obj);
            }
        }
        var metaClass =  classPool.get(Metadata.class.getPackageName());
        constrTypes.add(Metadata.class);
        constrVars.add(metadata);

        params[it++] = metaClass;
        builder.append("this.metadata = $").append(it).append(";\n}");

        CtField metaField = new CtField( metaClass, "metadata", ctClass);
        ctClass.addField(metaField);

        CtConstructor newConstructor = CtNewConstructor.make(params, new CtClass[] {},
                builder.toString(), ctClass);

        for (var mm : ctClass.getDeclaredMethods()) {
            if (mm.getName().startsWith("set")) continue;

            mm.insertBefore("setAllSetters();");
        }

        CtMethod init = CtNewMethod.make(
                "        public void init(List<ArgToCreateObjectDto> construction_args_to_generate_setters) {\n" +
                "            try {\n" +
                "                for (var arg : construction_args_to_generate_setters) {\n" +
                "                    var field = this.getClass().getField(arg.name);\n" +
                "                    field.set(this, arg.obj);\n" +
                "                }\n" +
                "            } catch (NoSuchFieldException | IllegalAccessException e) {\n" +
                "                throw new RuntimeException(e);\n" +
                "            }\n" +
                "        }", ctClass);

        CtMethod setSetters = CtNewMethod.make(
                "        public void setAllSetters() {\n" +
                        "            String id = metadata.objectToId.get(this);\n" +
                        "            BeanObject<?> beanObject = metadata.IdThreadToBeanObject.get(id + Thread.currentThread());\n" +
                        "            for (var arg : beanObject.dependecies.keySet()) {\n" +
                        "                var beanObjArg = beanObject.dependecies.get(arg).get(id + Thread.currentThread());\n" +
                        "                beanObjArg.workingId = beanObject.workingId;\n" +
                        "                String setterName = \"set\" + capitalize(beanObjArg.beanClass.name);\n" +
                        "                Method method = null;\n" +
                        "                try {\n" +
                        "                    method = this.getClass().getMethod(setterName, beanObjArg.beanClass.type);\n" +
                        "                    method.invoke(this, beanObjArg.object);\n" +
                        "                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {\n" +
                        "                    throw new RuntimeException(e);\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }",
                ctClass
        );

        CtMethod cap = CtNewMethod.make(
                "        private String capitalize(String name) {\n" +
                        "            if (name == null || name.isEmpty()) return name;\n" +
                        "            return name.substring(0, 1).toUpperCase() + name.substring(1);\n" +
                        "        }",
                ctClass
        );

        Class<?> newClass = ctClass.toClass();
        var constr = newClass.getConstructor(constrTypes.toArray(new Class<?>[0]));
        return tClass.cast(constr.newInstance(constrVars.toArray()));
    }
}

