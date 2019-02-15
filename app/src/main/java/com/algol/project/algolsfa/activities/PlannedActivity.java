package com.algol.project.algolsfa.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.fragments.OutletListFragment;
import com.algol.project.algolsfa.interfaces.PlannedUnplannedFragmentCommunicator;
import com.algol.project.algolsfa.models.CustomerDetailsModel;

/**
 * Created by swarnavo.dutta on 2/14/2019.
 */

public class PlannedActivity extends AppCompatActivity implements PlannedUnplannedFragmentCommunicator {
    private CustomerDetailsModel selectedCustomer;
    private Toolbar commonToolbar;
    private FrameLayout plannedFragmentHolder;
    private ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned);
        commonToolbar= findViewById(R.id.tb_common);
        setSupportActionBar(commonToolbar);
        actionBar= getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        plannedFragmentHolder= findViewById(R.id.planned_fragments_holder);
        loadFragment(new OutletListFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.planned_activity_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.item_team_chat:
                return true;
            default:
                return false;
        }

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.planned_fragments_holder, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void setTitle(String title) {
        actionBar.setTitle(title);
    }
}
