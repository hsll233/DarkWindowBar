package toni.darkwindowbar.fabric;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import toni.darkwindowbar.DarkWindowBar;
public final class DarkWindowBarClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        try {
            Path config = FabricLoader.getInstance().getConfigDir().resolve("darkwindowbar.properties");
            Properties settings = new Properties();
            if (Files.exists(config)) {
                try (var input = Files.newInputStream(config)) { settings.load(input); }
            } else {
                Files.createDirectories(config.getParent());
                Files.writeString(config, "# Local 26.2 port. Restart after changing.\nenabled=true\n");
            }
            if (Boolean.parseBoolean(settings.getProperty("enabled", "true"))) registerStartupListener();
        } catch (Throwable error) {
            System.getLogger(DarkWindowBar.MODNAME).log(System.Logger.Level.WARNING, "Could not initialize dark title bar; continuing normally.", error);
        }
    }
    public static void registerStartupListener() throws ReflectiveOperationException {
        Class<?> lifecycle = Class.forName("net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents");
        Class<?> listener = Class.forName(lifecycle.getName() + "$ClientStarted");
        Object callback = Proxy.newProxyInstance(listener.getClassLoader(), new Class<?>[]{listener}, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "toString" -> "DarkWindowBar.ClientStarted";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> null;
                };
            }
            if (method.getName().equals("onClientStarted")) DarkWindowBar.setDarkWindowBar(args[0]);
            return null;
        });
        Object event = lifecycle.getField("CLIENT_STARTED").get(null);
        Class.forName("net.fabricmc.fabric.api.event.Event").getMethod("register", Object.class).invoke(event, callback);
        System.getLogger(DarkWindowBar.MODNAME).log(System.Logger.Level.INFO, "Registered black title bar for Minecraft 26.2.");
    }
}
