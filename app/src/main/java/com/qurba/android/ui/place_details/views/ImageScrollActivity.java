package com.qurba.android.ui.place_details.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.jsibbold.zoomage.ZoomageView;
import com.qurba.android.R;
import com.qurba.android.utils.BaseActivity;
import com.qurba.android.utils.CustomViewPager;
import com.qurba.android.utils.SharedPreferencesManager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import javax.microedition.khronos.opengles.GL;

public class ImageScrollActivity extends BaseActivity implements BaseSliderView.OnSliderClickListener, CustomViewPager.OnPageChangeListener {

    private SliderLayout mDemoSlider;
    private Toolbar toolbar;
//    private TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_scroll);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        titleTv = (TextView) findViewById(R.id.toolbar_title_tv);

        toolbar.getNavigationIcon().setTint(Color.WHITE);

        toolbar.setNavigationOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        try {
            initSlider();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSlider() throws Exception {
        mDemoSlider = findViewById(R.id.slider);
        LinkedHashMap<String, String> url_maps = new LinkedHashMap<>();

//        titleTv.setText(1 + "/" + getIntent().getExtras().getStringArrayList("images").size());

        for (int i = 0; i < getIntent().getExtras().getStringArrayList("images").size(); i++) {
            url_maps.put(getIntent().getExtras().getStringArrayList("images").get(i)
                    , getIntent().getExtras().getStringArrayList("images").get(i));
        }

        for (String name : url_maps.keySet()) {
            TextBlurSliderView textSliderView = new TextBlurSliderView(this);
            textSliderView
                    .description("")
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterInside)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", "");

            mDemoSlider.addSlider(textSliderView);
        }
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().get("position") != null) {
                mDemoSlider.setCurrentPosition(getIntent().getExtras().getInt("position"));
            } else {
                mDemoSlider.setCurrentPosition(0);
            }
        }

        if (getIntent().getExtras().getStringArrayList("images").size() <= 1) {//stop sliding if only one image
            mDemoSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
            mDemoSlider.setPagerTransformer(false, new BaseTransformer() {

                @Override
                public void onTransform(View view, float position) {
                }

            });
            mDemoSlider.stopAutoCycle();
        }

//        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    protected void onStop() {
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
//        titleTv.setText((position + 1) + "/" + getIntent().getExtras().getStringArrayList("images").size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public class TextBlurSliderView extends BaseSliderView {
        public TextBlurSliderView(Context context) {
            super(context);
        }

        @Override
        public View getView() {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_view, null);
            ZoomageView target = v.findViewById(R.id.daimajia_slider_image);
            TextView description = v.findViewById(R.id.description);
            description.setText(getDescription());
            description.setBackgroundColor(Color.TRANSPARENT);
            Glide.with(ImageScrollActivity.this).load(getUrl()).placeholder(R.drawable.ic_image_placeholder).into(target);
            bindEventAndShow(v, target);
            return v;
        }
    }
}

