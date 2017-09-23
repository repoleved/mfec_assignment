package com.repol.mfec.mfec_test.mvp;

import com.repol.mfec.mfec_test.github.GitHubService;
import com.repol.mfec.mfec_test.model.GitHubUser;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created on 21/9/2560.
 */
public class MainPresenter implements MainContract.Presenter {
    private final MainContract.View mainView;
    private final GitHubService gitHubService;
    private CompositeDisposable compositeDisposable;

    public MainPresenter(MainContract.View mainView, GitHubService gitHubService) {
        this.mainView = mainView;
        this.gitHubService = gitHubService;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getGitHubUsers(final int startFromId) {
        Disposable disposable = gitHubService.getUsers(startFromId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<GitHubUser>>() {
                    @Override
                    public void onNext(@NonNull List<GitHubUser> gitHubUsers) {
                        if (startFromId == 0)
                            mainView.onUsersLoaded(gitHubUsers);
                        else
                            mainView.onLoadMoreUser(gitHubUsers);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mainView.onError();
                    }

                    @Override
                    public void onComplete() {
                        if (startFromId == 0)
                            getFollowing();
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void destroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }

    @Override
    public void getFollowing() {
        Disposable disposable = gitHubService.getFollowing()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<GitHubUser>>() {
                    @Override
                    public void onNext(@NonNull List<GitHubUser> gitHubUsers) {
                        mainView.onLoadFollowing(gitHubUsers);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mainView.onError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void following(final int position, String username) {
        Disposable disposable = gitHubService.followUser(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(@NonNull Response<Void> result) {
                        mainView.onFollowUser(position);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mainView.onError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void unfollowing(final int position, String username) {
        Disposable disposable = gitHubService.unfollowUser(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Response<Void>>() {
                    @Override
                    public void onNext(@NonNull Response<Void> result) {
                        mainView.onUnfollowUser(position);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mainView.onError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void getUserProfile(int position, GitHubUser gitHubUser) {
        mainView.openProfilePage(position, gitHubUser);
    }
}
