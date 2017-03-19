package org.petabytes.awesomeblogs.auth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jakewharton.rxrelay.BehaviorRelay;

import org.petabytes.awesomeblogs.util.Preferences;

import hugo.weaving.DebugLog;
import rx.Observable;

public class Authenticator {

    private final BehaviorRelay<Optional<User>> userRelay;
    private final FirebaseAuth firebaseAuth;

    public Authenticator() {
        userRelay = BehaviorRelay.create();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(auth ->
            userRelay.call((auth != null && auth.getCurrentUser() != null)
                ? Optional.of(User.of(auth.getCurrentUser())) : Optional.empty()));

    }

    public Observable<Optional<User>> user() {
        return userRelay;
    }

    public Observable<Boolean> isSignIn() {
        return userRelay.map(Optional::isPresent);
    }

    public Observable<Optional<User>> signIn(@NonNull Context context) {
        context.startActivity(SignInActivity.intent(context));
        return userRelay.skip(1);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    @DebugLog
    void signInWithGoogle(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null));
            Preferences.accessToken().set(account.getIdToken());
        } else {
            userRelay.call(Optional.empty());
        }
    }
}
