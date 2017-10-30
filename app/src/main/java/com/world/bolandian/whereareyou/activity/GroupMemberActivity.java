package com.world.bolandian.whereareyou.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.world.bolandian.whereareyou.Params;
import com.world.bolandian.whereareyou.R;
import com.world.bolandian.whereareyou.models.Groups;
import com.world.bolandian.whereareyou.models.Invitation;
import com.world.bolandian.whereareyou.models.User;

import java.util.ArrayList;

public class GroupMemberActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView rvGroupMemberList;
    private GroupMemberListAdapter adapter;
    private User userModel;
    private Groups groupModel;
    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser;
    private ArrayList<User> membersInGroup = new ArrayList<>();
    private ArrayList<User> usersFromDB = new ArrayList<>();
    private ArrayList<User> filterUsers = new ArrayList<>();
    private DatabaseReference mMembersInGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        rvGroupMemberList = (RecyclerView) findViewById(R.id.rvGroupMemberList);

        //this gets the Groups model from the GroupsFragment and sets the title of the group
        groupModel = getIntent().getExtras().getParcelable("model");
        if (groupModel.getGroupName() != null) {
            setTitle(groupModel.getGroupName());
        } else {
            setTitle("");
        }

        //gets all the members in the group so i can check on the filter if they already exist in the group
        mDatabase.getReference(Params.MEMBER_GROUP_LIST).child(groupModel.getGroupUID()).child(groupModel.getOwnerGroupUID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            membersInGroup.add(snapshot.getValue(User.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //get all the users and add them to arraylist when the activity is on
        mDatabase.getReference(Params.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    userModel = snapshot.getValue(User.class);
                    if (!(userModel.getUid().equals(currentUser.getUid()))) {
                        usersFromDB.add(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setAdapterFriends();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        //this fires up the onQueryTextSubmit method
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Search for friends");
        searchView.setOnQueryTextListener(this);

        return true;
    }


    //TODO: continue to complate this code - for now it is working
    //TODO: problem with the adapter. when i search for a user and add him.
    //TODO: i cant return to the adapter of the users in the group. -- CHECK IT!!!--
    //TODO: check if its good to do 2 recyclers
    @Override
    public boolean onQueryTextSubmit(String query) {
        //this method filters the user by the name that the user type
        filterUsers.clear();
        User memberInGroup;
        final String userNameInput = query.toLowerCase();
        for (int i = 0; i < usersFromDB.size(); i++) {
            //userModel = searchedUsersFromDatabase
            userModel = usersFromDB.get(i);
            if (!(membersInGroup.size() == 0)) {
                for (int j = 0; j < membersInGroup.size(); j++) {
                    memberInGroup = membersInGroup.get(j);
                    check(userModel, memberInGroup, userNameInput, i);
                    //TODO: not complete, in the result of the filter, it shows the same member X the members in the group
                    //TODO: need to think how to not show in the filter users that are already in the group
                }
            }else{
                addUserForTheFirstTime(userModel,userNameInput, i);
            }
        }
        if (filterUsers.size() == 0) {
            Toast.makeText(this, "User is not exist", Toast.LENGTH_SHORT).show();
        } else {
            adapter = new GroupMemberListAdapter(this, filterUsers, groupModel,currentUser);
            rvGroupMemberList.setAdapter(adapter);
            rvGroupMemberList.setLayoutManager(new LinearLayoutManager(this));
        }
        return true;
    }

    public void addUserForTheFirstTime(User userModel,String userNameInput,int i){
        if (userModel.getDisplayName().toLowerCase().contains(userNameInput))
            filterUsers.add(usersFromDB.get(i));
    }

    public void check(User userModel, User memberInGroup,String userNameInput,int i) {
         ArrayList<User> checkFilter = new ArrayList<>();
        //userModel = userFromDB(searched user)
        //this if filter the owner of the group
        if (!(userModel.getUid().equals(memberInGroup.getUid()))) {
            if (userModel.getDisplayName().toLowerCase().contains(userNameInput)) {
                if (filterUsers.size() == 0) {
                    filterUsers.add(usersFromDB.get(i));
                } else {
                    User userFilter = null;
                    for (i = 0; i < filterUsers.size(); i++) {
                        userFilter = filterUsers.get(i);
                        if (userFilter.getUid() != userModel.getUid())
                            filterUsers.add(usersFromDB.get(i));
                    }
                }
            }
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


        //Adapter for searching Members in the search view
    public static class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListViewHolder> {
            private Context context;
            private LayoutInflater inflater;
            private ArrayList<User> data;
            private Groups groupModel;
            private FirebaseUser currentUser;


        public GroupMemberListAdapter(Context context, ArrayList<User> data,Groups groupModel,FirebaseUser currentUser){
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.data = data;
            this.groupModel = groupModel;
            this.currentUser = currentUser;
        }

        @Override
        public GroupMemberListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.group_member_add_item,parent,false);
            return new GroupMemberListViewHolder(v,groupModel,currentUser);
        }

        @Override
        public void onBindViewHolder(GroupMemberListViewHolder holder, int position) {
                User user = data.get(position);
                holder.userName.setText(user.getDisplayName());
                Glide.with(context).load(user.getProfileImage()).into(holder.profileImage);
                holder.userModel = user;
                holder.context = context;
                Groups group = groupModel;
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    public static class GroupMemberListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView userName;
            private CircularImageView profileImage;
            private BootstrapButton btnAddMember;
            private User userModel;
            private Groups groupModel;
            private Context context;
            private FirebaseUser currentUser;

        public GroupMemberListViewHolder(View itemView,Groups groupModel,FirebaseUser currentUser) {
            super(itemView);
            this.groupModel = groupModel;
            this.currentUser = currentUser;
            userName = (TextView) itemView.findViewById(R.id.tvUserName);
            profileImage = (CircularImageView)itemView.findViewById(R.id.cvGroupImage);
            btnAddMember = (BootstrapButton)itemView.findViewById(R.id.btnAddMember);

            btnAddMember.setOnClickListener(this);
        }

        //add member to the group
        @Override
        public void onClick(View view) {
            //set Invitation Model
            //TODO:Works good - saves the data. continue work with that
            Task<GetTokenResult> idToken = currentUser.getIdToken(true);
            Invitation invite = new Invitation(Params.INVITE,currentUser.getDisplayName(),currentUser.getUid()
                    ,currentUser.getPhotoUrl().toString()
                    ,idToken.toString());

            //save the invitation under userID of the member you want to add to your group.
            //Invitations -> userUID invited -> data of the member who sends the request
            DatabaseReference refToNotifi = FirebaseDatabase.getInstance().getReference(Params.INVITATIONS).child(userModel.getUid());
            refToNotifi.setValue(invite);

            //save data under MemberGroupList -> groupID ->owner of the group ID -> user id -> the user data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.MEMBER_GROUP_LIST)
                    .child(groupModel.getGroupUID()).child(groupModel.getOwnerGroupUID())
                    .child(userModel.getUid());
            ref.setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String name = userModel.getDisplayName();
                    Toast.makeText(userName.getContext(),name + " Has added" , Toast.LENGTH_SHORT).show();
                    //TODO: set the adapter
                }
            });
        }
    }

    //Member List Adapter
   public static class MemberAdapter extends FirebaseRecyclerAdapter<User,MemberListViewHolder> {
       private Context context;
       private Groups modelGroup;
       private FirebaseUser currentUser;
       private ArrayList<User> membersInGroup;


       public MemberAdapter(Query query,Context context,Groups modelGroup,ArrayList<User> membersInGroup) {
           super(User.class, R.layout.group_member_item, MemberListViewHolder.class, query);
           this.context = context;
           this.modelGroup = modelGroup;
           currentUser = FirebaseAuth.getInstance().getCurrentUser();
           this.membersInGroup = membersInGroup;
       }

       @Override
       protected void populateViewHolder(MemberListViewHolder viewHolder, User model, int position) {
           User member;
           member = membersInGroup.get(position);
           //this if - check if its the owner of the group. if it is, it remove the button "kick" from his item
           if(currentUser.getUid().equals(member.getUid())){
               viewHolder.btnKick.setVisibility(View.GONE);
            //this if check if it is the owner of the group, if its not him then it remove the "kick" button from all the items
           }else if(!(currentUser.getUid().equals(modelGroup.getOwnerGroupUID()))){
               viewHolder.btnKick.setVisibility(View.GONE);
           }
            viewHolder.name.setText(model.getDisplayName());
            Glide.with(context).load(model.getProfileImage()).into(viewHolder.civImage);
            viewHolder.userModel = model;
            viewHolder.groups = modelGroup;
       }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }
    }

    public static class MemberListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView name;
            private CircularImageView civImage;
            private BootstrapButton btnKick;
            private User userModel;
            private Groups groups;

         public MemberListViewHolder(View itemView) {
             super(itemView);
             name = (TextView)itemView.findViewById(R.id.tvNameMember);
             civImage =(CircularImageView)itemView.findViewById(R.id.cvGroupImage);
             btnKick = (BootstrapButton) itemView.findViewById(R.id.btnKickMemeber);

             btnKick.setOnClickListener(this);
         }



         //Delete a member from the list
        @Override
        public void onClick(View view) {
            final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(btnKick.getContext());

            dialogDelete.setIcon(R.drawable.ic_warning);
            dialogDelete.setTitle("Kick:  " + userModel.getDisplayName());
            dialogDelete.setCancelable(true);
            dialogDelete.setMessage("Are you sure you want to kick him?");

            dialogDelete.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    //Delete member from a group
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.MEMBER_GROUP_LIST).child(groups.getGroupUID())
                                                                                                                .child(groups.getOwnerGroupUID());
                    ref.child(userModel.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(btnKick.getContext(),userModel.getDisplayName() + " Kicked", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(btnKick.getContext(), "some problem has occurred, please try again", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        }
                    });

                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            final AlertDialog alertDialog = dialogDelete.create();
            alertDialog.show();
        }
    }

    public void setAdapterFriends(){
        mMembersInGroup = FirebaseDatabase.getInstance().getReference(Params.MEMBER_GROUP_LIST)
                .child(groupModel.getGroupUID())
                .child(groupModel.getOwnerGroupUID());
        MemberAdapter adapter = new MemberAdapter(mMembersInGroup,this,groupModel,membersInGroup);
        rvGroupMemberList.setAdapter(adapter);
        rvGroupMemberList.setLayoutManager(new LinearLayoutManager(this));
    }
}

