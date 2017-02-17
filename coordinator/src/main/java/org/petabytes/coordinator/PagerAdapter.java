package org.petabytes.coordinator;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PagerAdapter<T> extends android.support.v4.view.PagerAdapter {

    private final List<T> items;
    private PagerFactory factory;

    public PagerAdapter(@NonNull List<T> items, @NonNull PagerFactory factory) {
        this.items = items;
        this.factory = factory;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = factory.create(items.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}