package com.world.bolandian.whereareyou;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.world.bolandian.whereareyou.models.Groups;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {
        private RecyclerView rvGroup;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        rvGroup = (RecyclerView)view.findViewById(R.id.rvGroup);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null)return view;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GroupUsers").child(user.getUid());
        GroupsAdapter adapter = new GroupsAdapter(ref,this);
        rvGroup.setAdapter(adapter);
        rvGroup.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    public static class GroupsAdapter extends FirebaseRecyclerAdapter<Groups,GroupListViewHolder>  {
           private Fragment fragment;

        public GroupsAdapter(Query query,Fragment fragment) {
            super(Groups.class, R.layout.group_item, GroupListViewHolder.class, query);
            this.fragment = fragment;
        }

        @Override
        protected void populateViewHolder(GroupListViewHolder viewHolder, Groups model, int position) {
            viewHolder.groupName.setText(model.getGroupName());

        }
    }

    public static class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView groupName;
        private FloatingActionButton fabAdd;
        private Context fragment;

        public GroupListViewHolder(View itemView) {
            super(itemView);
            this.fragment = itemView.getContext();
            groupName = (TextView) itemView.findViewById(R.id.groupName);
            fabAdd = (FloatingActionButton) itemView.findViewById(R.id.fabAdd);

            fabAdd.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            //   LayoutInflater.from(GroupsFragment.this).inflate();
            //    AlertDialog.Builder alertDialog = new AlertDialog.Builder(fragment.getContext());
            if(view == fabAdd)
            Toast.makeText(fragment, "chrcked!", Toast.LENGTH_SHORT).show();

//            EditText editText = new EditText(fragment.getContext());
//            AlertDialog.Builder dialog = new AlertDialog.Builder(fragment.getContext());
//            dialog.setTitle("Group name");
//            dialog.setMessage("Please enter the group name");
//            dialog.setView(R.layout.dialog_create_group);
//            dialog.show();


        }
    }

}
