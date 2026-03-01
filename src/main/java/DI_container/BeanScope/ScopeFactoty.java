package DI_container.BeanScope;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScopeFactoty {
    private final Map<String, String> scopes = new HashMap<>();
    private final Set<String> threadDependedScopes = new HashSet<>();
    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();


    public <T> Scope<T> getScope(String scopeName) {
        if (!scopes.containsKey(scopeName)) {
            throw new RuntimeException("No scope with this name");
        }

        Class<?> scopeClass;
        try {
            scopeClass = classLoader.loadClass(scopes.get(scopeName));
            return createScope(scopeName, scopeClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage() + " (Exception in scope getting)");
        }

    }

    public void addScope(String scopeName, String scopePath, boolean isThreadDepended) {
        this.scopes.put(scopeName, scopePath);
        if (isThreadDepended) this.threadDependedScopes.add(scopeName);
    }


    private <T> Scope<T> createScope(String scopeName, Class<?> scopeClass) {
        try {
            var isDep = this.threadDependedScopes.contains(scopeName);
            Object obj = scopeClass.getConstructor().newInstance();
            var scope = (Scope<T>) obj;
            scope.setThreadDepended(isDep);
            return scope;

        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
        }
    }
}
