package com.repol.mfec.mfec_test.mvp;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.repol.mfec.mfec_test.R;
import com.repol.mfec.mfec_test.adapter.GitHubUsersAdapter;
import com.repol.mfec.mfec_test.databinding.ActivityMainBinding;
import com.repol.mfec.mfec_test.github.GitHubProvider;
import com.repol.mfec.mfec_test.model.GitHubUser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View {
    public static final int REQUEST_PROFILE_ACTIVITY = 1000;
    public static final String ARGS_USER_OBJECT = "user_object";
    public static final String ARGS_POSITION = "user_object_position";

    private ActivityMainBinding binding;
    private MainPresenter presenter;

    private boolean isLoading = false;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private GitHubUsersAdapter gitHubUsersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        presenter = new MainPresenter(this, GitHubProvider.getGitHubService());
        presenter.getGitHubUsers(0);

        initToolBar();
        initRecyclerView();
        initSwipeToRefreshView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PROFILE_ACTIVITY && resultCode == Activity.RESULT_OK) {
            int position = data.getIntExtra(ARGS_POSITION, 0);
            GitHubUser gitHubUser = data.getParcelableExtra(ARGS_USER_OBJECT);
            gitHubUsersAdapter.setGitHubUser(position, gitHubUser);
        }
    }

    private void initSwipeToRefreshView() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    isLoading = true;
                    presenter.getGitHubUsers(0);
                } else {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void initToolBar() {
        binding.toolbar.setTitle(R.string.main_title);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MainActivity.this, linearLayoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (presenter != null) {
                        presenter.getGitHubUsers(gitHubUsersAdapter.getLastUserId());
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                gitHubUsersAdapter.getFilter().filter(query);
                isLoading = true;
                return true;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isLoading = false;
                return false;
            }
        });

        return true;
    }

    @Override
    public void onUsersLoaded(List<GitHubUser> gitHubUsers) {
        if (gitHubUsersAdapter == null) {
            gitHubUsersAdapter = new GitHubUsersAdapter(MainActivity.this, presenter, gitHubUsers);
            binding.recyclerView.setAdapter(gitHubUsersAdapter);
        } else {
            gitHubUsersAdapter.setGitHubUsers(gitHubUsers);
        }
        isLoading = false;
        binding.swipeRefreshLayout.setRefreshing(isLoading);
    }

    @Override
    public void onLoadMoreUser(List<GitHubUser> gitHubUsers) {
        gitHubUsersAdapter.addMoreUsers(gitHubUsers);
        isLoading = false;
    }

    @Override
    public void onLoadFollowing(List<GitHubUser> gitHubUsers) {
        gitHubUsersAdapter.onLoadFollowing(gitHubUsers);
    }

    @Override
    public void onFollowUser(int position) {
        gitHubUsersAdapter.setFollowing(position);
    }

    @Override
    public void onUnfollowUser(int position) {
        gitHubUsersAdapter.setUnfollowing(position);
    }

    @Override
    public void openProfilePage(int position, GitHubUser gitHubUser) {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra(ARGS_POSITION, position);
        intent.putExtra(ARGS_USER_OBJECT, gitHubUser);
        startActivityForResult(intent, REQUEST_PROFILE_ACTIVITY);
    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        presenter = null;
        super.onDestroy();
    }
}
