package org.petabytes.awesomeblogs.settings;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.annimon.stream.Optional;

import org.petabytes.awesomeblogs.R;
import org.petabytes.awesomeblogs.util.Strings;
import org.petabytes.awesomeblogs.util.Views;
import org.petabytes.coordinator.Coordinator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;
import rx.functions.Action1;

class BillingDialogCoordinator extends Coordinator {

    @BindView(R.id.items) LinearLayoutCompat itemsView;
    @BindView(R.id.empty) View emptyView;

    private final Context context;
    private final BillingProcessor billingProcessor;
    private final Action1<SkuDetails> purchaseAction;
    private final Action0 cancelAction;

    BillingDialogCoordinator(@NonNull Context context, @NonNull BillingProcessor billingProcessor,
                             @NonNull Action1<SkuDetails> purchaseAction,
                             @NonNull Action0 cancelAction) {
        this.context = context;
        this.billingProcessor = billingProcessor;
        this.purchaseAction = purchaseAction;
        this.cancelAction = cancelAction;
    }

    @Override
    public void attach(@NonNull View view) {
        super.attach(view);
        ArrayList<String> items = new ArrayList<>(Arrays.asList("donation_1", "donation_2", "donation_3"));
        Bundle query = new Bundle();
        query.putStringArrayList("ITEM_ID_LIST", items);
        Optional<List<SkuDetails>> details = Optional.ofNullable(billingProcessor.getPurchaseListingDetails(items));
        details.executeIfAbsent(cancelAction::call)
            .ifPresent(d -> {
                for (int i = d.size() - 1; i >= 0; i--) {
                    View itemView = LayoutInflater.from(context).inflate(R.layout.dialog_billing_item, null, false);
                    ImageView iconView = ButterKnife.findById(itemView, R.id.icon);
                    TextView titleView = ButterKnife.findById(itemView, R.id.title);
                    TextView priceView = ButterKnife.findById(itemView, R.id.price);
                    SkuDetails detail = d.get(i);
                    switch (i) {
                        case 0: iconView.setImageResource(R.drawable.coffee_1); break;
                        case 1: iconView.setImageResource(R.drawable.coffee_2); break;
                        case 2: iconView.setImageResource(R.drawable.coffee_3); break;
                    }
                    titleView.setText(detail.title.replace("(어썸블로그)", Strings.EMPTY));
                    titleView.setTypeface(titleView.getTypeface(), Typeface.BOLD);
                    priceView.setText(detail.priceText);
                    priceView.setTypeface(priceView.getTypeface(), Typeface.BOLD);
                    itemView.setOnClickListener($ -> {
                        purchaseAction.call(detail);
                        cancelAction.call();
                    });
                    itemsView.addView(itemView, 0);
                }
            });
        Views.setVisibleOrGone(emptyView, details.isPresent());
    }

    @OnClick(R.id.cancel)
    void onCancelClick() {
        cancelAction.call();
    }
}
