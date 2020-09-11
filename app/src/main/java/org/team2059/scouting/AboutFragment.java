package org.team2059.scouting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ((NavigationDrawer) getActivity()).showBackButton(true);

        TextView acknowledgementsTitle = view.findViewById(R.id.about_event_data);
        acknowledgementsTitle.setText(getText(R.string.acknowledgements));

        TextView acknowledgementsDesc = view.findViewById(R.id.about_visit_frcAPI);
        String visit = "Visit " + getString(R.string.frcAPI_link);
        acknowledgementsDesc.setText(visit);

        LinearLayout frcAPIContainer = view.findViewById(R.id.about_frcAPI_container);

        frcAPIContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(getString(R.string.frcAPI_link));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        TextView teamWebsiteDesc = view.findViewById(R.id.about_visit_website);
        String visit2059 = "Visit " + getString(R.string.website);
        teamWebsiteDesc.setText(visit2059);

        LinearLayout teamWebsiteContainer = view.findViewById(R.id.about_website_container);
        teamWebsiteContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(getString(R.string.website));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });


        return view;

    }
}
