package org.petabytes.awesomeblogs.auth;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import org.petabytes.awesomeblogs.util.Strings;

public class User {

    private final String id;
    private final String name;
    private final Uri photoUri;
    private final String email;

    public static User of(@NonNull FirebaseUser user) {
        return new User(user.getUid(),
            user.getDisplayName() != null ? user.getDisplayName() : Strings.EMPTY,
            user.getPhotoUrl() != null ? user.getPhotoUrl() : Uri.EMPTY,
            user.getEmail() != null ? user.getEmail() : Strings.EMPTY);
    }

    private User(@NonNull String id, @NonNull String name, @NonNull Uri photoUri, @NonNull String email) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", photoUri=" + photoUri +
            ", email='" + email + '\'' +
            '}';
    }
}
