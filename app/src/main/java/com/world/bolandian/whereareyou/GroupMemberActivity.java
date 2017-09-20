package com.world.bolandian.whereareyou;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.world.bolandian.whereareyou.models.Groups;
import com.world.bolandian.whereareyou.models.User;

import java.util.ArrayList;

public class GroupMemberActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView rvGroupMemberList;
    private GroupMemberListAdapter adapter;
    private User userModel;
    private Groups groupModel;
    private FirebaseDatabase mDatabase;
    private FirebaseUser currentUser;
    private ArrayList<User> adapterUsers = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
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
        rvGroupMemberList = (RecyclerView)findViewById(R.id.rvGroupMemberList);

        //this gets the Groups model from the GroupsFragment and sets the title of the group
         groupModel = getIntent().getExtras().getParcelable("model");
        if(groupModel.getGroupName() != null) {
            setTitle(groupModel.getGroupName());
        }else {
            setTitle("");
        }

        //get all the users and add them to arrylist when the activity is on
        mDatabase.getReference(Params.USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    userModel = snapshot.getValue(User.class);
                    if(!(userModel.getUid().equals(currentUser.getUid()))){
                        users.add(userModel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //TODO: find out why it gives me only one result. maybe the ref is not good enough
        mMembersInGroup = FirebaseDatabase.getInstance().getReference(Params.MEMBER_GROUP_LIST)
                                                        .child(groupModel.getGroupUID())
                                                        .child(groupModel.getOwnerGroupUID());
        MemberAdapter adapter = new MemberAdapter(mMembersInGroup,this);
        rvGroupMemberList.setAdapter(adapter);
        rvGroupMemberList.setLayoutManager(new LinearLayoutManager(this));

        //this code line gives me an error "DatabaseException: cant convert object of type String to model user.
        //need to check why
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

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
        final String userNameInput = query.toLowerCase();
        for(int i = 0; i < users.size();i++){
            userModel = users.get(i);
            if(userModel.getDisplayName().toLowerCase().contains(userNameInput)){
                filterUsers.add(users.get(i));
            }
        }
        if(filterUsers.size() == 0){
            Toast.makeText(this, "User is not exist", Toast.LENGTH_SHORT).show();
        }else {
            GroupMemberListAdapter adapter = new GroupMemberListAdapter(this, filterUsers,groupModel);
            rvGroupMemberList.setAdapter(adapter);
            rvGroupMemberList.setLayoutManager(new LinearLayoutManager(this));
        }
        return true;
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


        public GroupMemberListAdapter(Context context, ArrayList<User> data,Groups groupModel){
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            this.data = data;
            this.groupModel = groupModel;
        }

        @Override
        public GroupMemberListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.group_member_add_item,parent,false);
            return new GroupMemberListViewHolder(v,groupModel);
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

        public GroupMemberListViewHolder(View itemView,Groups groupModel) {
            super(itemView);
            this.groupModel = groupModel;
            userName = (TextView) itemView.findViewById(R.id.tvUserName);
            profileImage = (CircularImageView)itemView.findViewById(R.id.cvProfile);
            btnAddMember = (BootstrapButton)itemView.findViewById(R.id.btnAddMember);

            btnAddMember.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //save data under MemberGroupList -> groupID -> owner of the group ID -> the user data
//            DatabaseReference refToGroupLists = FirebaseDatabase.getInstance()
//                    .getReference(Params.MEMBER_GROUP_LIST).child(groupModel.getGroupUID()).child(groupModel.getOwnerGroupUID());
//            refToGroupLists.push().setValue(userModel);

            //save data under MemberGroupList -> groupID ->owner of the group ID -> user id -> the user data
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.MEMBER_GROUP_LIST)
                    .child(groupModel.getGroupUID()).child(groupModel.getOwnerGroupUID())
                    .child(userModel.getUid());
            ref.setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    String name = userModel.getDisplayName();
                    Toast.makeText(userName.getContext(),"User" + name + " Has added" , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Member List Adapter
   public static class MemberAdapter extends FirebaseRecyclerAdapter<User,MemberListViewHolder> {
       private Context context;
       public MemberAdapter(Query query,Context context) {
           super(User.class, R.layout.group_member_item, MemberListViewHolder.class, query);
           this.context = context;
       }

       @Override
       protected void populateViewHolder(MemberListViewHolder viewHolder, User model, int position) {
            viewHolder.name.setText(model.getDisplayName());
            Glide.with(context).load(model.getDisplayName()).into(viewHolder.civImage);
       }
   }

    public static class MemberListViewHolder extends RecyclerView.ViewHolder {
            private TextView name;
            private CircularImageView civImage;
            private ImageButton btnDelete;

         public MemberListViewHolder(View itemView) {
             super(itemView);
             name = (TextView)itemView.findViewById(R.id.tvNameMember);
             civImage =(CircularImageView)itemView.findViewById(R.id.cvProfile);
             btnDelete = (ImageButton) itemView.findViewById(R.id.ibDeleteMember);
         }
     }
}

