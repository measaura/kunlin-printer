package cn.westlan.coding.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import cn.leancloud.utils.StringUtil;
import cn.westlan.coding.util.HttpUtil;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class UpdateUtil {

    public static Single<Version> checkVersion(final String checkUrl, final String localVersion){
        return Single.fromCallable(()->{
            InputStream inputStream = HttpUtil.get(checkUrl);
            JSONObject jsonObject = new JSONObject(HttpUtil.readStr(inputStream, "UTF-8"));
            String version = jsonObject.optString("version");
            String url = jsonObject.optString("url");
            String description = jsonObject.optString("description");
            boolean needUpdate = !localVersion.equalsIgnoreCase(version)&& !StringUtil.isEmpty(version);
            return new Version().version(version).url(url).description(description).needUpdate(needUpdate);
        }).subscribeOn(Schedulers.io());
    }

    public static void installPackage(Context context, Version version){
        DownloadManager mDownloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
        Uri resource = Uri.parse(version.url());
        DownloadManager.Request request = new DownloadManager.Request(resource);
        //下载的本地路径，表示设置下载地址为SD卡的Download文件夹，文件名为mobileqq_android.apk。
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, UpdateUtil.getFileNameFromUrl(version.url()));
        //start 一些非必要的设置
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle("download...");
        request.setDescription(version.description());
        request.setMimeType("application/vnd.android.package-archive");
        long downLoadId = mDownloadManager.enqueue(request);
        //将downloadid存到本地
        SharedPreferences sharedPreferences = context.getSharedPreferences("download_id.txt",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("downLoadId",downLoadId);
        editor.commit();
    }

    public static Single<String> downloadPackage(final File dir, final String packageUrl){
        return Single.fromCallable(()->{
            InputStream inputStream = HttpUtil.get(packageUrl);
            String fileName = getFileNameFromUrl(packageUrl);
            File file = new File(dir, fileName);
            if (inputStream != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buf = new byte[1024];
                int ch = -1;
                while ((ch = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, ch);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                return file.getAbsolutePath();
            }
            return "";
        }).subscribeOn(Schedulers.io());
    }

    public static String getFileNameFromUrl(String url){
        int index = url.lastIndexOf("/");
        if(index > 0){
            String name = url.substring(index + 1).trim();
            if(name.length()>0){
                return name;
            }
        }
        return System.currentTimeMillis() + ".X";
    }

    public static String getVersionName(Context context) {
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
