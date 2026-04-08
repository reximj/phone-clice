package com.phoneclice.appcloner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AppAdapter.OnAppActionListener {

    private RecyclerView recyclerView;
    private AppAdapter appAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tabLayout;
    private FloatingActionButton fabKeepAlive;
    private List<AppInfo> installedApps;
    private List<AppInfo> clonedApps;
    private ExecutorService executorService;
    private Handler mainHandler;
    private boolean isShowingClonedApps = false;
    private AppClonerHelper clonerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initHelpers();
        loadApps();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tabLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        fabKeepAlive = findViewById(R.id.fabKeepAlive);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appAdapter = new AppAdapter(this, false);
        recyclerView.setAdapter(appAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isShowingClonedApps = tab.getPosition() == 1;
                appAdapter = new AppAdapter(MainActivity.this, isShowingClonedApps);
                recyclerView.setAdapter(appAdapter);
                displayApps();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this::loadApps);

        fabKeepAlive.setOnClickListener(v -> {
            KeepAliveService.startService(this);
            Toast.makeText(this, "后台保活服务已启动", Toast.LENGTH_SHORT).show();
        });
    }

    private void initHelpers() {
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        clonerHelper = new AppClonerHelper(this);
        installedApps = new ArrayList<>();
        clonedApps = new ArrayList<>();
    }

    private void loadApps() {
        swipeRefreshLayout.setRefreshing(true);
        
        executorService.execute(() -> {
            PackageManager pm = getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            
            installedApps.clear();
            for (ApplicationInfo app : apps) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppName(app.loadLabel(pm).toString());
                    appInfo.setPackageName(app.packageName);
                    appInfo.setAppIcon(app.loadIcon(pm));
                    installedApps.add(appInfo);
                }
            }

            clonedApps.clear();
            clonedApps.addAll(clonerHelper.getClonedApps());

            mainHandler.post(() -> {
                displayApps();
                swipeRefreshLayout.setRefreshing(false);
            });
        });
    }

    private void displayApps() {
        if (isShowingClonedApps) {
            appAdapter.setAppList(clonedApps);
        } else {
            appAdapter.setAppList(installedApps);
        }
    }

    @Override
    public void onCloneApp(AppInfo appInfo) {
        ProgressDialog dialog = ProgressDialog.show(this, "分身中", "正在创建应用分身，请稍候...", true);
        
        executorService.execute(() -> {
            boolean success = clonerHelper.cloneApp(appInfo);
            mainHandler.post(() -> {
                dialog.dismiss();
                if (success) {
                    Toast.makeText(this, "应用分身创建成功！", Toast.LENGTH_SHORT).show();
                    loadApps();
                } else {
                    Toast.makeText(this, "应用分身创建失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onLaunchApp(AppInfo appInfo) {
        try {
            clonerHelper.launchClonedApp(appInfo);
        } catch (Exception e) {
            Toast.makeText(this, "启动失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClone(AppInfo appInfo) {
        boolean success = clonerHelper.deleteClone(appInfo);
        if (success) {
            Toast.makeText(this, "分身已删除", Toast.LENGTH_SHORT).show();
            loadApps();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
