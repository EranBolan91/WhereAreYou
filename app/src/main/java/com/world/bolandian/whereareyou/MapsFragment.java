package com.world.bolandian.whereareyou;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.world.bolandian.whereareyou.models.Groups;
import com.world.bolandian.whereareyou.models.MemberLocation;
import com.world.bolandian.whereareyou.models.User;

public class MapsFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, com.google.android.gms.location.LocationListener,
                                                    GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final int RC_LOCATION = 1;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private DatabaseReference ref,refToMemberList;
    private FirebaseUser user;
    private Groups groupModel;
    private RecyclerView rvGroupMap;


    public MapsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_maps, container, false);
        rvGroupMap = (RecyclerView)view.findViewById(R.id.rvGroupMap);
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        // each group saved by user id = userid -> group name
        ref = FirebaseDatabase.getInstance().getReference(Params.GROUP_LISTS).child(user.getUid());

            GroupsMapAdapter groupMapAdapter = new GroupsMapAdapter(ref,getContext());
            rvGroupMap.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true));
            rvGroupMap.setAdapter(groupMapAdapter);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        if(checkLocationPermission())
        mMap.setMyLocationEnabled(true);

        addMyLocation();

    }

    //save in the firebase the user location
    @Override
    public void onLocationChanged(Location location) {
        ref = FirebaseDatabase.getInstance().getReference(Params.MEMBER_LOCATION);
        GeoFire geoFire = new GeoFire(ref);
        geoFire.setLocation(user.getUid(),new GeoLocation(location.getLatitude(),location.getLongitude()));

        refToMemberList = FirebaseDatabase.getInstance().getReference(Params.MEMBER_GROUP_LIST);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).
                addConnectionCallbacks(this).
                addApi(LocationServices.API).
                build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        ref = FirebaseDatabase.getInstance().getReference(Params.MEMBER_LOCATION);
//        GeoFire geoFire = new GeoFire(ref);
//        geoFire.removeLocation(user.getUid());
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

        User userModel = new User();
        userModel.setDisplayName(user.getDisplayName());
        userModel.setEmail(user.getEmail());
        userModel.setProfileImage(String.valueOf(user.getPhotoUrl()));
        userModel.setUid(user.getUid());

        if(mMap.getMyLocation() != null) {
            Location userLocation = mMap.getMyLocation();

            MemberLocation memberLocation = new MemberLocation(userModel, userLocation.getLatitude(), userLocation.getLongitude());
            ref = FirebaseDatabase.getInstance().getReference(Params.MEMBER_LOCATION).child(user.getUid());
            ref.setValue(memberLocation);
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (mMap.getMyLocation()!=null) {

                    Location myLocation = mMap.getMyLocation();
                    Toast.makeText(getContext(), "" + myLocation.getLatitude(), Toast.LENGTH_SHORT).show();
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




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(checkLocationPermission())
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
