package unitil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu on 2016/8/16.
 */
public class LogCollect extends Thread{
    private Process mProcess;
    private boolean mStart;
    private InputStream in;
    private InputStreamReader isstr;
    BufferedReader buffStr;
    @Override
    public void run() {
        super.run();
        this.startCollect();
    }
    public void startCollect(){
        List<String> commandList = new ArrayList<String>();
        commandList.add("logcat");
        commandList.add("-v");
        commandList.add("|");
        commandList.add("grep");
        commandList.add("test123");
        try{
            this.mProcess = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));
            Logger.e(this.mProcess.toString());
            in = this.mProcess.getInputStream();
            isstr = new InputStreamReader(in,"utf-8");
            buffStr = new BufferedReader(isstr);
            String line = null;
            StringBuffer result = new StringBuffer();
            while((line = buffStr.readLine())!=null){
                result.append(line+"\n");
            }
            Logger.e(result.toString());
        }
        catch (Exception e){
            Logger.e(e.getStackTrace().toString());
        }
    }
    public void launch(){
        if (this.mStart){
            return;
        }
        Logger.e("启动");
        this.mStart = true;
        this.start();
    }
    public void cancel(){
        if(!this.mStart){
            return;
        }
        Logger.e("取消");
        this.mStart = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogCollect.this.clearCache();
            }
        });

    }
    public void clearCache(){
        Process pro = null;
        List<String> commandList = new ArrayList<>();
        commandList.add("logcat");
        commandList.add("-c");
        try {
            Thread.sleep(300);
            if (null!=this.mProcess){
                this.mProcess.destroy();
                this.mProcess=null;
            }
            pro = Runtime.getRuntime().exec(commandList.toArray(new String[commandList.size()]));

        }catch (Exception e){
            Logger.e(e.getStackTrace().toString());
            }
        finally {
            try {
                if (pro != null) {
                    Thread.sleep(300);
                    pro.destroy();
                    pro = null;
                }
            }catch (Exception e){
                Logger.e(e.getStackTrace().toString());
            }

        }
    }

}
