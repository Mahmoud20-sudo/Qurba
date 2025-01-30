package com.qurba.android.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qurba.android.R;

import java.text.NumberFormat;

/**
 * View to allow the selection of a numeric value by pressing plus/minus buttons.  Pressing and holding
 * a button will update the value repeatedly.
 * <p>
 * This view can be configured with a minimum and maximum value.  There is also a label that will
 * display below the current value.
 * </p>
 */
public class ValueSelector extends RelativeLayout {

    private int minValue = 0;
    private int maxValue = 10;

    private boolean plusButtonIsPressed = false;
    private boolean minusButtonIsPressed = false;
    private final long REPEAT_INTERVAL_MS = 100l;

    View rootView;
    TextView valueTextView;
    View minusButton;
    View plusButton;

    DeletionListener deletionListener;

    Handler handler = new Handler(Looper.getMainLooper());
    private boolean isProduct;
    private int itemPrice;

    public ValueSelector(Context context) {
        super(context);
        init(context);
    }

    public ValueSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ValueSelector(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setDeletionListener(DeletionListener deletionListener) {
        this.deletionListener = deletionListener;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    /**
     * Get the current minimum value that is allowed
     *
     * @return
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * Set the minimum value that will be allowed
     *
     * @param minValue
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setIsProduct(boolean isProduct) {
        this.isProduct = isProduct;
    }

    /**
     * Get the current maximum value that is allowed
     *
     * @return
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Set the maximum value that will be allowed
     *
     * @param maxValue
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Get the current value
     *
     * @return the current value
     */
    public int getValue() {
        return Integer.valueOf(valueTextView.getText().toString());
    }

    /**
     * Set the current value.  If the passed in value exceeds the current min or max, the value
     * will be set to the respective min/max.
     *
     * @param newValue new value
     */
    public void setValue(int newValue) {
        int value = newValue;
        if (newValue < minValue) {
            value = minValue;
        } else if (newValue > maxValue) {
            value = maxValue;
        }

        valueTextView.setText(NumberFormat.getInstance().format(value));
        if (value == 1) {
            ((ImageView) minusButton).setImageResource(R.drawable.ic_baseline_delete_forever_24);
        } else {
            ((ImageView) minusButton).setImageResource(R.drawable.ic_iconfinder_minus_4115236);
        }
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.value_selector_v2, this);
        valueTextView = (TextView) rootView.findViewById(R.id.valueTextView);

        minusButton = rootView.findViewById(R.id.minusButton);
        plusButton = rootView.findViewById(R.id.plusButton);

        valueTextView.setEnabled(false);

        minusButton.setOnClickListener(v -> {
            decrementValue();
            if (deletionListener != null) {
                if (Integer.parseInt(valueTextView.getText().toString()) == 0) {
                    deletionListener.onDeleteItemListener((Integer) getTag());
                } else {
                    deletionListener.onQuantityChangeListener(Integer.parseInt(valueTextView.getText().toString()), (Integer) getTag());
                }
            }
        });
//        minusButton.setOnLongClickListener(
//            new OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View arg0) {
//                    minusButtonIsPressed = true;
//                    handler.post(new AutoDecrementer());
//                    return false;
//                }
//            }
//        );
//        minusButton.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
//                    minusButtonIsPressed = false;
//                }
//                return false;
//            }
//        });

        plusButton.setOnClickListener(v -> {
            incrementValue();
            if (deletionListener != null)
                deletionListener.onQuantityChangeListener(Integer.parseInt(valueTextView.getText().toString()), (Integer) getTag());

        });
//        plusButton.setOnLongClickListener(
//            new OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View arg0) {
//                    plusButtonIsPressed = true;
//                    handler.post(new AutoIncrementer());
//                    return false;
//                }
//            }
//        );
//
//        plusButton.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
//                    plusButtonIsPressed = false;
//                }
//                return false;
//            }
//        });
    }

    private void incrementValue() {
        int currentVal = Integer.valueOf(valueTextView.getText().toString());
        if (currentVal == maxValue) {
            Toast.makeText(getContext(), getContext().getString(R.string.max_quantity_hint), Toast.LENGTH_SHORT).show();
            return;
        } else if (itemPrice * (currentVal + 1) > 1000) {
            Toast.makeText(getContext(), getContext().getString(R.string.max_price_hint), Toast.LENGTH_SHORT).show();
            return;
        } else {
            valueTextView.setText(String.valueOf(currentVal + 1));
        }
    }

    private void decrementValue() {
        int currentVal = Integer.valueOf(valueTextView.getText().toString());
        if (currentVal == 1 && !isProduct) {
            ((ImageView) minusButton).setImageResource(R.drawable.ic_baseline_delete_forever_24);
        }
        if (currentVal > minValue) {
            valueTextView.setText(String.valueOf(currentVal - 1));
        }
    }

    private class AutoIncrementer implements Runnable {
        @Override
        public void run() {
            if (plusButtonIsPressed) {
                incrementValue();
                handler.postDelayed(new AutoIncrementer(), REPEAT_INTERVAL_MS);
            }
        }
    }

    private class AutoDecrementer implements Runnable {
        @Override
        public void run() {
            if (minusButtonIsPressed) {
                decrementValue();
                handler.postDelayed(new AutoDecrementer(), REPEAT_INTERVAL_MS);
            }
        }
    }
}
