package com.repol.mfec.mfec_test.mvp;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.repol.mfec.mfec_test.R;
import com.repol.mfec.mfec_test.databinding.ActivityProfileBinding;
import com.repol.mfec.mfec_test.github.GitHubProvider;
import com.repol.mfec.mfec_test.model.GitHubUser;
import com.repol.mfec.mfec_test.util.ViewProvider;
import com.repol.mfec.mfec_test.util.ViewUtil;

/**
 * Created on 23/9/2560.
 */
public class ProfileActivity extends AppCompatActivity implements ProfileContract.View {
    private static final String FORMATTER_AT_SIGN = "@%s";

    private ActivityProfileBinding binding;
    private int position;
    private GitHubUser gitHubUser;
    private ProfilePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ProfileActivity.this, R.layout.activity_profile);

        position = getIntent().getIntExtra(MainActivity.ARGS_POSITION, 0);
        gitHubUser = getIntent().getParcelableExtra(MainActivity.ARGS_USER_OBJECT);
        presenter = new ProfilePresenter(this, GitHubProvider.getGitHubService());

        presenter.getUserProfile(gitHubUser.getLogin(), gitHubUser.getFollowStatus());
        initToolBar();
        initView();
        setProfile();
    }

    private void initView() {
        ViewUtil viewUtil = new ViewProvider();
        viewUtil.setViewElevation(ProfileActivity.this, binding.ivUser);
        binding.tbFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gitHubUser.getFollowStatus() == 1)
                    presenter.following(gitHubUser.getLogin());
                else
                    presenter.unfollowing(gitHubUser.getLogin());
            }
        });
    }

    private void setProfile() {
        binding.tbFollow.setOnCheckedChangeListener(null);

        Glide.with(ProfileActivity.this).load(gitHubUser.getAvatarUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(binding.ivUser);
        binding.tvName.setText(gitHubUser.getName());
        binding.tvLogin.setText(String.format(FORMATTER_AT_SIGN, gitHubUser.getLogin()));
        binding.tvFollower.setText(gitHubUser.getFollowers());
        binding.tvFollowing.setText(gitHubUser.getFollowing());
        binding.tbFollow.setChecked(gitHubUser.getFollowStatus() == 2);
        binding.tvBio.setText(gitHubUser.getBio());
        binding.tvLocation.setText(gitHubUser.getLocation());
        binding.tvUrl.setText(gitHubUser.getUrl());

        binding.tbFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                compoundButton.setChecked(!isCheck);
            }
        });
    }

    private void initToolBar() {
        binding.toolbar.setTitle(gitHubUser.getLogin());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onLoadProfile(GitHubUser result) {
        this.gitHubUser = result;
        setProfile();
    }

    @Override
    public void onFollowUser() {
        gitHubUser.setFollowStatus(2);
        setProfile();
    }

    @Override
    public void onUnfollowUser() {
        gitHubUser.setFollowStatus(1);
        setProfile();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putExtra(MainActivity.ARGS_USER_OBJECT, gitHubUser);
        setResult(Activity.RESULT_OK, intent);

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        presenter = null;
        super.onDestroy();
    }
}
