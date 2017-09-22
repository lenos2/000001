package zw.co.matrixcab.matrixcab.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by android on 21/3/17.
 */

public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(false);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(false);
    }

    public MyTextView(Context context,Boolean result) {
        super(context);
        init(result);
    }

    private void init(Boolean set) {
        if (!isInEditMode()) {
            // Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font.ttf");
            if (set) {
                Typeface book = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Book.otf");
                setTypeface(book);
            } else {
                Typeface medium = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Medium.otf");

                setTypeface(medium);
            }
        }
    }

}