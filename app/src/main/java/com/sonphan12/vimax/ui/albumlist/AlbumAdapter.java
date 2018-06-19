package com.sonphan12.vimax.ui.albumlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sonphan12.vimax.R;
import com.sonphan12.vimax.data.model.Album;
import com.sonphan12.vimax.ui.MainActivity;
import com.sonphan12.vimax.ui.videolist.VideoFragment;
import com.sonphan12.vimax.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.FolderViewHolder> {
    private List<Album> listAlbum;
    private Context ctx;

    public AlbumAdapter() {
        super();
    }
    public AlbumAdapter(Context ctx) {
        this.ctx = ctx;
        this.listAlbum = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlbumAdapter.FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.album_card, parent, false);
        FolderViewHolder holder = new FolderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumAdapter.FolderViewHolder holder, int position) {
        Album album = listAlbum.get(position);
        holder.txtAlbumName.setText(album.getName());
        holder.txtNumVideo.setText(String.valueOf(album.getNumVideos()));

        // Switch to video fragment
        holder.cardViewAlbum.setOnClickListener(v -> {
            FragmentManager fm = ((AppCompatActivity) ctx).getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            VideoFragment videoFragment = new VideoFragment();

            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.EXTRA_ALBUM_NAME, album.getName());
            videoFragment.setArguments(bundle);

            transaction.replace(R.id.fragmentDummy, videoFragment);
            transaction.addToBackStack(null);

            transaction.commit();
        });

    }

    @Override
    public int getItemCount() {
        return listAlbum.size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txtAlbumName) TextView txtAlbumName;
        @BindView(R.id.txtNumVideo) TextView txtNumVideo;
        @BindView(R.id.cardViewAlbum) CardView cardViewAlbum;

        public FolderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setListAlbum(List<Album> listAlbum) {
        this.listAlbum = listAlbum;
    }
}
