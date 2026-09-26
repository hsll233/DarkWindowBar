package toni.darkwindowbar;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.Platform;
/** Local 26.2 adaptation of txnimc/DarkWindowBar. See LICENSE.md. */
public class DarkWindowBar {
    public static final String MODNAME = "Dark Window Bar";
    public static final String ID = "darkwindowbar";
    private static final System.Logger LOGGER = System.getLogger(MODNAME);
    public static void setDarkWindowBar(Object client) {
        if (Platform.get() != Platform.WINDOWS) return;
        try {
            Object window = client.getClass().getMethod("getWindow").invoke(client);
            long glfwWindow = (long) window.getClass().getMethod("handle").invoke(window);
            long hwndLong = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
            if (hwndLong == 0) throw new IllegalStateException("Missing Windows window handle");
            int[] result = applyDarkCaption(hwndLong);
            LOGGER.log(System.Logger.Level.INFO, "Applied dark title bar: dark={0}, background={1}, text={2}", result[0], result[1], result[2]);
        } catch (Throwable error) {
            LOGGER.log(System.Logger.Level.WARNING, "Could not apply dark title bar; continuing normally.", error);
        }
    }
    public static int[] applyDarkCaption(long hwndLong) {
        WinDef.HWND hwnd = new WinDef.HWND(Pointer.createConstant(hwndLong));
        int dark = setAttribute(hwnd, 20, 1);
        if (dark < 0) dark = setAttribute(hwnd, 19, 1);
        return new int[]{dark, setAttribute(hwnd, 35, 0x000000), setAttribute(hwnd, 36, 0xFFFFFF)};
    }
    private static int setAttribute(WinDef.HWND hwnd, int attribute, int value) {
        try (Memory mem = new Memory(4)) {
            mem.setInt(0, value);
            return DwmApi.INSTANCE.DwmSetWindowAttribute(hwnd, new WinDef.DWORD(attribute), new WinDef.LPVOID(mem), new WinDef.DWORD(4)).intValue();
        }
    }
}
