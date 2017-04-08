package org.petabytes.coordinator;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public interface OnBindViewHolderListener<T> {

        void onBindViewHolder(T data, int position);
    }

    private final List<T> items;
    private final Supplier<ViewHolder> viewHolderSupplier;

    public RecyclerAdapter(@NonNull Supplier<RecyclerAdapter.ViewHolder> viewHolderSupplier) {
        this.items = new ArrayList<>();
        this.viewHolderSupplier = viewHolderSupplier;
    }

    public RecyclerAdapter(@NonNull List<T> items, @NonNull Supplier<RecyclerAdapter.ViewHolder> viewHolderSupplier) {
        this.items = items;
        this.viewHolderSupplier = viewHolderSupplier;
    }

    public void setItems(@NonNull List<T> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolderSupplier.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder<T> extends RecyclerView.ViewHolder {

        private final OnBindViewHolderListener<T> listener;

        public ViewHolder(@NonNull View itemView, @NonNull OnBindViewHolderListener<T> listener) {
            super(itemView);
            this.listener = listener;
            ButterKnife.bind(this, itemView);
        }

        protected void bind(@NonNull T t, int position) {
            listener.onBindViewHolder(t, position);
        }
    }
}
