package com.world.bolandian.whereareyou;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.Query;
import com.world.bolandian.whereareyou.models.Groups;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final int RC_LOCATION = 1;
    private GoogleMap mMap;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addMyLocation();

    }

    private boolean checkLocationPermission(){
        String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};
        //If No Permission-> Request the permission and return false.
        if (ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), permissions, RC_LOCATION);
            return false;
        }
        return true;//return true if we have a permission
    }

    private void addMyLocation(){
        if (!checkLocationPermission())return;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mMap.getMyLocation()!=null) {
                    Location myLocation = mMap.getMyLocation();
                    Toast.makeText(getActivity(), "" + myLocation.getLatitude(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //noinspection MissingPermission
            addMyLocation();
        }
    }

    public static class GroupsMapAdapter extends FirebaseRecyclerAdapter<Groups,GroupsMapViewHolder> {

        public GroupsMapAdapter(Query query) {
            super(Groups.class,R.layout.group_map_item,GroupsMapViewHolder.class, query);
        }

        @Override
        protected void populateViewHolder(GroupsMapViewHolder viewHolder, Groups model, int position) {

        }
    }

    public static class GroupsMapViewHolder extends RecyclerView.ViewHolder{
            private TextView groupName;
            private ImageView groupImage;

        public GroupsMapViewHolder(View itemView) {
            super(itemView);
            //groupName = (TextView)itemView.findViewById(R.id.);
            //groupImage = (ImageView)itemView.findViewById(R.id.);
        }
    }
}
