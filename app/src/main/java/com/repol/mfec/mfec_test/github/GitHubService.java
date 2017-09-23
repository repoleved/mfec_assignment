package com.repol.mfec.mfec_test.github;

import com.repol.mfec.mfec_test.model.GitHubUser;
import com.repol.mfec.mfec_test.model.GitHubUserProfile;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created on 21/9/2560.
 */
public interface GitHubService {
    @GET("/users")
    Observable<List<GitHubUser>> getUsers(@Query("since") int lastUserId);

    @GET("/users/{username}")
    Observable<GitHubUser> getUserProfile(@Path("username") String username);

    @GET("/user/following")
    Observable<List<GitHubUser>> getFollowing();

    @PUT("/user/following/{username}")
    Observable<Response<Void>> followUser(@Path("username") String username);

    @DELETE("/user/following/{username}")
    Observable<Response<Void>> unfollowUser(@Path("username") String username);
}
