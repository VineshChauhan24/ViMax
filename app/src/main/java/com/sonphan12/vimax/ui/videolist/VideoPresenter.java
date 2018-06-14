package com.sonphan12.vimax.ui.videolist;

import android.content.Context;

import com.sonphan12.vimax.data.OfflineVideoRepository;
import com.sonphan12.vimax.data.model.Video;

import java.util.List;

public class VideoPresenter implements VideoContract.Presenter {
    private VideoContract.View view;
    private OfflineVideoRepository offlineVideoRepository;

    public VideoPresenter(VideoContract.View view) {
        this.view = view;
        this.offlineVideoRepository = new OfflineVideoRepository();
    }


    @Override
    public List<Video> getVideos(Context ctx) {
        return offlineVideoRepository.load(ctx);
    }


    @Override
    public void setView(VideoContract.View view) {
        this.view = view;
    }
}
