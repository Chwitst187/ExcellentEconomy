package su.nightexpress.coinsengine.util.placeholder;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;
import su.nightexpress.coinsengine.currency.CurrencyRegistry;
import su.nightexpress.nightcore.bridge.placeholder.PlaceholderRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PlaceholderRegistryCompat {

    private static final String[] MAPPED_METHODS = {"registerMapped", "register"};
    private static final String[] RAW_METHODS = {"registerRaw", "register"};

    public static void registerCurrencyMapped(
        @NonNull PlaceholderRegistry registry,
        @NonNull CurrencyRegistry currencyRegistry,
        @NonNull String key,
        @NonNull CurrencyPlaceholder parser
    ) {
        for (String methodName : MAPPED_METHODS) {
            for (Method method : registry.getClass().getMethods()) {
                Class<?>[] types = method.getParameterTypes();
                if (!method.getName().equals(methodName) || types.length != 3) continue;
                if (types[0] != String.class || types[1] != Class.class || !types[2].isInterface()) continue;

                Object mapper = Proxy.newProxyInstance(types[2].getClassLoader(), new Class[]{types[2]}, createMappedHandler(currencyRegistry, parser));
                if (invoke(registry, method, key, ExcellentCurrency.class, mapper)) return;
            }
        }
    }

    public static void registerRaw(@NonNull PlaceholderRegistry registry, @NonNull String key, @NonNull RawPlaceholder parser) {
        for (String methodName : RAW_METHODS) {
            for (Method method : registry.getClass().getMethods()) {
                Class<?>[] types = method.getParameterTypes();
                if (!method.getName().equals(methodName) || types.length != 2) continue;
                if (types[0] != String.class || !types[1].isInterface()) continue;

                Object mapper = Proxy.newProxyInstance(types[1].getClassLoader(), new Class[]{types[1]}, createRawHandler(parser));
                if (invoke(registry, method, key, mapper)) return;
            }
        }
    }

    private static InvocationHandler createMappedHandler(@NonNull CurrencyRegistry currencyRegistry, @NonNull CurrencyPlaceholder parser) {
        return (proxy, method, args) -> {
            if (isObjectMethod(method)) return method.invoke(proxy, args);
            if (args == null || args.length < 2) return null;

            Player player = args[0] instanceof Player online ? online : null;
            ExcellentCurrency currency = getCurrency(currencyRegistry, args[1]);
            if (player == null || currency == null) return null;

            return parser.parse(player, currency);
        };
    }

    private static InvocationHandler createRawHandler(@NonNull RawPlaceholder parser) {
        return (proxy, method, args) -> {
            if (isObjectMethod(method)) return method.invoke(proxy, args);

            Player player = null;
            String payload = "";

            if (args != null) {
                if (args.length >= 1 && args[0] instanceof Player online) {
                    player = online;
                }
                if (args.length >= 2 && args[1] != null) {
                    payload = String.valueOf(args[1]);
                }
            }

            return parser.parse(player, payload);
        };
    }

    private static boolean invoke(@NonNull PlaceholderRegistry registry, @NonNull Method method, Object... args) {
        try {
            method.invoke(registry, args);
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    @Nullable
    private static ExcellentCurrency getCurrency(@NonNull CurrencyRegistry currencyRegistry, @Nullable Object object) {
        if (object instanceof ExcellentCurrency currency) {
            return currency;
        }
        if (object == null) return null;

        return currencyRegistry.getById(String.valueOf(object));
    }

    private static boolean isObjectMethod(@NonNull Method method) {
        return method.getDeclaringClass() == Object.class;
    }

    @FunctionalInterface
    public interface CurrencyPlaceholder {

        @Nullable String parse(@NonNull Player player, @NonNull ExcellentCurrency currency);
    }

    @FunctionalInterface
    public interface RawPlaceholder {

        @Nullable String parse(@Nullable Player player, @NonNull String payload);
    }
}
