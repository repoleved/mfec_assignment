package com.repol.mfec.mfec_test.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.repol.mfec.mfec_test.R;
import com.repol.mfec.mfec_test.databinding.ListitemLoadingBinding;

/**
 * Created on 22/9/2560.
 */
public class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ListitemLoadingBinding binding;

    public LoadingViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}
