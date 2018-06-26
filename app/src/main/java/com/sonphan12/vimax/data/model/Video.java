package com.sonphan12.vimax.data.model;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.sonphan12.vimax.R;

import java.io.File;

public class Video {
    private String name;
    private boolean isChecked;

    private String duration;
    private String fileSrc;

    public Video(String fileSrc, String name, String duration) {
        this.fileSrc = fileSrc;
        this.name = name;
        this.duration = duration;
        this.isChecked = false;
    }
    public Video() {

    }


    public String getName() {
        if (name == null || name.equals("")) {
            File videoFile = new File(this.fileSrc);
            name = videoFile.getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDuration() {
        if (duration == null || duration.equals("")) {
            duration = "88:88:88";
        }
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFileSrc() {
        return fileSrc;
    }

    public void setFileSrc(String fileSrc) {
        this.fileSrc = fileSrc;
    }
}
