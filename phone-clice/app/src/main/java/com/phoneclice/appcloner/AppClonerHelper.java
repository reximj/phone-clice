package com.phoneclice.appcloner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppClonerHelper {

    private static final String PREFS_NAME = "cloned_apps";
    private static final String KEY_CLONE_COUNT = "clone_count_";
    private Context context;
    private SharedPreferences prefs;

    public AppClonerHelper(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean cloneApp(AppInfo appInfo) {
        try {
            int cloneId = getNextCloneId(appInfo.getPackageName());
            String clonedDir = getClonedAppDir(appInfo.getPackageName(), cloneId);
            
            File dir = new File(clonedDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            saveCloneInfo(appInfo, cloneId);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<AppInfo> getClonedApps() {
        List<AppInfo> clonedApps = new ArrayList<>();
        Map<String, ?> allPrefs = prefs.getAll();
        
        for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("cloned_")) {
                String[] parts = key.split("_");
                if (parts.length >= 3) {
                    String packageName = parts[1];
                    int cloneId = Integer.parseInt(parts[2]);
                    
                    try {
                        PackageManager pm = context.getPackageManager();
                        android.content.pm.ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                        
                        AppInfo clonedApp = new AppInfo();
                        clonedApp.setAppName(appInfo.loadLabel(pm).toString() + " (分身" + cloneId + ")");
                        clonedApp.setPackageName(packageName);
                        clonedApp.setAppIcon(appInfo.loadIcon(pm));
                        clonedApp.setCloned(true);
                        clonedApp.setCloneId(cloneId);
                        
                        clonedApps.add(clonedApp);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return clonedApps;
    }

    public void launchClonedApp(AppInfo appInfo) {
        try {
            PackageManager pm = context.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.putExtra("clone_id", appInfo.getCloneId());
                context.startActivity(launchIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("无法启动应用");
        }
    }

    public boolean deleteClone(AppInfo appInfo) {
        try {
            String key = "cloned_" + appInfo.getPackageName() + "_" + appInfo.getCloneId();
            prefs.edit().remove(key).apply();
            
            String clonedDir = getClonedAppDir(appInfo.getPackageName(), appInfo.getCloneId());
            deleteDirectory(new File(clonedDir));
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getNextCloneId(String packageName) {
        int count = prefs.getInt(KEY_CLONE_COUNT + packageName, 0);
        count++;
        prefs.edit().putInt(KEY_CLONE_COUNT + packageName, count).apply();
        return count;
    }

    private void saveCloneInfo(AppInfo appInfo, int cloneId) {
        String key = "cloned_" + appInfo.getPackageName() + "_" + cloneId;
        prefs.edit().putBoolean(key, true).apply();
    }

    private String getClonedAppDir(String packageName, int cloneId) {
        return context.getExternalFilesDir(null) + "/cloned/" + packageName + "_" + cloneId;
    }

    private void deleteDirectory(File dir) {
        if (dir != null && dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            dir.delete();
        }
    }

    private void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
