package com.example.bowan.newimageloader.entity;

public class FolderEntity {
    private String mFolderPath;
    private String mFirstImagePath;
    private String mFolderName;
    private int mCount;

    public FolderEntity(String folderPath, String firstImagePath) {
        mFolderPath = folderPath;
        mFirstImagePath = firstImagePath;
    }

    public String getFolderPath() {
        return mFolderPath;
    }

    public void setFolderPath(String folderPath) {
        mFolderPath = folderPath;
        String[] parts = folderPath.split("/");
        mFolderName = parts[parts.length];
    }

    public String getFirstImagePath() {
        return mFirstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        mFirstImagePath = firstImagePath;
    }

    public String getFolderName() {
        return mFolderName;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    @Override
    public String toString() {
        return "FolderEntity{" +
                "mFolderPath='" + mFolderPath + '\'' +
                '}';
    }
}
