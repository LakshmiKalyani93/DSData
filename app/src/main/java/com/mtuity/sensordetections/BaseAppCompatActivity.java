package com.mtuity.sensordetections;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;

/**
 * Created by Avinash
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    private ACProgressFlower progressDialog;
    public static final float FLOAT_ONE = 1F;
    public static final float FLOAT_ZERO_POINT_SEVEN_FIVE = 0.75F;
    public static final float FLOAT_FIVE = 5F;

    /**
     * Dismisses dialog if it is already showing
     */
    public void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Show dialog if not showing already
     */
    public void showDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            // already progress bar object is created
        } else {
            progressDialog = new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .bgColor(android.R.color.black)
                    .bgAlpha(FLOAT_ZERO_POINT_SEVEN_FIVE)
                    .themeColor(Color.WHITE)
                    .petalCount(30)
                    .petalThickness(2)
                    .petalAlpha(FLOAT_ONE)
                    .bgCornerRadius(FLOAT_FIVE)
                    .fadeColor(Color.DKGRAY).build();
            progressDialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            dismissDialog();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
