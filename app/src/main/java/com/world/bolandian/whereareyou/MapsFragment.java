package com.world.bolandian.whereareyou;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.world.bolandian.whereareyou.models.Groups;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private static final int RC_LOCATION = 1;
    private GoogleMap mMap;
    private DatabaseReference ref;
    private FirebaseUser user;
    private Groups groupModel;
    private RecyclerView rvGroupMap;




    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.fragment_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        rvGroupMap = (RecyclerView)findViewById(R.id.rvGroupMap);

        user = FirebaseAuth.getInstance().getCurrentUser();
        // each group saved by user id = userid -> group name
        ref = FirebaseDatabase.getInstance().getReference(Params.GROUP_LISTS);
        if(ref == null){ // the ref is not null
            Toast.makeText(this, "ref is NULL!!!!", Toast.LENGTH_SHORT).show();
        }
        try{
            GroupsMapAdapter groupMapAdapter = new GroupsMapAdapter(ref,this);
            Log.d("adapterCheck",groupMapAdapter.toString());Log.d("refe",ref.toString());Log.d("userCheck",user.toString());
            rvGroupMap.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
            rvGroupMap.setAdapter(groupMapAdapter);

        }catch (Exception e){
            Log.d("exceptionEcheck",e.toString());
            System.out.println(e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addMyLocation();

    }

    private boolean checkLocationPermission(){
        String[] permissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};
        //If No Permission-> Request the permission and return false.
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, permissions, RC_LOCATION);
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
                    Toast.makeText(getApplication(), "" + myLocation.getLatitude(), Toast.LENGTH_SHORT).show();
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
            private Context context;

        public GroupsMapAdapter(Query query,Context context) {
            super(Groups.class,R.layout.group_map_item,GroupsMapViewHolder.class, query);
            this.context = context;
        }

        @Override
        protected void populateViewHolder(GroupsMapViewHolder viewHolder, Groups model, int position) {
                viewHolder.groupName.setText(model.getGroupName());
           // Glide.with(context).load(model.ge).into(holder.profileImage);
        }
    }

    public static class GroupsMapViewHolder extends RecyclerView.ViewHolder{
            private TextView groupName;
            private ImageView groupImage;

        public GroupsMapViewHolder(View itemView) {
            super(itemView);
            groupName = (TextView)itemView.findViewById(R.id.tvGroupName);
        //    groupImage = (ImageView)itemView.findViewById(R.id.ivGroup);
        }
    }
}
