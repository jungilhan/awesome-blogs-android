package org.petabytes.awesomeblogs.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.squareup.coordinators.Coordinators;

import org.petabytes.awesomeblogs.R;

import rx.functions.Action1;

public class BillingDialog extends DialogFragment {

    private BillingProcessor billingProcessor;
    private Action1<SkuDetails> purchaseAction;

    public BillingDialog setBillingProcessor(@NonNull BillingProcessor billingProcessor) {
        this.billingProcessor = billingProcessor;
        return this;
    }

    public BillingDialog setPurchaseAction(@NonNull Action1<SkuDetails> purchaseAction) {
        this.purchaseAction = purchaseAction;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_billing, null, false);
        Coordinators.bind(view, $ -> new BillingDialogCoordinator(getContext(), billingProcessor, purchaseAction, this::dismiss));
        return builder
            .setView(view)
            .create();
    }
}
