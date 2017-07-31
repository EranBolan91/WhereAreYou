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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.world.bolandian.whereareyou.models.Groups;
import com.world.bolandian.whereareyou.models.User;

public class GroupMemberActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
                private RecyclerView rvGroupMemberList;

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        GroupMemberListAdapter adapter = new GroupMemberListAdapter(ref,this);
        rvGroupMemberList.setAdapter(adapter);
        rvGroupMemberList.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_member, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        //this fires up the onQueryTextSubmit method
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        //if it works then delete these lines
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);


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


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
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
            //TODO: find why the model is not invoke the data from database - its gets null
        }
    }

    public static class GroupMemberListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView userName;
            private CircularImageView profileImage;
            private BootstrapButton btnAddMember;

        public GroupMemberListViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.tvUserName);
            profileImage = (CircularImageView)itemView.findViewById(R.id.cvProfile);
            btnAddMember = (BootstrapButton)itemView.findViewById(R.id.btnAddMember);

            btnAddMember.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(userName.getContext(), "hello", Toast.LENGTH_SHORT).show();
        }
    }
}
