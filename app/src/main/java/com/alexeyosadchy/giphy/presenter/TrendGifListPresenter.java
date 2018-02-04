package com.alexeyosadchy.giphy.presenter;

import com.alexeyosadchy.giphy.model.api.ApiManager;
import com.alexeyosadchy.giphy.view.GifView;
import com.alexeyosadchy.giphy.view.ITrendGifListActivity;
import com.alexeyosadchy.giphy.view.TrendGifListActivity;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TrendGifListPresenter implements ITrendGifListPresenter {

    private static final int LIMIT_RECORDS = 25;

    private ITrendGifListActivity mView;
    private ApiManager mApiManager;
    private List<GifView> mGifViews;

    @Inject
    public TrendGifListPresenter(ApiManager apiManager) {
        mApiManager = apiManager;
        mGifViews = new ArrayList<>();
    }

    @Override
    public void loadGifs() {
        mView.showLoading();
        Consumer<List<GifView>> onNext = gifViews -> {
            mGifViews.addAll(gifViews);
            mView.updateList();
            mView.hideLoading();
        };
        Consumer<Throwable> onError = throwable -> errorHandling(throwable, this::loadGifs);
        if (mView.isSearchActive()) {
            getFoundGifs(onNext, onError, mView.getSearchQuery().toString());
        } else {
            getTrendingGifs(onNext, onError);
        }
    }

    @Override
    public void onCreateView() {
        mView.showLoading();
        getTrendingGifs(gifViews -> {
            mGifViews.addAll(gifViews);
            mView.prepareView(mGifViews);
            mView.hideLoading();
        }, throwable -> errorHandling(throwable, this::onCreateView));
    }

    @Override
    public void onSearchSubmit(String query) {
        mView.showLoading();
        getFoundGifs(gifViews -> {
            mGifViews.clear();
            mGifViews.addAll(gifViews);
            mView.updateList();
            mView.hideLoading();
        }, throwable -> {
        }, query);
    }

    private void getFoundGifs(Consumer<? super List<GifView>> onNext, Consumer<? super Throwable> onError, String query) {
        mApiManager.search(query, LIMIT_RECORDS, mGifViews.size())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Mapper::transform)
                .subscribe(onNext, onError);
    }

    private void getTrendingGifs(Consumer<? super List<GifView>> onNext, Consumer<? super Throwable> onError) {
        mApiManager.getTrendingGifs(LIMIT_RECORDS, mGifViews.size())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Mapper::transform)
                .subscribe(onNext, onError);
    }

    @Override
    public void onAttach(ITrendGifListActivity view) {
        mView = view;
    }

    @Override
    public void onDetach() {
        mView = null;
    }

    private void errorHandling(Throwable t, TrendGifListActivity.Callback callback) {
        mView.hideLoading();
        if (t instanceof ConnectException || t instanceof UnknownHostException) {
            mView.showError("There is no internet connection", callback);
            t.printStackTrace();
        } else {
            mView.showError(t.getLocalizedMessage(), callback);
        }
    }
}
