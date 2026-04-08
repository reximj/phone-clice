package com.phoneclice.appcloner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {

    private List<AppInfo> appList;
    private OnAppActionListener listener;
    private boolean showClonedApps;

    public interface OnAppActionListener {
        void onCloneApp(AppInfo appInfo);
        void onLaunchApp(AppInfo appInfo);
        void onDeleteClone(AppInfo appInfo);
    }

    public AppAdapter(OnAppActionListener listener, boolean showClonedApps) {
        this.appList = new ArrayList<>();
        this.listener = listener;
        this.showClonedApps = showClonedApps;
    }

    public void setAppList(List<AppInfo> appList) {
        this.appList = appList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo appInfo = appList.get(position);
        holder.bind(appInfo);
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView packageName;
        MaterialButton actionButton;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
            packageName = itemView.findViewById(R.id.packageName);
            actionButton = itemView.findViewById(R.id.actionButton);
        }

        public void bind(AppInfo appInfo) {
            appIcon.setImageDrawable(appInfo.getAppIcon());
            appName.setText(appInfo.getAppName());
            
            if (appInfo.isCloned()) {
                packageName.setText(appInfo.getClonedPackageName());
                actionButton.setText(R.string.launch);
                actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onLaunchApp(appInfo);
                    }
                });
            } else {
                packageName.setText(appInfo.getPackageName());
                actionButton.setText(R.string.clone_app);
                actionButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onCloneApp(appInfo);
                    }
                });
            }
        }
    }
}
