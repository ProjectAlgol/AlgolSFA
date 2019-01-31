package com.algol.project.algolsfa.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.adapters.DashboardItemAdapter;
import com.algol.project.algolsfa.helper.APIClient;
import com.algol.project.algolsfa.helper.AppUtility;
import com.algol.project.algolsfa.helper.SQLiteHelper;
import com.algol.project.algolsfa.models.DashboardItemModel;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.others.Constants.UserPrivilege;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by swarnavo.dutta on 1/8/2019.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Toolbar commonToolBar;
    private LinearLayout tabOrderAndVisit, tabDelivery, tabAnalytics;
    private Button btnOrderAndVisit, btnDelivery, btnAnalytics;
    private RecyclerView navList;
    private SQLiteHelper dbHelper;
    private DashboardItemAdapter dashboardItemAdapter;
    private ArrayList<DashboardItemModel> orderAndVisitList, deliveryList, analyticsList;
    private TextView tvNoList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        dbHelper = SQLiteHelper.getHelper(context);
        commonToolBar = findViewById(R.id.tb_common);
        setSupportActionBar(commonToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        btnOrderAndVisit = findViewById(R.id.btn_order_visit);
        btnDelivery = findViewById(R.id.btn_delivery);
        btnAnalytics = findViewById(R.id.btn_analytics);
        tabOrderAndVisit = findViewById(R.id.order_visit);
        tabDelivery = findViewById(R.id.delivery);
        tabAnalytics = findViewById(R.id.analytics);
        navList = findViewById(R.id.rv_dashboard_list);
        tvNoList = findViewById(R.id.tv_no_list);
        navList.setHasFixedSize(true);
        navList.setLayoutManager(new LinearLayoutManager(context));
        orderAndVisitList = new ArrayList<>();
        deliveryList = new ArrayList<>();
        analyticsList = new ArrayList<>();
        int tabCount = 0;

        if (dbHelper.isPrivileged(UserPrivilege.OrderAndVisit.getValue())) {
            tabOrderAndVisit.setOnClickListener(this);
            btnOrderAndVisit.setOnClickListener(this);
            tabCount++;
            showOrderAndVisitOptions();
        } else {
            tabOrderAndVisit.setVisibility(View.GONE);
        }

        if (dbHelper.isPrivileged(UserPrivilege.Delivery.getValue())) {
            tabDelivery.setOnClickListener(this);
            btnDelivery.setOnClickListener(this);
            tabCount++;
            if (tabCount == 1) { // being the first tab
                btnDelivery.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnDelivery.setTextColor(getResources().getColor(R.color.white));
                showDeliveryOptions();
            }
        } else {
            tabDelivery.setVisibility(View.GONE);
        }

        if (dbHelper.isPrivileged(UserPrivilege.Analytics.getValue())) {
            tabAnalytics.setOnClickListener(this);
            btnAnalytics.setOnClickListener(this);
            tabCount++;
            if (tabCount == 1) { // being the first tab
                btnAnalytics.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnAnalytics.setTextColor(getResources().getColor(R.color.white));
                showAnalyticsOptions();
            }
        } else {
            tabAnalytics.setVisibility(View.GONE);
        }

        if (tabCount == 0) {
            findViewById(R.id.horizontal_menu).setVisibility(View.GONE);
            navList.setVisibility(View.GONE);
            tvNoList.setVisibility(View.VISIBLE);
        } else if (tabCount == 1) {
            findViewById(R.id.horizontal_menu).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        AppUtility.minimizeApp(context);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_toolbar_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_visit:
            case R.id.btn_order_visit:
                btnOrderAndVisit.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnOrderAndVisit.setTextColor(getResources().getColor(R.color.white));
                btnDelivery.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnDelivery.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnAnalytics.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnAnalytics.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                showOrderAndVisitOptions();
                break;
            case R.id.delivery:
            case R.id.btn_delivery:
                btnOrderAndVisit.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnOrderAndVisit.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnDelivery.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnDelivery.setTextColor(getResources().getColor(R.color.white));
                btnAnalytics.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnAnalytics.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                showDeliveryOptions();
                break;
            case R.id.analytics:
            case R.id.btn_analytics:
                btnOrderAndVisit.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnOrderAndVisit.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnDelivery.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnDelivery.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnAnalytics.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnAnalytics.setTextColor(getResources().getColor(R.color.white));
                showAnalyticsOptions();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                confirmLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showOrderAndVisitOptions()
    /*
    * displays the list of Order & Visit options based on privilege
    * */ {
        tvNoList.setVisibility(View.GONE);
        navList.setVisibility(View.VISIBLE);
        if (orderAndVisitList.size() == 0) {
            orderAndVisitList = prepareDataList("Order and Visit");
            if (orderAndVisitList.size() > 0) {
                dashboardItemAdapter = new DashboardItemAdapter(context, orderAndVisitList);
                navList.setAdapter(dashboardItemAdapter);
            } else {
                navList.setVisibility(View.GONE);
                tvNoList.setVisibility(View.VISIBLE);
            }
        } else {
            dashboardItemAdapter = new DashboardItemAdapter(context, orderAndVisitList);
            navList.setAdapter(dashboardItemAdapter);
        }
    }

    private void showDeliveryOptions() {
        tvNoList.setVisibility(View.GONE);
        navList.setVisibility(View.VISIBLE);
        if (deliveryList.size() == 0) {
            deliveryList = prepareDataList("Delivery");
            if (deliveryList.size() > 0) {
                dashboardItemAdapter = new DashboardItemAdapter(context, deliveryList);
                navList.setAdapter(dashboardItemAdapter);
            } else {
                navList.setVisibility(View.GONE);
                tvNoList.setVisibility(View.VISIBLE);
            }
        } else {
            dashboardItemAdapter = new DashboardItemAdapter(context, deliveryList);
            navList.setAdapter(dashboardItemAdapter);
        }
    }

    private void showAnalyticsOptions() {
        tvNoList.setVisibility(View.GONE);
        navList.setVisibility(View.VISIBLE);
        if (analyticsList.size() == 0) {
            analyticsList = prepareDataList("Analytics");
            if (analyticsList.size() > 0) {
                dashboardItemAdapter = new DashboardItemAdapter(context, analyticsList);
                navList.setAdapter(dashboardItemAdapter);
            } else {
                navList.setVisibility(View.GONE);
                tvNoList.setVisibility(View.VISIBLE);
            }
        } else {
            dashboardItemAdapter = new DashboardItemAdapter(context, analyticsList);
            navList.setAdapter(dashboardItemAdapter);
        }
    }

    private ArrayList<DashboardItemModel> prepareDataList(String purpose) {
        DashboardItemModel dashboardItem;
        switch (purpose) {
            case "Order and Visit":
                // Planned Visit
                if (dbHelper.isPrivileged(UserPrivilege.PlannedVisit.getValue())) {
                    dashboardItem = new DashboardItemModel();
                    dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                    dashboardItem.setTitle(getResources().getString(R.string.planned_visit));
                    dashboardItem.setDescription(getResources().getString(R.string.planned_visit_desc));
                    orderAndVisitList.add(dashboardItem);
                }

                // Unplanned Visit
                if (dbHelper.isPrivileged(UserPrivilege.UnplannedVisit.getValue())) {
                    dashboardItem = new DashboardItemModel();
                    dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                    dashboardItem.setTitle(getResources().getString(R.string.unplanned_visit));
                    dashboardItem.setDescription(getResources().getString(R.string.unplanned_visit_desc));
                    orderAndVisitList.add(dashboardItem);
                }

                // New Outlet Addition
                if (dbHelper.isPrivileged(UserPrivilege.NewOutletAddition.getValue())) {
                    dashboardItem = new DashboardItemModel();
                    dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                    dashboardItem.setTitle(getResources().getString(R.string.new_outlet_addition));
                    dashboardItem.setDescription(getResources().getString(R.string.new_outlet_addition_desc));
                    orderAndVisitList.add(dashboardItem);
                }

                // Multi Survey
                if (dbHelper.isPrivileged(UserPrivilege.MultiSurvey.getValue())) {
                    dashboardItem = new DashboardItemModel();
                    dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                    dashboardItem.setTitle(getResources().getString(R.string.multi_survey));
                    dashboardItem.setDescription(getResources().getString(R.string.multi_survey_desc));
                    orderAndVisitList.add(dashboardItem);
                }
                return orderAndVisitList;
            case "Delivery":
                // Order Delivery
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.order_delivery));
                dashboardItem.setDescription(getResources().getString(R.string.order_delivery_desc));
                deliveryList.add(dashboardItem);

                // My Transaction
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.my_transactions));
                dashboardItem.setDescription(getResources().getString(R.string.my_transactions_desc));
                deliveryList.add(dashboardItem);

                // Reload Stock
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.settlement));
                dashboardItem.setDescription(getResources().getString(R.string.settlement_desc));
                deliveryList.add(dashboardItem);

                // Reload Stock
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.reload_stock));
                dashboardItem.setDescription(getResources().getString(R.string.reload_stock_desc));
                deliveryList.add(dashboardItem);
                return deliveryList;
            case "Analytics":
                // View Outlets
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.view_outlets));
                dashboardItem.setDescription(getResources().getString(R.string.view_outlets_desc));
                analyticsList.add(dashboardItem);

                // My team
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.my_team));
                dashboardItem.setDescription(getResources().getString(R.string.my_team_desc));
                analyticsList.add(dashboardItem);

                // Order Reports
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.order_reports));
                dashboardItem.setDescription(getResources().getString(R.string.order_reports_desc));
                analyticsList.add(dashboardItem);

                // Delivery Reports
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.delivery_reports));
                dashboardItem.setDescription(getResources().getString(R.string.delivery_reports_desc));
                analyticsList.add(dashboardItem);

                // Survey Reports
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.survey_reports));
                dashboardItem.setDescription(getResources().getString(R.string.survey_reports_desc));
                analyticsList.add(dashboardItem);

                // Customer Segmentation Groupings
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.customer_segmentation_groupings));
                dashboardItem.setDescription(getResources().getString(R.string.customer_segmentation_groupings_desc));
                analyticsList.add(dashboardItem);

                // Profitability Analysis
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.profitability_analysis));
                dashboardItem.setDescription(getResources().getString(R.string.profitability_analysis_desc));
                analyticsList.add(dashboardItem);

                // Escalation Tracking
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.escalation_tracking));
                dashboardItem.setDescription(getResources().getString(R.string.escalation_tracking_desc));
                analyticsList.add(dashboardItem);

                // Predictive Modelling
                dashboardItem = new DashboardItemModel();
                dashboardItem.setIcon(getResources().getDrawable(R.drawable.outlet));
                dashboardItem.setTitle(getResources().getString(R.string.predictive_modelling));
                dashboardItem.setDescription(getResources().getString(R.string.predictive_modelling_desc));
                analyticsList.add(dashboardItem);
                return analyticsList;
            default:
                return null;
        }
    }

    public void onDashboardListItemClick(View view) {
        String title= ((TextView)view.findViewById(R.id.tv_dashboard_item_title)).getText().toString();
        String message= "Opening " + title;
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        HashMap<String,String> request= new HashMap<>();
        request.put("userid","12450");
        new APIClient(context,Constants.apiBase + "hndlMyTeam.ashx",null,request).exec();
    }

    private void confirmLogout() {
        SweetAlertDialog logoutDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        logoutDialog.setContentText(getResources().getString(R.string.logout_alert));
        logoutDialog.setCancelable(true);
        logoutDialog.setConfirmButton("Yes", this::processLogout);
        logoutDialog.setCancelButton("No", SweetAlertDialog::dismissWithAnimation);
        logoutDialog.show();
        logoutDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setBackgroundResource(R.drawable.confirm_button_background);
        logoutDialog.getButton(SweetAlertDialog.BUTTON_CANCEL).setBackgroundResource(R.drawable.cancel_button_background);
    }

    private void processLogout(SweetAlertDialog logoutAlert) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.LOGIN_CRED_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getResources().getString(R.string.password), "");
        editor.apply();
        Intent loginIntent = new Intent(context, LoginActivity.class);
        logoutAlert.dismissWithAnimation();
        startActivity(loginIntent);
        finish();
    }
}
