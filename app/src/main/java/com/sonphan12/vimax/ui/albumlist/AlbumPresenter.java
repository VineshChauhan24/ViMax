package com.sonphan12.vimax.ui.albumlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.sonphan12.vimax.data.OfflineVideoAlbumRepository;
import com.sonphan12.vimax.data.model.Album;
import com.sonphan12.vimax.data.model.Video;
import com.sonphan12.vimax.ui.base.BaseFragment;
import com.sonphan12.vimax.ui.videolist.VideoAdapter;
import com.sonphan12.vimax.utils.AppConstants;
import com.sonphan12.vimax.utils.ApplyScheduler;

import io.reactivex.disposables.CompositeDisposable;

public class AlbumPresenter implements AlbumContract.Presenter {
    private AlbumContract.View view;
    private OfflineVideoAlbumRepository offlineVideoAlbumRepository;
    private CompositeDisposable disposable;

    public AlbumPresenter(OfflineVideoAlbumRepository offlineVideoAlbumRepository,
                          CompositeDisposable disposable) {
        this.offlineVideoAlbumRepository = offlineVideoAlbumRepository;
        this.disposable = disposable;
    }

    @Override
    public void getAlbums(Context ctx) {
        disposable.add(offlineVideoAlbumRepository.loadAll(ctx)
                .compose(ApplyScheduler.applySchedulersObservable())
                .subscribe(albums -> {
                    view.hideProgressCircle();
                    view.showAlbums(albums);
                }, e -> {
                    view.showToastMessage(e.toString(), Toast.LENGTH_SHORT);
                    Log.d("ERROR_GET_ALBUMS", e.toString());
                }));
    }

    @Override
    public void onReceiveAction(Context ctx, Intent intent) {
        switch (intent.getAction()) {
            case AppConstants.ACTION_UPDATE_DATA:
                getAlbums(ctx);
                break;
        }
    }

    @Override
    public void setView(AlbumContract.View view) {
        this.view = view;
    }

    @Override
    public void destroy() {
        disposable.dispose();
    }

    @Override
    public void onListScroll(int lastVisiblePosition, int listSize, int dx, int dy) {
        if (lastVisiblePosition >= 10) view.showBackOnTopButton();
        else view.hideBackOnTopButton();
    }

    @Override
    public void onBtnBackOnTopClicked() {
        view.scrollOnTop();
    }

    @Override
    public boolean enableAllCheckBox(AlbumAdapter adapter, int position) {
        Album album = adapter.getListAlbum().get(position);
        adapter.setEnableAllCheckbox(true);
        album.setChecked(true);
        adapter.notifyDataSetChanged();
        view.showHiddenLayout();
        ((BaseFragment)view).setInitialState(false);
        return false;
    }

    @Override
    public void returnToInitialState(AlbumAdapter adapter) {
        for (Album album : adapter.getListAlbum()) {
            album.setChecked(false);
        }
        adapter.setEnableAllCheckbox(false);
        adapter.notifyDataSetChanged();
        view.hideHiddenLayout();
        ((BaseFragment) view).setInitialState(true);
    }
}
