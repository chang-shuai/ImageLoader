package com.example.bowan.newimageloader;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.bowan.newimageloader.entity.FolderEntity;
import com.example.bowan.newimageloader.entity.ImageEntity;
import com.example.bowan.newimageloader.util.ExternalImageResolver;
import com.example.bowan.newimageloader.util.ThumbnailLoaderThread;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridImageFragment extends Fragment {

    private static final String TAG = "GridImageFragment";

    private RecyclerView mImageGrid;
    private List<ImageEntity> mImages = new ArrayList<>();
    private List<FolderEntity> mFolders = new ArrayList<>();
    private ThumbnailLoaderThread<ImageHolder> mThumbnailLoaderThread;
    private ProgressDialog mProgressDialog;
    private AsyncTask<Void, Void, List<FolderEntity>> mQueryImageTask;

    public static GridImageFragment newInstance() {
        GridImageFragment fragment = new GridImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mQueryImageTask = new QueryImageTask().execute();
        Handler mHandler = new Handler();
        mThumbnailLoaderThread = new ThumbnailLoaderThread(mHandler);

        mThumbnailLoaderThread.setThumbnailLoaderListener(
                new ThumbnailLoaderThread.ThumbnailLoaderListener<ImageHolder>() {
            @Override
            public void onThumbnailLoaderListener(ImageHolder target, Bitmap bitmap) {
                BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                target.bindDrawable(drawable);
            }
        });

        mThumbnailLoaderThread.start();
        mThumbnailLoaderThread.getLooper();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_image, container, false);

        mImageGrid = (RecyclerView) view.findViewById(R.id.image_grid_recycler_view);
        mImageGrid.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        return view;
    }

    private void setupAdapter() {
        if (isAdded()) {
            FolderEntity folderEntity = mFolders.get(1);
            String[] file = new File(folderEntity.getFolderPath()).list();
            mImageGrid.setAdapter(new ImageAdapter(Arrays.asList(file)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mQueryImageTask != null) {
            mQueryImageTask.cancel(false);
        }
        mThumbnailLoaderThread.quit();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailLoaderThread.clearQueue();
    }

    /**
     * ############################################################################
     * 异步线程内部类
     * 通过内容解析器获得所有的图片文件夹.
     */
    private class QueryImageTask extends AsyncTask<Void, Void, List<FolderEntity>> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(),null, "正在加载...");
        }

        @Override
        protected List<FolderEntity> doInBackground(Void... voids) {
            ExternalImageResolver resolver = new ExternalImageResolver(getActivity());
            List<FolderEntity> folders = resolver.getFolders();
            return folders;
        }


        @Override
        protected void onPostExecute(List<FolderEntity> folders) {
            mFolders = folders;
            mProgressDialog.dismiss();
            setupAdapter();
        }
    }

    /**
     * ###############################################################################
     * RecyclerView适配器内部类
     */
    private class ImageHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private ImageButton mImageButton;

        public ImageHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_grid_image_view);
            mImageButton = itemView.findViewById(R.id.image_grid_image_button);
        }

        public void bindDrawable(Drawable drawable) {
            mImageView.setImageDrawable(drawable);
        }

    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {

        private List<String> mImageItems;

        private ImageAdapter(List<String> imageItems) {
            mImageItems = imageItems;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_thumbnail, parent, false);
            return new ImageHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {
            String folderPath = mFolders.get(1).getFolderPath();
            String imagePath = folderPath +"/"+ mImageItems.get(position);
            Drawable drawable = getResources().getDrawable(R.drawable.no_pic);
            holder.bindDrawable(drawable);
            mThumbnailLoaderThread.queueThumbnail(holder, imagePath);
        }

        @Override
        public int getItemCount() {
            return mImageItems.size();
        }
    }

}
