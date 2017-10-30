package com.world.bolandian.whereareyou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.world.bolandian.whereareyou.MainActivity;
import com.world.bolandian.whereareyou.R;
import com.world.bolandian.whereareyou.fragments.InvitationsFragment;
import com.world.bolandian.whereareyou.fragments.RequestsFragment;

public class InboxActivity extends AppCompatActivity {
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                int position = tab.getPosition();
                //requests = position - 0
                //invitations = position - 1
                switch (position){
                    case 0:
                        fragment = new RequestsFragment();
                        break;
                    case 1:
                        fragment = new InvitationsFragment();
                        break;
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.containerInbox, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(InboxActivity.this,MainActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

}
