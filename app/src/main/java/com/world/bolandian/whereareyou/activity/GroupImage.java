package com.world.bolandian.whereareyou.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.world.bolandian.whereareyou.R;

import java.io.File;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

import static com.world.bolandian.whereareyou.R.id.tvGroupName;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupImage extends DialogFragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView groupImageView;
    private TextView tvGroupTitle;
    private ImageButton ibImport,ibPicture;
    private View v;

    public GroupImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.fragment_group_image, container, false);
        groupImageView = (ImageView)v.findViewById(R.id.ivGroup);
        tvGroupTitle = (TextView)v.findViewById(tvGroupName);
        ibImport = (ImageButton)v.findViewById(R.id.ibImport);
        ibPicture = (ImageButton)v.findViewById(R.id.ibPicture);

        ibPicture.setOnClickListener(this);
        ibImport.setOnClickListener(this);
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String groupName ="";
        String groupImage ="";

        //get the picture link and the name of the group from the groupFragment
        //when the picture of the group is pressed
        Bundle groupData = getArguments();
        if(groupData != null) {
            groupName = groupData.getString("groupName");
            groupImage = groupData.getString("groupImage");
        }
        tvGroupTitle.setText(groupName);
        Glide.with(getContext()).load(groupImage).into(groupImageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibImport:
                if(!checkStoragePermission())return;
                EasyImage.openChooserWithGallery(getActivity(),"Please pick a picture",1);
                break;
            case R.id.ibPicture:
                break;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                onClick(v);
        }

    }

    private boolean checkStoragePermission(){
        int resultCode = ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean granted = resultCode == PackageManager.PERMISSION_GRANTED;

        if (!granted){
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PICK_IMAGE_REQUEST /*Constant Field int*/
            );
        }
        return granted;
    }

    //TODO:find out why it is not getting into activity result after getting the picture
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                Toast.makeText(getContext(),e + "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                if (imageFiles.size() > 0) {
                    File file = imageFiles.get(0);
                    //String name = file.getName();
                    Uri uri = Uri.fromFile(file);

                    Toast.makeText(getContext(), "OnActivityResult!!!!", Toast.LENGTH_SHORT).show();
                    //Bitmap vs file -> file.
                    String absolutePath = file.getAbsolutePath();
                    Glide.with(getActivity()).load(file).into(groupImageView);
                }
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });
    }
}
