package org.petabytes.awesomeblogs.auth;

import android.support.annotation.NonNull;

import org.petabytes.awesomeblogs.R;
import org.petabytes.coordinator.Coordinator;

import butterknife.OnClick;
import rx.functions.Action0;

class SignInCoordinator extends Coordinator {

    private final Action0 onSignInAction;

    SignInCoordinator(@NonNull Action0 onSignInAction) {
        this.onSignInAction = onSignInAction;
    }

    @OnClick(R.id.sign_in)
    void onSignInClick() {
        onSignInAction.call();
    }
}
