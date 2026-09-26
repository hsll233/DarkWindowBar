import com.sun.jna.*;
import toni.darkwindowbar.DarkWindowBar;
import toni.darkwindowbar.fabric.DarkWindowBarClient;
public class VerifyPort {
    public static void main(String[] args) throws Exception {
        if (args[0].equals("lifecycle")) {
            DarkWindowBarClient.registerStartupListener();
            System.out.println("PASS: registration against installed Fabric lifecycle event");
            return;
        }
        long handle = Long.parseLong(args[0]);
        int[] results = DarkWindowBar.applyDarkCaption(handle);
        for (int result : results) if (result != 0) throw new AssertionError("DWM HRESULT=" + result);
        Function get = NativeLibrary.getInstance("dwmapi").getFunction("DwmGetWindowAttribute", Function.ALT_CONVENTION);
        // Caption/text colors are set-only attributes; Windows rejects querying them.
        int[] ids = {20};
        int[] expected = {1};
        for (int i = 0; i < ids.length; i++) {
            try (Memory value = new Memory(4)) {
                int hr = get.invokeInt(new Object[]{new Pointer(handle), ids[i], value, 4});
                if (hr != 0 || value.getInt(0) != expected[i]) throw new AssertionError("Attribute " + ids[i] + " hr=" + hr);
            }
        }
        System.out.println("PASS: Windows accepted black caption/white text; dark mode read back as enabled");
    }
}
