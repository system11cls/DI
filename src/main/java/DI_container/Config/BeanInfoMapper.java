package DI_container.Config;

import DI_container.BeanData.ArgInfo;
import DI_container.BeanData.ArgToCreateObjectDto;
import DI_container.BeanData.BeanInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class BeanInfoMapper {

    private static final Map<Class<?>, String> PRIMITIVE_KEY = new java.util.HashMap<>();

    static {
        PRIMITIVE_KEY.put(boolean.class, "boolean");
        PRIMITIVE_KEY.put(byte.class, "byte");
        PRIMITIVE_KEY.put(char.class, "char");
        PRIMITIVE_KEY.put(short.class, "short");
        PRIMITIVE_KEY.put(int.class, "int");
        PRIMITIVE_KEY.put(long.class, "long");
        PRIMITIVE_KEY.put(float.class, "float");
        PRIMITIVE_KEY.put(double.class, "double");
        PRIMITIVE_KEY.put(void.class, "void");
        PRIMITIVE_KEY.put(String.class, "string");
        PRIMITIVE_KEY.put(Boolean.class, "boolean");
        PRIMITIVE_KEY.put(Byte.class, "byte");
        PRIMITIVE_KEY.put(Character.class, "char");
        PRIMITIVE_KEY.put(Short.class, "short");
        PRIMITIVE_KEY.put(Integer.class, "int");
        PRIMITIVE_KEY.put(Long.class, "long");
        PRIMITIVE_KEY.put(Float.class, "float");
        PRIMITIVE_KEY.put(Double.class, "double");
    }

    private BeanInfoMapper() {}

    public static Map<String, BeanInfo> toBeanInfoMap(ContainerConfig config) {
        Map<String, BeanConfig> byId = indexById(config);

        Map<String, BeanInfo> out = new LinkedHashMap<>();
        for (BeanConfig b : config.getBeans()) {
            out.put(b.getId(), toBeanInfo(b, byId));
        }
        return out;
    }

    public static Map<String, BeanConfig> indexById(ContainerConfig config) {
        Map<String, BeanConfig> byId = new LinkedHashMap<>();
        for (BeanConfig b : config.getBeans()) {
            byId.put(b.getId(), b);
        }
        return byId;
    }

    public static BeanInfo toBeanInfo(BeanConfig beanConfig, Map<String, BeanConfig> byId) {
        try {
            BeanInfo info = new BeanInfo();
            info.name = beanConfig.getId();
            info.classPath = beanConfig.getClassName();
            info.scope = beanConfig.getScope().name().toLowerCase();
            info.constructor_args = buildArgInfos(beanConfig, byId);
            info.setters_args = List.of();
            info.interfacesImplemented = List.of();
            info.injected_classes = collectInjectedCanonicalNames(beanConfig, byId);
            return info;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ArgToCreateObjectDto> toProxyConstructionArgs(
            BeanConfig beanConfig,
            Class<?> beanClass,
            Map<String, BeanConfig> byId,
            Map<String, Object> resolvedBeanIdToInstance) {
        try {
            List<ArgumentConfig> args = beanConfig.getConstructorArgs();
            Constructor<?> ctor = resolveConstructor(beanClass, args, byId);
            List<ArgToCreateObjectDto> res = new ArrayList<>();
            Parameter[] params = ctor.getParameters();
            for (int i = 0; i < args.size(); i++) {
                ArgumentConfig ac = args.get(i);
                Class<?> paramType = params[i].getType();
                String fieldName = resolveFieldName(beanClass, paramType, i, params[i]);
                if (ac.isReference()) {
                    Object ref = resolvedBeanIdToInstance.get(ac.getRef());
                    if (ref == null) {
                        throw new IllegalStateException(
                                "Unresolved ref '" + ac.getRef() + "' for bean '" + beanConfig.getId() + "'");
                    }
                    res.add(new ArgToCreateObjectDto(paramType, fieldName, ref));
                } else {
                    Object value = parseValue(paramType, ac.getValue());
                    res.add(new ArgToCreateObjectDto(paramType, fieldName, value));
                }
            }
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> collectInjectedCanonicalNames(BeanConfig beanConfig, Map<String, BeanConfig> byId) {
        List<String> names = new ArrayList<>();
        for (ArgumentConfig ac : beanConfig.getConstructorArgs()) {
            if (ac.isReference()) {
                BeanConfig ref = byId.get(ac.getRef());
                if (ref != null) {
                    names.add(ref.getClassName());
                }
            }
        }
        return names;
    }

    private static List<ArgInfo> buildArgInfos(BeanConfig beanConfig, Map<String, BeanConfig> byId)
            throws ClassNotFoundException {
        Class<?> clazz = Class.forName(beanConfig.getClassName());
        List<ArgumentConfig> args = beanConfig.getConstructorArgs();
        Constructor<?> ctor = resolveConstructor(clazz, args, byId);
        List<ArgInfo> res = new ArrayList<>();
        Parameter[] params = ctor.getParameters();
        for (int i = 0; i < args.size(); i++) {
            ArgumentConfig ac = args.get(i);
            Class<?> paramType = params[i].getType();
            String fieldName = resolveFieldName(clazz, paramType, i, params[i]);
            if (ac.isReference()) {
                if (ac.isLazy()) {
                    res.add(new ArgInfo(ac.getRef(), null, fieldName, true));
                } else {
                    res.add(new ArgInfo(fieldName, ac.getRef(), null));
                }
            } else {
                Object value = parseValue(paramType, ac.getValue());
                String key = primitiveKey(paramType);
                res.add(new ArgInfo(fieldName, key, value));
            }
        }
        return res;
    }

    private static String primitiveKey(Class<?> c) {
        String k = PRIMITIVE_KEY.get(c);
        if (k != null) {
            return k;
        }
        throw new IllegalArgumentException("Unsupported primitive/wrapper for DI mapping: " + c.getName());
    }

    private static Constructor<?> resolveConstructor(
            Class<?> clazz, List<ArgumentConfig> args, Map<String, BeanConfig> byId)
            throws ClassNotFoundException {
        if (args.isEmpty()) {
            try {
                return clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("No default constructor for " + clazz.getName(), e);
            }
        }

        List<Class<?>> expected = new ArrayList<>();
        for (ArgumentConfig ac : args) {
            if (ac.isReference()) {
                BeanConfig ref = byId.get(ac.getRef());
                if (ref == null) {
                    throw new IllegalArgumentException("Unknown bean ref: " + ac.getRef());
                }
                expected.add(Class.forName(ref.getClassName()));
            } else {
                Class<?> t = Class.forName(ac.getType());
                expected.add(t);
            }
        }

        for (Constructor<?> c : clazz.getConstructors()) {
            if (c.getParameterCount() != expected.size()) {
                continue;
            }
            boolean ok = true;
            Class<?>[] pts = c.getParameterTypes();
            for (int i = 0; i < expected.size(); i++) {
                if (!isCompatibleParameter(pts[i], expected.get(i))) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return c;
            }
        }
        throw new IllegalArgumentException("No matching constructor for " + clazz.getName() + " with given XML args");
    }

    private static boolean isCompatibleParameter(Class<?> param, Class<?> fromXml) {
        if (param.isAssignableFrom(fromXml)) {
            return true;
        }
        if (param.isPrimitive() || fromXml.isPrimitive()) {
            return wrap(param).isAssignableFrom(wrap(fromXml));
        }
        return false;
    }

    private static Class<?> wrap(Class<?> c) {
        if (!c.isPrimitive()) {
            return c;
        }
        if (c == int.class) {
            return Integer.class;
        }
        if (c == boolean.class) {
            return Boolean.class;
        }
        if (c == byte.class) {
            return Byte.class;
        }
        if (c == char.class) {
            return Character.class;
        }
        if (c == short.class) {
            return Short.class;
        }
        if (c == long.class) {
            return Long.class;
        }
        if (c == float.class) {
            return Float.class;
        }
        if (c == double.class) {
            return Double.class;
        }
        return c;
    }

    private static String resolveFieldName(Class<?> beanClass, Class<?> paramType, int index, Parameter parameter) {
        if (parameter.isNamePresent() && !parameter.getName().startsWith("arg")) {
            return parameter.getName();
        }
        List<Field> match = new ArrayList<>();
        for (Field f : beanClass.getFields()) {
            if (!Modifier.isPublic(f.getModifiers())) {
                continue;
            }
            if (f.getType().equals(paramType) || wrap(f.getType()).equals(wrap(paramType))) {
                match.add(f);
            }
        }
        if (match.size() == 1) {
            return match.get(0).getName();
        }
        if (match.size() > 1 && index < match.size()) {
            return match.get(index).getName();
        }
        throw new IllegalArgumentException(
                "Cannot resolve field name for parameter " + index + " of " + beanClass.getName());
    }

    private static Object parseValue(Class<?> targetType, String raw) {
        Class<?> t = wrap(targetType);
        if (t == String.class) {
            return raw;
        }
        if (t == Integer.class) {
            return Integer.parseInt(raw);
        }
        if (t == Long.class) {
            return Long.parseLong(raw);
        }
        if (t == Boolean.class) {
            return Boolean.parseBoolean(raw);
        }
        if (t == Double.class) {
            return Double.parseDouble(raw);
        }
        if (t == Float.class) {
            return Float.parseFloat(raw);
        }
        throw new IllegalArgumentException("Unsupported value type for XML literal: " + targetType.getName());
    }
}
