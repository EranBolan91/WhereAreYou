package com.world.bolandian.whereareyou;

import android.content.SharedPreferences;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Bolandian on 05/10/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences prefsToken = getSharedPreferences(Params.IDToken, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefsToken.edit();
        edit.putString("token",token);
        edit.commit();

        sendRegistrationToServer(token);
    }
        //TODO: check how to save the refresh token
    private void sendRegistrationToServer(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Task<Void> ref = FirebaseDatabase.getInstance().getReference(Params.USERS).child(user.getUid()).child("token")
                .setValue(token);
    }
}
