package dev1503.litematerial.lmc4a.app.demo;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.Icon;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.widget.tabs.MaterialTab;
import dev1503.lmc4a.v3.widget.tabs.MaterialTabLayout;
import dev1503.lmc4a.v3.widget.tabs.TabMode;
import dev1503.lmc4a.v3.widget.tabs.TabStyle;

public class TabsDemoActivity extends DemoActivity {

    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(16.0f);
        content.setPadding(padding, padding, padding, padding);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        status = new TextView(this);
        status.setTextColor(SchemeHelper.onBackgroundColor());
        status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        content.addView(caption("PRIMARY / FIXED"), captionParams());
        final MaterialTabLayout primaryFixed = new MaterialTabLayout(this);
        primaryFixed.addTab("首页");
        primaryFixed.addTab("发现");
        primaryFixed.addTab("我的");
        primaryFixed.setOnTabSelectedListener(listener("primaryFixed"));
        content.addView(primaryFixed, tabParams());

        content.addView(caption("PRIMARY / SCROLLABLE + 图标"), captionParams());
        final MaterialTabLayout primaryScrollable = new MaterialTabLayout(this);
        primaryScrollable.setMode(TabMode.SCROLLABLE);
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_agenda), "首页");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_search), "搜索");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_gallery), "图库");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_compass), "定位");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_manage), "设置");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_share), "分享");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_edit), "编辑");
        primaryScrollable.addTab(new Icon(android.R.drawable.ic_menu_more), "更多");
        primaryScrollable.setOnTabSelectedListener(listener("primaryScrollable"));
        content.addView(primaryScrollable, tabParams());

        content.addView(caption("SECONDARY"), captionParams());
        final MaterialTabLayout secondary = new MaterialTabLayout(this);
        secondary.setStyle(TabStyle.SECONDARY);
        secondary.addTab("概览");
        secondary.addTab("规格");
        secondary.addTab("评价");
        secondary.addTab("推荐");
        secondary.setOnTabSelectedListener(listener("secondary"));
        content.addView(secondary, tabParams());

        content.addView(caption("bindTo(ViewPager)"), captionParams());
        final MaterialTabLayout boundTabs = new MaterialTabLayout(this);
        final ViewPager boundPager = new ViewPager(this);
        boundPager.setAdapter(new TabsPagerAdapter());
        boundTabs.setOnTabSelectedListener(listener("boundTabs"));
        boundTabs.bindTo(boundPager);
        content.addView(boundTabs, tabParams());
        content.addView(boundPager, pagerParams());
        content.addView(row(
                actionButton("bindTo(viewPager)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boundTabs.bindTo(boundPager);
                        toast("bindTo(viewPager) → isBound()=" + boundTabs.isBound()
                                + "，tab 数 = " + boundTabs.getTabCount());
                    }
                }),
                actionButton("unbind()", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boundTabs.unbind();
                        toast("unbind() → isBound()=" + boundTabs.isBound());
                    }
                }),
                actionButton("isBound()", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toast("isBound()=" + boundTabs.isBound()
                                + "，pager 当前页 = " + boundPager.getCurrentItem());
                    }
                })), rowParams());

        content.addView(caption("运行时操作"), captionParams());
        content.addView(row(
                actionButton("setStyle(SECONDARY)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TabStyle next = primaryFixed.getStyle() == TabStyle.PRIMARY
                                ? TabStyle.SECONDARY : TabStyle.PRIMARY;
                        primaryFixed.setStyle(next);
                        toast("primaryFixed.setStyle(" + next + ")");
                    }
                }),
                actionButton("setMode(SCROLLABLE)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TabMode next = primaryFixed.getMode() == TabMode.FIXED
                                ? TabMode.SCROLLABLE : TabMode.FIXED;
                        primaryFixed.setMode(next);
                        toast("primaryFixed.setMode(" + next + ")");
                    }
                }),
                actionButton("setSelectedTab(2)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondary.setSelectedTab(2);
                    }
                })), rowParams());

        content.addView(row(
                actionButton("addTab", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = primaryFixed.getTabCount();
                        primaryFixed.addTab("新标签 " + count);
                    }
                }),
                actionButton("removeTabAt(0)", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        primaryFixed.removeTabAt(0);
                    }
                }),
                actionButton("自定义指示器", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        secondary.setIndicatorColor(Lmc.publicColorScheme.getTertiary());
                        secondary.setActiveTabColor(Lmc.publicColorScheme.getTertiary());
                        toast("secondary 指示器/选中色 = tertiary");
                    }
                })), rowParams());

        content.addView(status, tabParams());

        setContentView(scrollView);
    }

    private final class TabsPagerAdapter extends PagerAdapter {

        private final String[] titles = {"页面一", "页面二", "页面三"};

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView page = new TextView(container.getContext());
            page.setText(titles[position]);
            page.setGravity(Gravity.CENTER);
            page.setTextColor(SchemeHelper.onBackgroundColor());
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

    private MaterialTabLayout.OnTabSelectedListener listener(final String name) {
        return new MaterialTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(MaterialTab tab) {
                updateStatus(name + " onTabSelected: " + tab.getText());
            }

            @Override
            public void onTabUnselected(MaterialTab tab) {
                updateStatus(name + " onTabUnselected: " + tab.getText());
            }

            @Override
            public void onTabReselected(MaterialTab tab) {
                updateStatus(name + " onTabReselected: " + tab.getText());
            }
        };
    }

    private void updateStatus(String text) {
        status.setText(text);
    }

    private MaterialButton actionButton(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setStyle(ButtonStyle.TEXT);
        button.setText(text);
        button.setTextSize(13.0f);
        button.setOnClickListener(listener);
        return button;
    }

    private View row(View... children) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        for (View child : children) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = dp(4.0f);
            row.addView(child, params);
        }
        return row;
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTextColor(SchemeHelper.onBackgroundColor());
        return caption;
    }

    private LinearLayout.LayoutParams captionParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = dp(16.0f);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams tabParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private LinearLayout.LayoutParams pagerParams() {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(180.0f));
    }

    private LinearLayout.LayoutParams rowParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int dp(float valueDp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, valueDp, getResources().getDisplayMetrics());
    }
}
