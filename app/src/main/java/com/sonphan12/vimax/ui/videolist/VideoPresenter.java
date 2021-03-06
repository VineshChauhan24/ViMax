package com.sonphan12.vimax.ui.videolist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.sonphan12.vimax.R;
import com.sonphan12.vimax.data.OfflineVideoRepository;
import com.sonphan12.vimax.data.model.Video;
import com.sonphan12.vimax.data.model.VideoComparator;
import com.sonphan12.vimax.ui.base.BaseFragment;
import com.sonphan12.vimax.utils.AppConstants;
import com.sonphan12.vimax.utils.ApplyScheduler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class VideoPresenter implements VideoContract.Presenter {
    private VideoContract.View view;
    private OfflineVideoRepository offlineVideoRepository;
    private CompositeDisposable compositeDisposable;

    public VideoPresenter(OfflineVideoRepository offlineVideoRepository
            , CompositeDisposable compositeDisposable) {
        this.offlineVideoRepository = offlineVideoRepository;
        this.compositeDisposable = compositeDisposable;
    }


    @Override
    public void getVideos(Context ctx) {
        Bundle bundle = ((Fragment) view).getArguments();
        String albumName;
        if (bundle == null || (albumName = bundle.getString(AppConstants.EXTRA_ALBUM_NAME, null)) == null) {
            compositeDisposable.add(offlineVideoRepository.loadAll(ctx)
                    .compose(ApplyScheduler.applySchedulersObservableIO())
                    .doOnSubscribe(__ -> view.showProgressCircle())
                    .doOnTerminate(() -> view.hideProgressCircle())
                    .subscribe(videos -> view.showVideos(videos),
                            e -> view.showToastMessage(e.toString(), Toast.LENGTH_SHORT)));
        } else {
            compositeDisposable.add(offlineVideoRepository.loadFromAlbum(ctx, albumName)
                    .compose(ApplyScheduler.applySchedulersObservableIO())
                    .doOnSubscribe(__ -> view.showProgressCircle())
                    .doOnTerminate(() -> view.hideProgressCircle())
                    .subscribe(videos -> view.showVideos(videos),
                            e -> view.showToastMessage(e.toString(), Toast.LENGTH_SHORT)));
        }
    }

    @Override
    public void setCheckAll(List<Video> listVideo) {
        for (Video video : listVideo) {
            video.setChecked(true);
        }
        view.showVideos(listVideo);
    }

    @Override
    public void setUncheckAll(List<Video> listVideo) {
        for (Video video : listVideo) {
            video.setChecked(false);
        }
        view.showVideos(listVideo);
    }

    @Override
    public void returnToInitialState(VideoAdapter adapter) {
        for (Video video : adapter.getListVideo()) {
            video.setChecked(false);
        }
        adapter.setEnableAllCheckbox(false);
        adapter.notifyDataSetChanged();
        view.hideHiddenLayout();
        ((BaseFragment) view).setInitialState(true);
    }

    @Override
    public boolean enableAllCheckBox(VideoAdapter adapter, int position) {
        Video video = adapter.getListVideo().get(position);
        adapter.setEnableAllCheckbox(true);
        video.setChecked(true);
        adapter.notifyDataSetChanged();
        view.showHiddenLayout();
        ((BaseFragment)view).setInitialState(false);
        return false;
    }

    @Override
    public void deleteCheckedVideos(List<Video> listVideo) {
        Context ctx = ((BaseFragment)view).getContext();
        int numVideoToDelete = 0;
        int numDeletedVideo = 0;
        for (Video video : listVideo) {
            if (video.isChecked()) {
                numVideoToDelete++;
                File file = new File(video.getFileSrc());
                if (file.exists()) {
                    compositeDisposable.add(
                            offlineVideoRepository.deleteVideo(ctx, video.getId())
                            .compose(ApplyScheduler.applySchedulersCompletableIO())
                            .subscribe(() -> {
                                Intent intent = new Intent(AppConstants.ACTION_UPDATE_DATA);
                                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                            }, Throwable::printStackTrace)
                    );
                    boolean isSuccess = file.delete();
                    if (isSuccess) {
                        numDeletedVideo++;
                    }
                }
            }
        }
        String deleteMessage = "Deleted " + String.valueOf(numDeletedVideo) + "/" +
                String.valueOf(numVideoToDelete) + " videos";
        view.showToastMessage(deleteMessage, Toast.LENGTH_SHORT);
    }

    @Override
    public void checkVideo(Video video) {
        video.setChecked(!video.isChecked());
    }

    @Override
    public void onReceiveAction(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AppConstants.ACTION_UPDATE_DATA:
                getVideos(context);
                break;
            case AppConstants.ACION_SEARCH:
                searchVideo(intent.getStringExtra(AppConstants.EXTRA_SEARCH_QUERY));
                break;
            case AppConstants.ACTION_SORT_ASC:
                view.showCurrentVideosWithOptions(AppConstants.ACTION_SORT_ASC);
                break;
            case AppConstants.ACTION_SORT_DESC:
                view.showCurrentVideosWithOptions(AppConstants.ACTION_SORT_DESC);
                break;
        }
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
    public void searchVideo(String query) {
        compositeDisposable.clear();
        Disposable d = Observable.just(query)
                .switchMap((Function<String, ObservableSource<List<Video>>>) q -> offlineVideoRepository.searchVideo(((BaseFragment) view).getContext(), q))
                .compose(ApplyScheduler.applySchedulersObservableIO())
                .doOnSubscribe(__ -> {
                    view.showVideos(new ArrayList<>());
                    view.showProgressCircle();
                })
                .doOnTerminate(() -> view.hideProgressCircle())
                .subscribe(
                        listVideo -> view.showVideos(listVideo),
                        error -> view.showToastMessage(((BaseFragment) view).getContext().getString(R.string.error), Toast.LENGTH_SHORT));
        compositeDisposable.add(d);
    }

    @Override
    public void showCurrentVideosWithOptions(List<Video> listCurrentVideos, String option) {
        compositeDisposable.clear();

        Disposable d = Completable.create(emitter -> {
                    VideoComparator videoComparator = new VideoComparator();
                    if (option.equals(AppConstants.ACTION_SORT_ASC)) {
                        Collections.sort(listCurrentVideos, videoComparator);
                    } else {
                        Collections.sort(listCurrentVideos, videoComparator);
                        Collections.reverse(listCurrentVideos);
                    }
                    emitter.onComplete();
                }
        )
                .compose(ApplyScheduler.applySchedulersCompletableComputation())
                .doOnSubscribe(__ -> {
                    view.showVideos(new ArrayList<>());
                    view.showProgressCircle();
                })
                .doOnTerminate(() -> view.hideProgressCircle())
                .subscribe(
                        () -> view.showVideos(listCurrentVideos),
                        error -> view.showToastMessage(((BaseFragment)view).getString(R.string.error), Toast.LENGTH_SHORT)
                );

        compositeDisposable.add(d);
    }

    @Override
    public void setView(VideoContract.View view) {
        this.view = view;
    }

    @Override
    public void destroy() {
        compositeDisposable.dispose();
        this.view = null;
    }
}
