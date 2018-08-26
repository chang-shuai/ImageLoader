package com.example.bowan.newimageloader.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.bowan.newimageloader.entity.FolderEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExternalImageResolver {

    /**
     * 外部储存图片Uri
     */
    private static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    /**
     * jpeg图片的mime_type类型
     */
    private static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";

    /**
     * png图片的mime_type类型
     */
    private static final String MIME_TYPE_IMAGE_PNG = "image/png";

    /**
     * 内容解析器
     */
    private ContentResolver mContentResolver;
    private Set<String> mFolderPaths;
    private List<FolderEntity> folders;


    public ExternalImageResolver(Context context) {
        mContentResolver = context.getContentResolver();
        mFolderPaths = new HashSet<>();
        folders = new ArrayList<>();
    }

    /**
     * 从内容解析器获取游标
     * @return
     */
    private Cursor getCursor() {
        return mContentResolver.query(
                IMAGE_URI,
                null,
                MediaStore.Images.Media.MIME_TYPE + "=? " +
                        "or " +
                        MediaStore.Images.Media.MIME_TYPE + "=? ",
                new String[]{MIME_TYPE_IMAGE_JPEG, MIME_TYPE_IMAGE_PNG},
                MediaStore.Images.Media.DATE_MODIFIED);
    }

    /**
     * 获得所有图片的目录.
     * @return
     */
    public List<FolderEntity> getFolders() {
        Cursor cursor = getCursor();
        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                String folderPath = getFolderPathByImagePath(imagePath);
                if (filterFolderPath(folderPath)) {
                    FolderEntity folder = new FolderEntity(folderPath, imagePath);
                    folders.add(folder);
                }
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return folders;
    }

    /**
     * 若此图片没有父目录, 则返回null
     * @param imagePath
     * @return
     */
    private String getFolderPathByImagePath(String imagePath) {
        File folderFile = new File(imagePath).getParentFile();
        if (folderFile != null) {
            return folderFile.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 如果目录名重复, 则过滤掉.
     * @param folderPath
     * @return
     */
    private boolean filterFolderPath(String folderPath) {
        if (folderPath == null) {
            return false;
        } else if (mFolderPaths.contains(folderPath)) {
            return false;
        } else {
            mFolderPaths.add(folderPath);
            return true;
        }
    }

}
