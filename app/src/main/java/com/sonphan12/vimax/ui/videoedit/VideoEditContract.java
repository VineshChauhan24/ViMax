package com.sonphan12.vimax.ui.videoedit;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.sonphan12.vimax.ui.base.BasePresenter;
import com.sonphan12.vimax.ui.base.BaseView;

public interface VideoEditContract {
    interface View extends BaseView {
        void showVideoPreview();
        void showProgressDialog(String message);
        void cancelProgressDialog();
    }

    interface Presenter extends BasePresenter<View> {
        void showVideoPreview();
        void onBtnRotateClicked(String videoUri, FFmpeg ffmpeg);
        void executeFfmpegCommand(String[] command, FFmpeg ffmpeg, String progressMessage);
    }
}
