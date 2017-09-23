package com.repol.mfec.mfec_test.mvp;

import com.repol.mfec.mfec_test.model.GitHubUser;

import java.util.List;

/**
 * Created on 21/9/2560.
 */
public interface MainContract {
    interface View {
        void onUsersLoaded(List<GitHubUser> gitHubUsers);

        void onLoadMoreUser(List<GitHubUser> gitHubUsers);

        void onLoadFollowing(List<GitHubUser> gitHubUsers);

        void onFollowUser(int position);

        void onUnfollowUser(int position);

        void openProfilePage(int position, GitHubUser gitHubUser);

        void onError();
    }

    interface Presenter {
        void getGitHubUsers(int startFromId);

        void destroy();

        void getFollowing();

        void following(int position, String username);

        void unfollowing(int position, String username);

        void getUserProfile(int position, GitHubUser gitHubUser);
    }
}
