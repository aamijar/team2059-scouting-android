package com.team2059.scouting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class NewFragment extends Fragment {

    private TextView textView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container, false);

        Button button = view.findViewById(R.id.fragment_button_test);
        final EditText editText = view.findViewById(R.id.fragment_editText_test);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                ft.replace(R.id.fragment_container, new MenuFragment(), "Open Fragment TAG");
                ft.commit();
                ft.addToBackStack(null);
                //FileManager.makeDir(editText.getText().toString(), getContext());
                //openMainActivity();
            }
        });


        return view;
    }
    public void openMainActivity(){
        Intent intent = new Intent(getActivity(), Menu.class);
        startActivity(intent);
    }


}
