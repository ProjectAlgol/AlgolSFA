package com.algol.project.algolsfa.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.helper.AppUtility;
import com.algol.project.algolsfa.others.Constants;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by swarnavo.dutta on 1/8/2019.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Toolbar commonToolBar;
    private LinearLayout tabOrderAndVisit, tabDelivery, tabReports;
    private Button btnOrderAndVisit, btnDelivery, btnReports;
    private RecyclerView navList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        commonToolBar = findViewById(R.id.tb_common);
        setSupportActionBar(commonToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        tabOrderAndVisit = findViewById(R.id.order_visit);
        tabOrderAndVisit.setOnClickListener(this);
        tabDelivery = findViewById(R.id.delivery);
        tabDelivery.setOnClickListener(this);
        tabReports = findViewById(R.id.reports);
        tabReports.setOnClickListener(this);
        btnOrderAndVisit = findViewById(R.id.btn_order_visit);
        btnOrderAndVisit.setOnClickListener(this);
        btnDelivery = findViewById(R.id.btn_delivery);
        btnDelivery.setOnClickListener(this);
        btnReports = findViewById(R.id.btn_reports);
        btnReports.setOnClickListener(this);
        navList= findViewById(R.id.rv_dashboard_list);
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
                btnReports.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnReports.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                showOrderAndVisitOptions();
                break;
            case R.id.delivery:
            case R.id.btn_delivery:
                btnOrderAndVisit.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnOrderAndVisit.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnDelivery.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnDelivery.setTextColor(getResources().getColor(R.color.white));
                btnReports.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnReports.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                showDeliveryOptions();
                break;
            case R.id.reports:
            case R.id.btn_reports:
                btnOrderAndVisit.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnOrderAndVisit.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnDelivery.setBackgroundColor(getResources().getColor(R.color.float_transparent));
                btnDelivery.setTextColor(getResources().getColor(R.color.inactive_tab_color));
                btnReports.setBackground(getResources().getDrawable(R.drawable.transparent_button_background_with_border));
                btnReports.setTextColor(getResources().getColor(R.color.white));
                showReportOptions();
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
    * displays the list of Order & Visit options based on priviledge
    * */
    {

    }

    private void showDeliveryOptions() {

    }

    private void showReportOptions() {

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
