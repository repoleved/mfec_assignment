package com.repol.mfec.mfec_test.mvp;

import com.repol.mfec.mfec_test.github.GitHubService;
import com.repol.mfec.mfec_test.model.GitHubUser;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created on 23/9/2560.
 */
public class ProfilePresenter implements ProfileContract.Presenter {
    private ProfileContract.View view;
    private GitHubService gitHubService;
    private CompositeDisposable compositeDisposable;

    public ProfilePresenter(ProfileContract.View view, GitHubService gitHubService) {
        this.view = view;
        this.gitHubService = gitHubService;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getUserProfile(String username, final int followingStatus) {
        Disposable disposable = gitHubService.getUserProfile(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GitHubUser>() {
                    @Override
                    public void onNext(GitHubUser result) {
                        result.setFollowStatus(followingStatus);
                        view.onLoadProfile(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.onError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void following(String username) {
        Disposable disposable = gitHubService.followUser(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(@NonNull Response<Void> result) {
                        view.onFollowUser();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.onError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void unfollowing(String username) {
        Disposable disposable = gitHubService.unfollowUser(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(@NonNull Response<Void> result) {
                        view.onUnfollowUser();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.onError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void destroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }
}
