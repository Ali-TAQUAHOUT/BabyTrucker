package com.example.babyone;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link extrasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class extrasFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public extrasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment extrasFragment.
     */
    public static extrasFragment newInstance(String param1, String param2) {
        extrasFragment fragment = new extrasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_extras, container, false);
        MaterialCardView btnGotoLang;
        btnGotoLang = view.findViewById(R.id.btnGotoLang);

        btnGotoLang.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LangChangeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });

        // Inflate the layout for this fragment
        return view;
    }
}
