package com.world.bolandian.whereareyou;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.world.bolandian.whereareyou.models.Groups;
import com.world.bolandian.whereareyou.models.User;

import java.util.ArrayList;

public class GroupMemberActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
                private RecyclerView rvGroupMemberList;
                private GroupMemberListAdapter adapter;
                private User userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvGroupMemberList = (RecyclerView)findViewById(R.id.rvGroupMemberList);

        Groups model = getIntent().getExtras().getParcelable("model");
        if(model.getGroupName() != null) {
            setTitle(model.getGroupName());
        }else {
            setTitle("");
        }
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


    @Override
    public boolean onQueryTextSubmit(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        Query q = ref.child(userModel.getUid()).orderByChild("displayName").equalTo(query);
        setAdapter(q);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

//        newText = newText.toLowerCase();
//        ArrayList<User> newUserList = new ArrayList<>();
//        for(User user : newUserList){
//            String name = user.getDisplayName().toLowerCase();
//            if(name.contains(newText))
//                newUserList.add(user);
//        }
//        //TODO: try to resolve the problem of finding user, it shows all the list
//        adapter.setFiler(newUserList);
        return true;
    }


    private void setAdapter(Query query) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.orderByChild("displayName").equalTo("Eran Bolandian");
        adapter = new GroupMemberListAdapter(query,this);
        rvGroupMemberList.setAdapter(adapter);
        rvGroupMemberList.setLayoutManager(new LinearLayoutManager(this));
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





    public static class GroupMemberListAdapter extends FirebaseRecyclerAdapter<User,GroupMemberListViewHolder> {
            private Context context;

        public GroupMemberListAdapter(Query query, Context context) {
            super(User.class, R.layout.group_member_add_item, GroupMemberListViewHolder.class, query);
            this.context = context;
        }

        @Override
        protected void populateViewHolder(GroupMemberListViewHolder viewHolder, User model, int position) {
            viewHolder.userName.setText(model.getDisplayName());
            Glide.with(context).load(model.getProfileImage()).into(viewHolder.profileImage);
            viewHolder.model = model;
        }

        public void setFiler(ArrayList<User> users){
            ArrayList<User> user = new ArrayList<>();
            user.addAll(users);
            notifyDataSetChanged();
        }

    }

    public static class GroupMemberListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView userName;
            private CircularImageView profileImage;
            private BootstrapButton btnAddMember;
            private User model;

        public GroupMemberListViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tvUserName);
            profileImage = (CircularImageView)itemView.findViewById(R.id.cvProfile);
            btnAddMember = (BootstrapButton)itemView.findViewById(R.id.btnAddMember);

            btnAddMember.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String name = model.getDisplayName();
            Toast.makeText(userName.getContext(), name , Toast.LENGTH_SHORT).show();
        }
    }
}
