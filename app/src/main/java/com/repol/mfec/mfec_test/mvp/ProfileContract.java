package com.repol.mfec.mfec_test.mvp;

import com.repol.mfec.mfec_test.model.GitHubUser;

/**
 * Created on 23/9/2560.
 */

public class ProfileContract {
    interface View {
        void onLoadProfile(GitHubUser result);

        void onFollowUser();

        void onUnfollowUser();

        void onError();
    }

    interface Presenter {
        void getUserProfile(String username, int followingStatus);

        void following(String username);

        void unfollowing(String username);

        void destroy();
    }
}
