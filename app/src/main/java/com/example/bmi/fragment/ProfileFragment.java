package com.example.bmi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bmi.DBHelper;
import com.example.bmi.activity.LoginActivity;
import com.example.bmi.R;

public class ProfileFragment extends Fragment {

    TextView tv_welcome;
    Button btn_logout;
    DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_welcome = view.findViewById(R.id.tv_welcome);
        btn_logout = view.findViewById(R.id.btn_logout);
        dbHelper = new DBHelper(getActivity());

        String user = dbHelper.getLoggedInUser();

        if (user != null) {

            tv_welcome.setText("Hello " + user);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return view;
    }
}
