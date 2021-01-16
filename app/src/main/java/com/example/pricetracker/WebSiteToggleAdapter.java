package com.example.pricetracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WebSiteToggleAdapter extends RecyclerView.Adapter<WebSiteToggleAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SiteToggler> siteTogglers;

    public WebSiteToggleAdapter(Context context, ArrayList<SiteToggler> siteTogglers) {
        this.context = context;
        this.siteTogglers = siteTogglers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.site_toggle_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.siteName.setText(siteTogglers.get(position).getSiteName());
        holder.siteToggle.setChecked(siteTogglers.get(position).isChecked());
        holder.siteLogo.setImageResource(siteTogglers.get(position).getDrawable());
        holder.siteToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                siteTogglers.get(position).setChecked(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return siteTogglers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteName;
        ImageView siteLogo;
        Switch siteToggle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.textView_site_name);
            siteLogo = itemView.findViewById(R.id.imageView_ic_logo);
            siteToggle = itemView.findViewById(R.id.switch_site_toggle);
        }
    }
}
