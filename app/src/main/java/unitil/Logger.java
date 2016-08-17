package unitil;

/**
 * Created by xu on 2016/8/17.
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String ROOT_TAG = "cmfsdk";
    public static final int TRACE_LEVEL = 1;
    public static final int INFO_LEVEL = 2;
    public static final int WARN_LEVEL = 4;
    public static final int ERROR_LEVEL = 8;
    public static final int FATAL_LEVEL = 16;
    private static int sLogLevel = 30;
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;

    public Logger() {
    }

    public static final void setLogLevel(int logLevel) {
        sLogLevel = logLevel;
    }

    public static final void v(String tag, String msg) {
        if((sLogLevel & 1) != 0) {
            print(2, "cmfsdk", buildLog(tag, msg, (Throwable)null));
        }

    }

    public static final void v(String tag, String msg, Throwable tr) {
        if((sLogLevel & 1) != 0) {
            print(2, "cmfsdk", buildLog(tag, msg, tr));
        }

    }

    public static final void v(String tag, String format, Object... args) {
        v(tag, (Throwable)null, format, args);
    }

    public static final void v(String tag, Throwable tr, String format, Object... args) {
        if((sLogLevel & 1) != 0) {
            print(2, "cmfsdk", buildLog(tag, formatString(format, args), tr));
        }

    }

    public static final void d(String tag, String msg) {
        if((sLogLevel & 1) != 0) {
            print(3, "cmfsdk", buildLog(tag, msg, (Throwable)null));
        }

    }

    public static final void d(String tag, String msg, Throwable tr) {
        if((sLogLevel & 1) != 0) {
            print(3, "cmfsdk", buildLog(tag, msg, tr));
        }

    }

    public static final void d(String tag, String format, Object... args) {
        d(tag, (Throwable)null, format, args);
    }

    public static final void d(String tag, Throwable tr, String format, Object... args) {
        if((sLogLevel & 1) != 0) {
            print(3, "cmfsdk", buildLog(tag, formatString(format, args), tr));
        }

    }

    public static final void i(String tag, String msg) {
        if((sLogLevel & 2) != 0) {
            print(4, "cmfsdk", buildLog(tag, msg, (Throwable)null));
        }

    }

    public static final void i(String tag, String msg, Throwable tr) {
        if((sLogLevel & 2) != 0) {
            print(4, "cmfsdk", buildLog(tag, msg, tr));
        }

    }

    public static final void i(String tag, String format, Object... args) {
        i(tag, (Throwable)null, format, args);
    }

    public static final void i(String tag, Throwable tr, String format, Object... args) {
        if((sLogLevel & 2) != 0) {
            print(4, "cmfsdk", buildLog(tag, formatString(format, args), tr));
        }

    }

    public static final void w(String tag, String msg) {
        if((sLogLevel & 4) != 0) {
            print(5, "cmfsdk", buildLog(tag, msg, (Throwable)null));
        }

    }

    public static final void w(String tag, String msg, Throwable tr) {
        if((sLogLevel & 4) != 0) {
            print(5, "cmfsdk", buildLog(tag, msg, tr));
        }

    }

    public static final void w(String tag, String format, Object... args) {
        w(tag, (Throwable)null, format, args);
    }

    public static final void w(String tag, Throwable tr, String format, Object... args) {
        if((sLogLevel & 4) != 0) {
            print(5, "cmfsdk", buildLog(tag, formatString(format, args), tr));
        }

    }

    public static final void e(String tag, String msg) {
        if((sLogLevel & 8) != 0) {
            print(6, "cmfsdk", buildLog(tag, msg, (Throwable)null));
        }

    }

    public static final void e(String tag, String msg, Throwable tr) {
        if((sLogLevel & 8) != 0) {
            print(6, "cmfsdk", buildLog(tag, msg, tr));
        }

    }

    public static final void e(String tag, String format, Object... args) {
        e(tag, (Throwable)null, format, args);
    }

    public static final void e(String tag, Throwable tr, String format, Object... args) {
        if((sLogLevel & 8) != 0) {
            print(6, "cmfsdk", buildLog(tag, formatString(format, args), tr));
        }

    }

    private static String formatString(String format, Object... args) {
        try {
            return String.format(format.replaceAll("%d", "%s").replaceAll("%f", "%s"), args);
        } catch (Exception var7) {
            e("cmfsdk", "formatString failed. " + var7.toString());
            if(args != null) {
                Object[] var6 = args;
                int var5 = args.length;

                for(int var4 = 0; var4 < var5; ++var4) {
                    Object arg = var6[var4];
                    format = format + ", " + arg;
                }
            }

            return format;
        }
    }

    private static final String buildLog(String tag, String msg, Throwable tr) {
        if(tr != null) {
            if(StringUtils.isEmpty(msg)) {
                msg = getStackTraceString(tr);
            } else {
                msg = msg + '\n' + getStackTraceString(tr);
            }
        }

        return "[" + tag + "] " + msg;
    }

    public static String getStackTraceString(Throwable tr) {
        if(tr == null) {
            return "";
        } else {
            for(Throwable t = tr; t != null; t = t.getCause()) {
                ;
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, false);
            tr.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }
    }

    private static final void print(int priority, String tag, String msg) {
        Log.println(priority, tag, msg);
        if(Logger.LogWriter.sWriteLocal) {
            Logger.LogWriter.getInstance().submitData(priority, Process.myTid(), tag, msg);
        }

    }

    public static class LogWriter extends Thread {
        private static final String TAG = "LogWriter";
        private static final int FILE_SIZE_DEFAULT = 1048576;
        private static final int MSG_WRITE_LOG = 1;
        private static final int MSG_STOP_WRITE = 2;
        public static final String MAIN_LOG_NAME = "cmf.log";
        public static final String BG_LOG_NAME = "cmfbg.log";
        private static final String KEY_BUNDLE_PRIORITY = "priority";
        private static final String KEY_BUNDLE_TID = "tid";
        private static final String KEY_BUNDLE_TIME = "time";
        private static final String KEY_BUNDLE_TAG = "tag";
        private static final String KEY_BUNDLE_MSG = "msg";
        private static Logger.LogWriter sSingleton;
        private static boolean sWriteLocal;
        private final int mFileSize;
        private final File mLogFile;
        private final Logger.LogWriter.LogHandler mHandler;
        private final StringBuilder mStringBuilder;
        private final SimpleDateFormat mSimpleDateFormat;
        private volatile boolean mQuitFlag;
        private Writer mWriter;
        private Looper mLooper;

        private LogWriter(String filePath, int fileSize) {
            if(fileSize <= 0) {
                fileSize = 1048576;
            }

            this.mFileSize = fileSize;
            this.mLogFile = new File(filePath);
            this.mStringBuilder = new StringBuilder();
            this.mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            try {
                this.mWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.mLogFile, true)), this.mFileSize);
            } catch (Exception var4) {
                Logger.e("LogWriter", "", (Throwable)var4);
            }

            this.start();
            this.mHandler = new Logger.LogWriter.LogHandler(this.getLooper());
        }

        public void run() {
            Looper.prepare();
            synchronized(this) {
                this.mLooper = Looper.myLooper();
                this.notifyAll();
            }

            Process.setThreadPriority(10);
            Looper.loop();
        }

        public static void setWriteLocal(boolean write) {
            sWriteLocal = write;
        }

        public static boolean isWriteLocal() {
            return sWriteLocal;
        }

        public static void init(String filePath, int fileSize) {
            if(sSingleton == null) {
                Class var2 = Logger.LogWriter.class;
                synchronized(Logger.LogWriter.class) {
                    if(sSingleton == null) {
                        sSingleton = new Logger.LogWriter(filePath, fileSize);
                    }
                }
            }

        }

        public static Logger.LogWriter getInstance() {
            if(sSingleton == null) {
                throw new IllegalStateException("Not initialized");
            } else {
                return sSingleton;
            }
        }

        public void close() {
            if(!this.mQuitFlag) {
                this.mQuitFlag = true;
                this.mHandler.sendEmptyMessage(2);
            }
        }

        public synchronized void submitData(int priority, int tid, String tag, String msg) {
            Bundle bundle = new Bundle();
            bundle.putInt("priority", priority);
            bundle.putInt("tid", tid);
            bundle.putString("time", this.mSimpleDateFormat.format(new Date()));
            bundle.putString("tag", tag);
            bundle.putString("msg", msg);
            Message message = this.mHandler.obtainMessage(1);
            message.setData(bundle);
            this.mHandler.sendMessage(message);
        }

        private Looper getLooper() {
            if(!this.isAlive()) {
                return null;
            } else {
                synchronized(this) {
                    while(this.isAlive() && this.mLooper == null) {
                        try {
                            this.wait();
                        } catch (InterruptedException var3) {
                            ;
                        }
                    }
                }

                return this.mLooper;
            }
        }

        private void writeLog(Bundle bundle) {
            try {
                String tid;
                if(this.mLogFile.length() >= (long)this.mFileSize) {
                    IOUtils.closeSilently(this.mWriter);
                    String[] e = this.mLogFile.getName().split("\\.");
                    tid = this.mLogFile.getParentFile().getAbsolutePath() + File.separator;
                    if(e != null && e.length == 2) {
                        tid = tid + e[0] + "-backup." + e[1];
                    } else {
                        tid = tid + this.mLogFile.getName() + ".backup";
                    }

                    FileHelper.renameFile(this.mLogFile, new File(tid));
                    this.mWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.mLogFile, true)), this.mFileSize);
                }

                this.mStringBuilder.delete(0, this.mStringBuilder.length());
                this.mStringBuilder.append(bundle.getString("time")).append(" ");
                String e1 = String.valueOf(Process.myPid());

                for(tid = String.valueOf(bundle.getInt("tid")); e1.length() < 5; e1 = " " + e1) {
                    ;
                }

                while(tid.length() < 5) {
                    tid = " " + tid;
                }

                this.mStringBuilder.append(e1).append(" ").append(tid).append(" ");
                switch(bundle.getInt("priority")) {
                    case 2:
                        this.mStringBuilder.append("V ");
                        break;
                    case 3:
                        this.mStringBuilder.append("D ");
                        break;
                    case 4:
                        this.mStringBuilder.append("I ");
                        break;
                    case 5:
                        this.mStringBuilder.append("W ");
                        break;
                    case 6:
                        this.mStringBuilder.append("E ");
                }

                this.mStringBuilder.append(bundle.getString("tag")).append(": ");
                this.mStringBuilder.append(bundle.getString("msg"));
                this.mWriter.write(this.mStringBuilder.toString());
                this.mWriter.write("\r\n");
                this.mWriter.flush();
            } catch (Exception var4) {
                Logger.e("LogWriter", "", (Throwable)var4);
            }

        }

        private void stopWrite() {
            if(this.mWriter != null) {
                IOUtils.closeSilently(this.mWriter);
            }

            Looper looper = this.getLooper();
            if(looper != null) {
                looper.quit();
            }

        }

        private final class LogHandler extends Handler {
            public LogHandler(Looper looper) {
                super(looper);
            }

            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                        LogWriter.this.writeLog(msg.getData());
                        break;
                    case 2:
                        LogWriter.this.stopWrite();
                }

            }
        }
    }
}
