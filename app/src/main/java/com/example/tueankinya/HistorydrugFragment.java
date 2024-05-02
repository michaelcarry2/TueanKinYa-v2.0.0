package com.example.tueankinya;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tueankinya.adapter.HistoryDrugAdapter;
import com.example.tueankinya.dao.DrugDatabaseHelper;
import com.example.tueankinya.dao.TakingMedicineDatabaseHelper;
import com.example.tueankinya.databinding.ActivityMainBinding;
import com.example.tueankinya.model.TakingMedicine;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistorydrugFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorydrugFragment extends Fragment {
    private RecyclerView historyDrugRecyclerView;
    private HistoryDrugAdapter historyDrugAdapter;
    private TakingMedicineDatabaseHelper dbHelper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ActivityMainBinding binding;

    private Button gobackButton;

    public HistorydrugFragment() {
        // Required empty public constructor
    }

    public static HistorydrugFragment newInstance(String param1, String param2) {
        HistorydrugFragment fragment = new HistorydrugFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historydrug, container, false);

        historyDrugRecyclerView = view.findViewById(R.id.history_drug_page);
        dbHelper = new TakingMedicineDatabaseHelper(getContext());
        List<TakingMedicine> takingMedicineList = dbHelper.getAllTakingMedicine();
        historyDrugAdapter = new HistoryDrugAdapter(takingMedicineList, getContext());
        historyDrugRecyclerView.setAdapter(historyDrugAdapter);
        historyDrugRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gobackButton = view.findViewById(R.id.goback_button_historydrug);

        gobackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToProfileFragment();
            }
        });


        return view;
    }
    private void navigateToProfileFragment() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

}