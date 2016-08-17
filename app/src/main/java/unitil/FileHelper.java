package unitil;

/**
 * Created by xu on 2016/8/17.
 */
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class FileHelper {
    private static final String TAG = "FileHelper";

    private FileHelper() {
    }

    public static boolean existFileInAssets(Context context, String fileName, String apkPath) {
        if(context != null && !StringUtils.isEmpty(fileName)) {
            InputStream inputStream = null;

            try {
                inputStream = getInputStreamFromAssets(context, fileName, apkPath);
                if(inputStream == null) {
                    return false;
                }

                return true;
            } catch (Exception var8) {
                Logger.e("FileHelper", "existFileInAssets. " + var8.toString());
            } finally {
                IOUtils.closeSilently(inputStream);
            }

            return false;
        } else {
            return false;
        }
    }

    public static boolean extractFileFromAssets(Context context, String fileName, String desFilePath, String apkPath) {
        if(context != null && !StringUtils.isEmpty(fileName) && !StringUtils.isEmpty(desFilePath)) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            boolean var8;
            try {
                File e = new File(desFilePath + ".temp");
                if(!createDir(e)) {
                    return false;
                }

                outputStream = new FileOutputStream(e);
                inputStream = getInputStreamFromAssets(context, fileName, apkPath);
                if(!wirte(inputStream, outputStream)) {
                    return false;
                }

                var8 = renameFile(e, new File(desFilePath));
            } catch (Exception var11) {
                Logger.e("FileHelper", "extractFileFromAssets. " + var11.toString());
                return false;
            } finally {
                IOUtils.closeSilently(inputStream);
                IOUtils.closeSilently(outputStream);
            }

            return var8;
        } else {
            return false;
        }
    }

    public static String getValueFromAssets(Context context, String fileName, String tagName, String apkPath) {
        if(context != null && !StringUtils.isEmpty(fileName) && !StringUtils.isEmpty(tagName)) {
            InputStream inputStream = null;
            XmlPullParser xmlPullParser = null;

            try {
                inputStream = getInputStreamFromAssets(context, fileName, apkPath);
                if(inputStream == null) {
                    return "";
                }

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(inputStream, "UTF-8");
                int e = xmlPullParser.getEventType();

                while(e != 1) {
                    switch(e) {
                        case 2:
                            if(tagName.equals(xmlPullParser.getName())) {
                                String var9 = xmlPullParser.nextText();
                                return var9;
                            }
                        case 0:
                        case 1:
                        case 3:
                        default:
                            e = xmlPullParser.next();
                    }
                }

                return "";
            } catch (Exception var12) {
                Logger.e("FileHelper", "getValueFromAssetsFile. " + var12.toString());
            } finally {
                IOUtils.closeSilently(inputStream);
            }

            return "";
        } else {
            return "";
        }
    }

    public static InputStream getInputStreamFromAssets(Context context, String fileName, String apkPath) throws Exception {
        if(StringUtils.isEmpty(apkPath)) {
            return context.getAssets().open(fileName);
        } else {
            AssetManager assetManager = (AssetManager)AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", new Class[]{String.class});
            addAssetPath.invoke(assetManager, new Object[]{apkPath});
            return assetManager.open(fileName);
        }
    }

    private static boolean createDir(File file) {
        if(file == null) {
            return false;
        } else {
            boolean mkResult = true;
            if(!file.getParentFile().exists()) {
                mkResult = file.getParentFile().mkdirs();
            }

            if(file.exists()) {
                file.delete();
            }

            file.setReadable(true);
            file.setExecutable(true);
            file.setWritable(true);
            return mkResult;
        }
    }

    public static boolean renameFile(String oldFilePath, String newFilePath) {
        return !StringUtils.isEmpty(oldFilePath) && !StringUtils.isEmpty(newFilePath)?renameFile(new File(oldFilePath), new File(newFilePath)):false;
    }

    public static boolean renameFile(File oldFile, File newFile) {
        if(oldFile != null && newFile != null) {
            if(!oldFile.exists()) {
                return false;
            } else {
                if(newFile.exists()) {
                    newFile.delete();
                }

                return oldFile.renameTo(newFile);
            }
        } else {
            return false;
        }
    }

    public static File getDirPath(Context context, String subDirName) {
        File root = context.getFilesDir();
        if(root == null) {
            return null;
        } else {
            File subDir = new File(root.getAbsolutePath() + File.separator + subDirName);
            Logger.d("FileHelper", "getDirPath: " + subDir.getAbsolutePath());
            return subDir;
        }
    }

    public static String getExternalStoragePath() {
        return Environment.getExternalStorageState().equals("mounted")?Environment.getExternalStorageDirectory().getAbsolutePath():File.separator + "sdcard";
    }

    public static File getSdPath(Context context, String dirName) {
        if(context == null) {
            return null;
        } else {
            File file = context.getExternalFilesDir(dirName);
            if(file == null) {
                file = new File(getExternalStoragePath() + File.separator + "Android" + File.separator + "data" + File.separator + context.getPackageName() + File.separator + "files" + File.separator + dirName);
                file.mkdirs();
            }

            return file;
        }
    }

    public static boolean copyFile(String srcFilePath, String desFilePath) {
        return !StringUtils.isEmpty(srcFilePath) && !StringUtils.isEmpty(desFilePath)?copyFile(new File(srcFilePath), new File(desFilePath)):false;
    }

    public static boolean copyAndRenameFile(File srcFile, File desFile) {
        return !copyFile(srcFile, desFile)?false:renameFile(srcFile, new File(srcFile.getAbsolutePath() + ".test"));
    }

    public static boolean copyFile(File srcFile, File desFile) {
        if(srcFile != null && desFile != null) {
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;

            boolean var6;
            try {
                File e = new File(desFile.getAbsolutePath() + ".temp");
                if(!createDir(e)) {
                    return false;
                }

                inputStream = new FileInputStream(srcFile);
                outputStream = new FileOutputStream(e);
                wirte(inputStream, outputStream);
                var6 = renameFile(e, desFile);
            } catch (Exception var9) {
                Logger.e("FileHelper", "", var9);
                return false;
            } finally {
                IOUtils.closeSilently(inputStream);
                IOUtils.closeSilently(outputStream);
            }

            return var6;
        } else {
            return false;
        }
    }

    private static boolean wirte(InputStream inputStream, OutputStream outputStream) throws IOException, IndexOutOfBoundsException {
        if(inputStream != null && outputStream != null) {
            if(inputStream.available() <= 0) {
                return false;
            } else {
                byte[] buffer = new byte[1024];
                boolean len = true;

                int len1;
                while((len1 = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len1);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static void deleteFileOrDir(File file, boolean keepRootDir) {
        if(file != null) {
            if(file.isFile()) {
                file.delete();
            } else {
                if(file.isDirectory()) {
                    File[] childFiles = file.listFiles();
                    if(childFiles == null || childFiles.length == 0) {
                        if(!keepRootDir) {
                            file.delete();
                        }

                        return;
                    }

                    for(int i = 0; i < childFiles.length; ++i) {
                        deleteFileOrDir(childFiles[i], false);
                    }

                    if(!keepRootDir) {
                        file.delete();
                    }
                }

            }
        }
    }

    public static boolean writeFile(String content, String storePath) {
        return !StringUtils.isEmpty(content) && !StringUtils.isEmpty(storePath)?writeFile(content.getBytes(Charset.forName("UTF-8")), storePath):false;
    }

    public static boolean writeFile(byte[] data, String storePath) {
        if(data != null && !StringUtils.isEmpty(storePath)) {
            BufferedOutputStream bos = null;

            try {
                File e = new File(storePath + ".temp");
                if(createDir(e)) {
                    bos = new BufferedOutputStream(new FileOutputStream(e));
                    bos.write(data);
                    boolean var5 = renameFile(e, new File(storePath));
                    return var5;
                }
            } catch (Exception var8) {
                Logger.e("FileHelper", "", var8);
                return false;
            } finally {
                IOUtils.closeSilently(bos);
            }

            return false;
        } else {
            return false;
        }
    }
}

