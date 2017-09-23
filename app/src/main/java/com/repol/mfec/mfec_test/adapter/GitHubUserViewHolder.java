package com.repol.mfec.mfec_test.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.repol.mfec.mfec_test.databinding.ListitemGithubUserBinding;

/**
 * Created on 22/9/2560.
 */
public class GitHubUserViewHolder extends RecyclerView.ViewHolder {
    public ListitemGithubUserBinding binding;

    public GitHubUserViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }
}
