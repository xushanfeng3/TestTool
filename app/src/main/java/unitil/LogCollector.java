package unitil;

import android.content.Context;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu on 2016/8/8.
 */
public class LogCollector extends Thread{
    private static final String TAG = "logCollector";
    private static LogCollector sSingleeton;

    private final  String mLogFileRootDir;
    private final long mLogFileSize;
    private final int mLogFileNum;
    private final DecimalFormat mDecimalFormater;
    private Process mLogProcess;
    private boolean mlsSatrt;
    private final String mLogFilePath;

    private LogCollector(String LogFileRootDir,float logFileSize,int logFileNum){
        File logFile = new File(LogFileRootDir);
        if(!logFile.exists()){
            logFile.mkdir();
        }
        if(logFileNum<=0){
            logFileNum = 30;
        }
        if(logFileSize<=0){
            logFileSize=5;
        }
        this.mLogFileRootDir = logFile.getAbsolutePath()+File.separator;
        this.mLogFilePath = this.mLogFileRootDir+"Log.0"+File.separator+"logcat.log";
        this.mLogFileSize = (long)(logFileSize*1024);
        this.mLogFileNum = logFileNum;
        this.mDecimalFormater = new DecimalFormat("##.##");
    }
    public static LogCollector getInstance(Context context){
        if(context==null){
            throw new IllegalArgumentException();
        }
        if(sSingleeton==null){
            synchronized (LogCollector.class){
                if(sSingleeton==null){
                    String path = SdCardUtils.getSDCardRootPath(context)+File.separator+"cedlogs";
                    File file = new File(path);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    sSingleeton = new LogCollector(path,10,9);
                }
            }
        }
        return sSingleeton;
    }
    public static LogCollector getInstace(String logFileRootDir,float logFileSize,int logFileNum){
        if(logFileRootDir==null){
            throw new IllegalArgumentException();
        }
        if(sSingleeton==null) {
            synchronized (LogCollector.class) {
                if (sSingleeton == null) {
                    sSingleeton = new LogCollector(logFileRootDir, logFileSize, logFileNum);
                }
            }
        }
        return sSingleeton;
    }
    public static LogCollector getInstance(){
        return sSingleeton;
    }

    @Override
    public void run() {

    }
    public void initLogFileDir(){
        File log0File = new File(this.mLogFileRootDir+"log0");
        if(!log0File.exists()){
            log0File.mkdirs();
            return;
        }
        File log1File = new File(this.mLogFileRootDir+"log1");
        if(!log1File.exists()){
            log1File.mkdirs();
            return;
        }
        File log2File = new File(this.mLogFileRootDir+"log2");
        if(!log2File.exists()){
            log2File.mkdirs();
        }
//        FileHelper.
    }
    private void startLogCollect(){
        List<String> command = new ArrayList<>();
        command.add("logcat");
        command.add("-v");
        command.add("threadtime");
        command.add("-f");
        command.add(this.mLogFilePath);
        command.add("-r");
        command.add(String.valueOf(this.mLogFileSize));
        command.add("-n");
        command.add(String.valueOf(this.mLogFileNum));
        try{
            this.mLogProcess = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
        }
        catch (Exception e){
            Logger.e("StartLog"+e.toString());
        }
    }
    public  void launch(){
        if(this.mlsSatrt){
            return;
        }
        Logger.i("Launch.");
        this.mlsSatrt=true;
        this.start();
    }
    public void cancel(){
        if(!mlsSatrt){
            return;
        }
        Logger.i("CANCEL");
        this.mlsSatrt=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogCollector.this.clearLogCache();
            }
        }).start();
    }
    public String getLogDir(){
        return this.mLogFileRootDir;
    }
    private void clearLogCache(){
        Process proc = null;
        List<String> command = new ArrayList<>();
        command.add("logcat");
        command.add("-c");
        try {
            Thread.sleep(300);
            if(null !=this.mLogProcess){
                this.mLogProcess.destroy();
                this.mLogProcess=null;
            }

        }
        catch (Exception e){
            Logger.i("ClearCache.");
        }
        finally {
            try{
                if (proc!=null){
                    Thread.sleep(200);
                    proc.destroy();
                }
            }
            catch (Exception e){
                Logger.e("ClearLogCache."+e.toString());
            }
        }
    }
}
