package com.world.bolandian.whereareyou;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.world.bolandian.whereareyou.models.Groups;
import com.world.bolandian.whereareyou.models.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment implements View.OnClickListener {
        private RecyclerView rvGroup;
        private FloatingActionButton fabAdd;
        private String groupName;
        private DatabaseReference ref;
        private FirebaseUser currentUser;
        private ProgressBar progressBar;


    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        rvGroup = (RecyclerView)view.findViewById(R.id.rvGroup);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        fabAdd = (FloatingActionButton)view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null)return view;
        //start the progress bar
        progressBar.setVisibility(View.VISIBLE);

         ref = FirebaseDatabase.getInstance().getReference(Params.GROUP_LISTS).child(currentUser.getUid());
        GroupsAdapter adapter = new GroupsAdapter(ref,this,progressBar);
        Log.d("checkAdapter",adapter.toString());Log.d("refe",ref.toString());
        rvGroup.setAdapter(adapter);
        rvGroup.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onClick(View view) {
           final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
           View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_create_group,null);
           final EditText groupNameInput = (EditText)viewDialog.findViewById(R.id.etGroupName);

            dialog.setTitle("Group name");
            dialog.setIcon(R.drawable.ic_group);
            dialog.setCancelable(true);
            dialog.setMessage("Please enter the group name");
            dialog.setView(viewDialog);


        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 groupName = groupNameInput.getText().toString();
                if(groupName.isEmpty()){
                    groupNameInput.requestFocus();
                    groupNameInput.setError("Please fill the text");
                }else{
                    addGroup(groupName);
                    alertDialog.dismiss();
                }
            }
        });
    }

    private Groups model;
    private void addGroup(String nameGroup) {

        ref = FirebaseDatabase.getInstance().getReference(Params.GROUP_LISTS).child(currentUser.getUid());
        DatabaseReference row = ref.push();
        String groupID = row.getKey();
        model = new Groups(currentUser.getUid(),groupID,nameGroup);

        row.setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //TODO: add task.isSuccsseful and else - if there is a problem
                addOwnerToTheGroup(model);
                Toast.makeText(getContext(), "Group has added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOwnerToTheGroup(Groups groupModel) {
        User user = new User(currentUser);
             DatabaseReference refToMemeberGroup = FirebaseDatabase.getInstance()
            .getReference(Params.MEMBER_GROUP_LIST).child(groupModel.getGroupUID())
            .child(groupModel.getOwnerGroupUID());
             refToMemeberGroup.push().setValue(user);
    }


    public static class GroupsAdapter extends FirebaseRecyclerAdapter<Groups,GroupListViewHolder>  {
           private Fragment fragment;
           private ProgressBar progressBar;

        public GroupsAdapter(Query query,Fragment fragment,ProgressBar progressBar) {
            super(Groups.class, R.layout.group_item, GroupListViewHolder.class, query);
            this.fragment = fragment;
            this.progressBar = progressBar;
        }

        @Override
        protected void populateViewHolder(GroupListViewHolder viewHolder, Groups model, int position) {
            viewHolder.groupName.setText(model.getGroupName());

            viewHolder.model = model;
        }

        @Override
        public GroupListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(viewType,parent,false);
            return new GroupListViewHolder(view,fragment,progressBar);
        }
    }

    public static class GroupListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView groupName;
        private BootstrapButton btnDeleteGroup;
        private Fragment fragment;
        private Groups model;

        public GroupListViewHolder(View itemView,Fragment fragment,ProgressBar progressBar) {
            super(itemView);
            this.fragment = fragment;
            progressBar.setVisibility(View.INVISIBLE);
            groupName = (TextView) itemView.findViewById(R.id.groupName);
            btnDeleteGroup =(BootstrapButton) itemView.findViewById(R.id.btnDeleteGroup);

            itemView.setOnClickListener(this);
            btnDeleteGroup.setOnClickListener(this);
        }

        //Delete a group VIA dialog alert
        @Override
        public void onClick(View view) {
            if(view == btnDeleteGroup){
                final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(btnDeleteGroup.getContext());

                dialogDelete.setIcon(R.drawable.ic_warning);
                dialogDelete.setTitle("Removing group:  " + model.getGroupName());
                dialogDelete.setCancelable(true);
                dialogDelete.setMessage("Are you sure you want to remove?");

                dialogDelete.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        //Delete group from firebase
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Params.GROUP_LISTS).child(model.getOwnerGroupUID());
                        ref.child(model.getGroupUID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(btnDeleteGroup.getContext(),model.getGroupName() + " Deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(btnDeleteGroup.getContext(), "some problem has occurred, please try again", Toast.LENGTH_SHORT).show();
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

            }else { // the else fires up when the user tape on the view, when remove button is not tapped
                    //this open an activity and transfer the data of the group that was tapped
                //TODO: jump to groupMemberFragment and transfer the data
                Intent i = new Intent(fragment.getContext(), GroupMemberActivity.class);
                i.putExtra("model", model);
                fragment.getActivity().startActivity(i);
            }
        }
    }

}
