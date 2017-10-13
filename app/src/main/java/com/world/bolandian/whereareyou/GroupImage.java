package com.world.bolandian.whereareyou;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static com.world.bolandian.whereareyou.R.id.tvGroupName;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupImage extends DialogFragment {
    private ImageView groupImageView;
    private TextView tvGroupTitle;

    public GroupImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_image, container, false);
        groupImageView = (ImageView)v.findViewById(R.id.ivGroup);
        tvGroupTitle = (TextView)v.findViewById(tvGroupName);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String groupName ="";
        String groupImage ="";

        Bundle groupData = getArguments();
        if(groupData != null) {
            groupName = groupData.getString("groupName");
            groupImage = groupData.getString("groupImage");
        }
        tvGroupTitle.setText(groupName);
        Glide.with(getContext()).load(groupImage).into(groupImageView);
    }
}
