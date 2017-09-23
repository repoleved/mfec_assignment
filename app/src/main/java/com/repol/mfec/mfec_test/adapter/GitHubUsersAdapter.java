package com.repol.mfec.mfec_test.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.repol.mfec.mfec_test.R;
import com.repol.mfec.mfec_test.model.GitHubUser;
import com.repol.mfec.mfec_test.mvp.MainPresenter;
import com.repol.mfec.mfec_test.util.ViewProvider;
import com.repol.mfec.mfec_test.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22/9/2560.
 */
public class GitHubUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private static final int VIEWTYPE_ITEM = 0;
    private static final int VIEWTYPE_LOAD = 1;

    private final Context context;
    private final MainPresenter presenter;
    private List<GitHubUser> gitHubUsers;
    private List<GitHubUser> gitHubUsersFilter;
    private List<GitHubUser> followingGitHubUsers;
    private LayoutInflater layoutInflater;
    private ViewUtil viewUtil;
    private int lastUserId = 0;

    public GitHubUsersAdapter(Context context, MainPresenter presenter, List<GitHubUser> gitHubUsers) {
        this.context = context;
        this.presenter = presenter;
        this.gitHubUsers = gitHubUsers;
        this.gitHubUsersFilter = gitHubUsers;
        this.layoutInflater = LayoutInflater.from(context);

        lastUserId = gitHubUsers.get(gitHubUsers.size() - 1).getId();
        viewUtil = new ViewProvider();
        gitHubUsers.add(null);
    }

    public int getLastUserId() {
        return lastUserId;
    }

    @Override
    public int getItemViewType(int position) {
        return gitHubUsersFilter.get(position) == null ? VIEWTYPE_LOAD : VIEWTYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_ITEM) {
            View view = layoutInflater.inflate(R.layout.listitem_github_user, parent, false);
            return new GitHubUserViewHolder(view);
        } else if (viewType == VIEWTYPE_LOAD) {
            View view = layoutInflater.inflate(R.layout.listitem_loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case VIEWTYPE_ITEM:
                final GitHubUser gitHubUser = gitHubUsersFilter.get(position);
                GitHubUserViewHolder gitHubUerViewHolder = (GitHubUserViewHolder) holder;

                Glide.with(context).load(gitHubUser.getAvatarUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(gitHubUerViewHolder.binding.ivUser);
                viewUtil.setViewElevation(context, gitHubUerViewHolder.binding.ivUser);

                gitHubUerViewHolder.binding.tbFollow.setOnCheckedChangeListener(null);

                gitHubUerViewHolder.binding.tvName.setText(gitHubUser.getLogin());
                gitHubUerViewHolder.binding.tbFollow.setChecked(gitHubUser.getFollowStatus() == 2);
                gitHubUerViewHolder.binding.tbFollow.setVisibility(gitHubUser.getFollowStatus() == 0 ? View.INVISIBLE : View.VISIBLE);
                gitHubUerViewHolder.binding.tbFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (gitHubUser.getFollowStatus() == 1)
                            presenter.following(position, gitHubUser.getLogin());
                        else
                            presenter.unfollowing(position, gitHubUser.getLogin());
                        gitHubUser.setFollowStatus(0);
                    }
                });
                gitHubUerViewHolder.binding.tbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                        compoundButton.setChecked(!isCheck);
                    }
                });
                gitHubUerViewHolder.binding.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.getUserProfile(position, gitHubUser);
                    }
                });
                break;
            case VIEWTYPE_LOAD:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.binding.pbLoading.setIndeterminate(true);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return gitHubUsersFilter.size();
    }

    public void setGitHubUsers(List<GitHubUser> gitHubUsers) {
        this.gitHubUsers = gitHubUsers;
        this.gitHubUsersFilter = gitHubUsers;
        notifyDataSetChanged();
    }

    public void addMoreUsers(List<GitHubUser> gitHubUsers) {
        this.gitHubUsers.remove(getItemCount() - 1);
        lastUserId = gitHubUsers.get(gitHubUsers.size() - 1).getId();
        int lastPositionIndex = getItemCount() - 1;
        this.gitHubUsers.addAll(gitHubUsers);
        this.gitHubUsers.add(null);
        this.gitHubUsersFilter = this.gitHubUsers;
        onLoadFollowing(followingGitHubUsers);
        notifyItemRangeChanged(lastPositionIndex, gitHubUsers.size());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();

                if (charString.isEmpty()) {
                    gitHubUsersFilter = gitHubUsers;
                } else {
                    List<GitHubUser> filteredList = new ArrayList<>();

                    for (GitHubUser gitHubUser : gitHubUsers) {
                        if (gitHubUser != null && gitHubUser.getLogin().toLowerCase().contains(charString)) {
                            filteredList.add(gitHubUser);
                        }
                    }

                    gitHubUsersFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = gitHubUsersFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                gitHubUsersFilter = (List<GitHubUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void onLoadFollowing(List<GitHubUser> gitHubUsers) {
        followingGitHubUsers = gitHubUsers;
        for (GitHubUser gitHubUser : this.gitHubUsers) {
            if (gitHubUser != null && gitHubUser.getFollowStatus() == 0) {
                gitHubUser.setFollowStatus(1);
                for (GitHubUser followGitHubUser : gitHubUsers) {
                    if (gitHubUser.getId() == followGitHubUser.getId()) {
                        gitHubUser.setFollowStatus(2);
                        break;
                    }
                }
            }
        }
        this.gitHubUsersFilter = this.gitHubUsers;
        notifyDataSetChanged();
    }

    public void setFollowing(int position) {
        GitHubUser gitHubUser = gitHubUsers.get(position);
        gitHubUser.setFollowStatus(2);
        this.gitHubUsersFilter = this.gitHubUsers;
        notifyItemChanged(position);
    }

    public void setUnfollowing(int position) {
        GitHubUser gitHubUser = gitHubUsers.get(position);
        gitHubUser.setFollowStatus(1);
        this.gitHubUsersFilter = this.gitHubUsers;
        notifyItemChanged(position);
    }

    public void setGitHubUser(int position, GitHubUser gitHubUser) {
        gitHubUsers.set(position, gitHubUser);
        this.gitHubUsersFilter = this.gitHubUsers;
        notifyItemChanged(position);
    }
}
