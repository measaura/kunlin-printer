package cn.westlan.coding.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import androidx.core.content.FileProvider;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("download_id.txt", MODE_PRIVATE);
        long downloadApkId = sharedPreferences.getLong("downLoadId", 0);
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            installApk(context, downloadApkId);
        }

    }

    private static void installApk(Context context, long downloadApkId) {

        Intent install = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            // 由于没有在Activity环境下启动Activity,设置下面的标签
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            String filename=  getPathWithId(downloadApkId, context);;
            String authority = context.getApplicationContext().getPackageName() + ".fileProvider";
            Uri apkUri = FileProvider.getUriForFile(context, authority, new File(filename));
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(install);
    }

    private static String getPathWithId(long id, Context context) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = manager.query(query);
        if (c.moveToFirst()) {
            //获取文件下载路径
            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String fileUri = c.getString(columnIndex);
            if (!TextUtils.isEmpty(fileUri)) {
                return Uri.parse(fileUri).getPath();
            }
        }
        return "";
    }

}