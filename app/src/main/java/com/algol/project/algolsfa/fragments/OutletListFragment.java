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
import com.algol.project.algolsfa.helper.SecureSQLiteHelper;
import com.algol.project.algolsfa.interfaces.PlannedUnplannedFragmentCommunicator;
import com.algol.project.algolsfa.models.CustomerDetailsModel;

import java.util.ArrayList;

/**
 * Created by swarnavo.dutta on 2/14/2019.
 */

public class OutletListFragment extends Fragment {
    private Context context;
    private SecureSQLiteHelper dbHelper;
    private ArrayList<CustomerDetailsModel> outlets;
    private String parentName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context= context;
        parentName= context.getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PlannedUnplannedFragmentCommunicator)context).setTitle((parentName.equalsIgnoreCase("PlannedActivity"))? context.getResources().getString(R.string.planned_visit) : context.getResources().getString(R.string.unplanned_visit));
        dbHelper= SecureSQLiteHelper.getHelper(context);
        outlets= dbHelper.getOutlets((parentName.equalsIgnoreCase("PlannedActivity"))? "Planned" : "Unplanned");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_outlet_list,container,false);
        return view;
    }
}
