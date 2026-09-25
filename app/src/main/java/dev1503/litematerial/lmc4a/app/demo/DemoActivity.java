package dev1503.litematerial.lmc4a.app.demo;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class DemoActivity extends Activity {

    protected MaterialAppBar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        } catch (RuntimeException ignored) {
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {
        ScrollView scrollView = findScrollView(view);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        appBar = new MaterialAppBar(this);
        appBar.setTitle(defaultTitle());
        root.addView(appBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(view, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        super.setContentView(root);

        if (scrollView != null) {
            appBar.bindTo(scrollView);
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private CharSequence defaultTitle() {
        String name = getClass().getSimpleName();
        if (name.endsWith("DemoActivity")) {
            return "Material" + name.substring(0, name.length() - "DemoActivity".length());
        }
        CharSequence title = getTitle();
        return title == null ? "" : title;
    }

    private static ScrollView findScrollView(View view) {
        if (view instanceof ScrollView) {
            return (ScrollView) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                ScrollView found = findScrollView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}