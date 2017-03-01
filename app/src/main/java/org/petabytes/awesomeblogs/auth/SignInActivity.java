package org.petabytes.awesomeblogs.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.petabytes.awesomeblogs.AwesomeBlogsApp;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.base.AwesomeActivity;
import org.petabytes.awesomeblogs.util.Alerts;
import org.petabytes.coordinator.ActivityGraph;

import hugo.weaving.DebugLog;

public class SignInActivity extends AwesomeActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN = 1000;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build())
            .build();
    }

    @Override
    protected ActivityGraph createActivityGraph() {
        return new ActivityGraph.Builder()
            .layoutResId(R.layout.sign_in)
            .coordinator(R.id.sign_in, new SignInCoordinator(() ->
                startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient), SIGN_IN)))
            .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            AwesomeBlogsApp.get().authenticator()
                .signInWithGoogle(result.isSuccess() ? result.getSignInAccount() : null);

            if (!result.isSuccess()) {
                Alerts.show(this, R.string.error_title, R.string.error_sign_in);
            }
        }
    }


    @DebugLog
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Alerts.show(this, R.string.error_title, R.string.error_sign_in);
    }

    public static Intent intent(@NonNull Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }
}
