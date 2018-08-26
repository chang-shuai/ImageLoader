package com.example.bowan.newimageloader;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GridImageActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return GridImageFragment.newInstance();
    }
}
