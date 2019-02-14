package com.algol.project.algolsfa.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.algol.project.algolsfa.R;
import com.algol.project.algolsfa.activities.PlannedActivity;
import com.algol.project.algolsfa.helper.SecureSQLiteHelper;
import com.algol.project.algolsfa.models.CustomerDetailsModel;

import java.util.ArrayList;

/**
 * Created by swarnavo.dutta on 2/14/2019.
 */

public class OutletListFragment extends Fragment {
    private Context context;
    private SecureSQLiteHelper dbHelper;
    private ArrayList<CustomerDetailsModel> outlets;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_outlet_list,container,false);
        dbHelper= SecureSQLiteHelper.getHelper(context);
        outlets= dbHelper.getOutlets("Planned");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context= context;
        ((PlannedActivity)context).actionBar.setTitle(context.getResources().getString(R.string.planned_visit));
    }
}
