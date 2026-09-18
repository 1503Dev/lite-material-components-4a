package dev1503.litematerial.lmc4a.app.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.widget.navigationrail.MaterialNavigationRail;
import dev1503.lmc4a.v3.widget.navigationrail.MaterialNavigationRailItem;
import dev1503.lmc4a.v3.widget.navigationrail.NavigationRailLabelVisibilityMode;
import dev1503.lmc4a.v3.widget.navigationrail.NavigationRailOrientation;
import dev1503.lmc4a.v3.widget.radiobutton.MaterialRadioButton;

public class NavigationRailDemoActivity extends Activity {

    private static final int[] ICON_RES_IDS = {
            android.R.drawable.ic_menu_agenda,
            android.R.drawable.ic_menu_gallery,
            android.R.drawable.ic_menu_manage,
            android.R.drawable.ic_menu_compass,
    };

    private static final String[] ITEM_LABELS = {"Home", "Gallery", "Manage", "Compass"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(16.0f);
        root.setPadding(padding, padding, padding, padding);

        root.addView(createLabel("Horizontal rail (bound to ViewPager)"), labelParams());

        MaterialNavigationRail horizontalRail = createRail(NavigationRailOrientation.HORIZONTAL);
        root.addView(horizontalRail, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        root.addView(createLabel("Label visibility mode"), labelParams());
        root.addView(createModeGroup(horizontalRail), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ViewPager viewPager = new ViewPager(this);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));
        viewPager.setAdapter(new DemoPagerAdapter());
        root.addView(viewPager);

        root.addView(createLabel("Vertical rail (unbound)"), labelParams());

        FrameLayout frame = new FrameLayout(this);
        frame.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(300)));
        MaterialNavigationRail verticalRail = createRail(NavigationRailOrientation.VERTICAL);
        FrameLayout.LayoutParams railParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        railParams.gravity = Gravity.START;
        frame.addView(verticalRail, railParams);
        root.addView(frame);

        horizontalRail.bindTo(viewPager);

        setContentView(root);
    }

    private RadioGroup createModeGroup(final MaterialNavigationRail rail) {
        RadioGroup group = new RadioGroup(this);
        group.setOrientation(RadioGroup.HORIZONTAL);
        NavigationRailLabelVisibilityMode[] modes = NavigationRailLabelVisibilityMode.values();
        for (int i = 0; i < modes.length; i++) {
            final NavigationRailLabelVisibilityMode mode = modes[i];
            MaterialRadioButton button = new MaterialRadioButton(this);
            button.setId(i);
            button.setText(mode.name());
            button.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        rail.setLabelVisibilityMode(mode);
                    }
                }
            });
            group.addView(button, new RadioGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
            if (i == 0) {
                button.setChecked(true);
            }
        }
        return group;
    }

    private MaterialNavigationRail createRail(NavigationRailOrientation orientation) {
        MaterialNavigationRail rail = new MaterialNavigationRail(this);
        rail.setOrientation(orientation);
        for (int i = 0; i < 4; i++) {
            MaterialNavigationRailItem item = new MaterialNavigationRailItem();
            item.setIcon(new Icon(ICON_RES_IDS[i % ICON_RES_IDS.length]));
            item.setText(ITEM_LABELS[i % ITEM_LABELS.length]);
            rail.addItem(item);
        }
        return rail;
    }

    private static class DemoPagerAdapter extends PagerAdapter {

        private static final String[] TITLES = {"Home", "Gallery", "Manage", "Compass"};

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView page = new TextView(container.getContext());
            page.setText(TITLES[position]);
            page.setGravity(Gravity.CENTER);
            page.setTextColor(0xFF000000);
            page.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            container.addView(page, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private TextView createLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(SchemeHelper.onBackgroundColor());
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        return label;
    }

    private LinearLayout.LayoutParams labelParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(4.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics()) + 0.5f);
    }
}