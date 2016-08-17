package unitil;

/**
 * Created by xu on 2016/8/17.
 */
import android.database.Cursor;
import android.os.ParcelFileDescriptor;
import java.io.Closeable;

public class IOUtils {
    private static final String TAG = "IOUtils";

    public IOUtils() {
    }

    public static void closeSilently(Closeable c) {
        try {
            if(c != null) {
                c.close();
            }
        } catch (Throwable var2) {
            Logger.w("IOUtils", "closeSilently. fail to close.", var2);
        }

    }

    public static void closeSilently(ParcelFileDescriptor p) {
        try {
            if(p != null) {
                p.close();
            }
        } catch (Throwable var2) {
            Logger.w("IOUtils", "closeSilently. fail to close.", var2);
        }

    }

    public static void closeSilently(Cursor c) {
        try {
            if(c != null) {
                c.close();
            }
        } catch (Throwable var2) {
            Logger.w("IOUtils", "closeSilently. fail to close.", var2);
        }

    }
}
