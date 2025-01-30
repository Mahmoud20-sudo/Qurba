package com.qurba.android.utils;

import androidx.recyclerview.widget.RecyclerView;

public abstract class MyRecyclerScroll extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;

    public MyRecyclerScroll() {
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                System.out.println("The RecyclerView is not scrolling");
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                System.out.println("Scrolling now");
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                System.out.println("Scroll Settling");
                break;

        }

    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (dy > 0) {
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            }
        } else if (dy < 0) {
            if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
        }
        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
            scrolledDistance += dy;
        }
    }

//    @Override
//    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        super.onScrolled(recyclerView, dx, dy);
//
//        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
//            onHide();
//            controlsVisible = false;
//            scrolledDistance = 0;
//        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
//            onShow();
//            controlsVisible = true;
//            scrolledDistance = 0;
//        }
//
//        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
//            scrolledDistance += dy;
//        }
//    }

    public abstract void onHide();
    public abstract void onShow();

}
