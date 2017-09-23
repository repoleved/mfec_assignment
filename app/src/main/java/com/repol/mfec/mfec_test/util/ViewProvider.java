package com.repol.mfec.mfec_test.util;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;

/**
 * Created on 21/9/2560.
 */

public class ViewProvider implements ViewUtil {
    @Override
    public void setViewElevation(Context context, View view) {
        float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics());
        ViewCompat.setElevation(view, elevation);
    }
}
