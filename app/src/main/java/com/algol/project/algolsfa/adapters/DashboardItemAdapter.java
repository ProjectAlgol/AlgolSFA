package com.algol.project.algolsfa.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.activities.HomeActivity;
import com.algol.project.algolsfa.models.DashboardItemModel;

import java.util.ArrayList;

/**
 * Created by swarnavo.dutta on 1/22/2019.
 */

public class DashboardItemAdapter extends RecyclerView.Adapter<DashboardItemAdapter.ItemViewHolder> {
    private ArrayList<DashboardItemModel> dashboardItemList;
    private Context context;
    private HomeActivity parent;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle, tvDescription;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_dashboard_item_icon);
            tvTitle= itemView.findViewById(R.id.tv_dashboard_item_title);
            tvDescription = itemView.findViewById(R.id.tv_dashboard_item_description);
        }
    }

    public DashboardItemAdapter(Context context, ArrayList<DashboardItemModel> dashboardItemList) {
        this.dashboardItemList = dashboardItemList;
        this.context = context;
        this.parent = (HomeActivity) context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(context).inflate(context.getResources().getLayout(R.layout.layout_dashbord_item_card),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DashboardItemModel dashboardItem= dashboardItemList.get(position);
        holder.ivIcon.setImageDrawable(dashboardItem.getIcon());
        holder.tvTitle.setText(dashboardItem.getTitle());
        holder.tvDescription.setText(dashboardItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return dashboardItemList.size();
    }
}
