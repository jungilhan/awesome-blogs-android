package org.petabytes.awesomeblogs.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.annimon.stream.Stream;
import com.f2prateek.rx.preferences.Preference;

import org.petabytes.awesomeblogs.BuildConfig;
import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Alerts;
import org.petabytes.awesomeblogs.util.Analytics;
import org.petabytes.awesomeblogs.util.Intents;
import org.petabytes.awesomeblogs.util.Preferences;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.awesomeblogs.util.Xors;
import org.petabytes.coordinator.Coordinator;

import butterknife.BindView;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import rx.functions.Action0;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ERROR;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE;
import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;

class SettingsCoordinator extends Coordinator implements BillingProcessor.IBillingHandler {

    @BindView(R.id.digest_switch) SwitchCompat digestSwitch;
    @BindView(R.id.silent_switch) SwitchCompat silentSwitch;
    @BindView(R.id.version) TextView versionView;
    @BindView(R.id.donate) View donateView;

    private final Context context;
    private final Action0 closeAction;
    private final Preference<Boolean> digestPreference;
    private final Preference<Boolean> silentPreference;
    private final BillingProcessor billingProcessor;

    SettingsCoordinator(@NonNull Context context, @NonNull Action0 closeAction) {
        this.context = context;
        this.closeAction = closeAction;
        this.digestPreference = Preferences.digest();
        this.silentPreference = Preferences.silent();
        this.billingProcessor = BillingProcessor.newBillingProcessor(context, Xors.decode("dXEsdX0IcHZ6AlxFClpRf1xABCBwaX0jdnUtcnlp" +
            "XXZ5K3h6ewJ8dyNgfXkIcnsmB0BqFG9iFklq\nbRB5YDZIYn" +
            "IBen0DSFJdTnZEBgdvTCpVQBtgbnE8ZXMISXpeI19MKXRxDT" +
            "JnfA9ydXM0YF4NGmto\nIkQGDERJbh9eBy4HWVQjVFVNQWhu" +
            "PEVXO0ATbiRCZgR8fGIOc2QAZ2hbHwFRUFxzDhRybTJ4Uwgh" +
            "\nTUUoaAh1A2YFI15tTQhkQyR3U2EwAUZTCVATEVVjGGZ8UT" +
            "RlbQgGWm8DZ0RVZGwJSgMDD0QLViRA\nZU0edQoBHH8JSXB7" +
            "A04HEQEAaBJiBBsEE0gKZWQlaUFwPwdABVp0Qh1AfxRbaWw2" +
            "c1E6aU5OMW9t\nF0MOe1dtczJCVV4ABFY7BkphSg9iBFVQDz" +
            "ZOBzFEX3YTVXIGAFZyABhnBXZiADdnRzVWSFEsU1YP\nW0tM" +
            "VF1COB5zDQgAWlADWQhcWlYjUltiEVJ6KVV3TVR9cy8BDn4jAW4qVU9xIXZlI3M=\n", "88e74b1"), this);
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        bind(digestPreference.asObservable()
            .doOnNext($ -> digestSwitch.animate().setStartDelay(100).alpha(1)), digestSwitch::setChecked);
        bind(silentPreference.asObservable()
            .doOnNext($ -> silentSwitch.animate().setStartDelay(100).alpha(1)), silentSwitch::setChecked);
        versionView.setText(context.getString(R.string.settings_version, BuildConfig.VERSION_NAME));

        billingProcessor.initialize();
        Views.setVisibleOrGone(donateView, isIabAvailable(context));

        Analytics.event(Analytics.Event.VIEW_SETTINGS);
    }

    @Override
    public void detach(@NonNull View view) {
        if (billingProcessor != null) {
            billingProcessor.release();
        }
        super.detach(view);
    }

    @DebugLog
    boolean onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        return billingProcessor.handleActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        closeAction.call();
    }

    @OnClick({R.id.digest, R.id.digest_switch})
    void onDigestClick() {
        digestPreference.set(!digestPreference.get());
        Analytics.event(Analytics.Event.SETTINGS_DIGEST,
            Analytics.Param.ENABLED, String.valueOf(digestPreference.get()));
    }

    @OnClick({R.id.silent, R.id.silent_switch})
    void onSilentClick() {
        silentPreference.set(!silentPreference.get());
        Analytics.event(Analytics.Event.SETTINGS_SILENT,
            Analytics.Param.ENABLED, String.valueOf(silentPreference.get()));
    }

    @OnClick(R.id.store)
    void onStoreClick() {
        context.startActivity(Intents.createStoreIntent());
    }

    @OnClick(R.id.github)
    void onGitHubClick() {
        context.startActivity(Intents.createGitHubIntent());
    }

    @OnClick(R.id.donate)
    void onDonateClick() {
        new BillingDialog()
            .setBillingProcessor(billingProcessor)
            .setPurchaseAction(detail -> billingProcessor.purchase((Activity) context, detail.productId))
            .show(((FragmentActivity) context).getSupportFragmentManager(), "BillingDialog");
    }

    @DebugLog
    @Override
    public void onBillingInitialized() {
        Stream.of(billingProcessor.listOwnedProducts())
            .forEach(billingProcessor::consumePurchase);
    }

    @DebugLog
    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        billingProcessor.consumePurchase(productId);
    }

    @DebugLog
    @Override
    public void onPurchaseHistoryRestored() {
        
    }

    @DebugLog
    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        switch (errorCode) {
            case BILLING_RESPONSE_RESULT_USER_CANCELED:
                break;
            case BILLING_RESPONSE_RESULT_SERVICE_UNAVAILABLE:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_2);
                break;
            case BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_3);
                break;
            case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_4);
                break;
            case BILLING_RESPONSE_RESULT_DEVELOPER_ERROR:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_5);
                break;
            case BILLING_RESPONSE_RESULT_ERROR:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_6);
                break;
            case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_7);
                break;
            case BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_8);
                break;
            default:
                Alerts.show((Activity) context, R.string.error_title, R.string.error_billing_1);
                break;
        }
    }

    public static boolean isIabAvailable(@NonNull Context context) {
        return BuildConfig.APPLICATION_ID.equals("org.petabytes.awesomeblogs") && BillingProcessor.isIabServiceAvailable(context);
    }
}