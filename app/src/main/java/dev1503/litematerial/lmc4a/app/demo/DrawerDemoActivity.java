package dev1503.litematerial.lmc4a.app.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import dev1503.litematerial.lmc4a.app.SchemeHelper;
import dev1503.lmc4a.v3.Lmc;
import dev1503.lmc4a.v3.widget.button.ButtonStyle;
import dev1503.lmc4a.v3.widget.button.MaterialButton;
import dev1503.lmc4a.v3.drawer.DrawerOrientation;
import dev1503.lmc4a.v3.drawer.MaterialDrawer;

public class DrawerDemoActivity extends DemoActivity {

    private MaterialDrawer drawer;
    private TextView statusView;
    private int contentGeneration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);

        drawer = new MaterialDrawer(this);
        drawer.setContentView(createDrawerContent("初始内容"));

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        int padding = dp(24.0f);
        content.setPadding(padding, padding, padding, padding);
        scrollView.addView(content, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(caption("打开 / 关闭"), captionParams());
        content.addView(action("open(DrawerOrientation.LEFT)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open(DrawerOrientation.LEFT);
                updateStatus();
            }
        }), buttonParams());
        content.addView(action("open(DrawerOrientation.END)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.open(DrawerOrientation.END);
                updateStatus();
            }
        }), buttonParams());
        content.addView(action("close()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.close();
                updateStatus();
            }
        }), buttonParams());
        content.addView(action("isOpen() / getOrientation()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("isOpen=" + drawer.isOpen()
                        + " orientation=" + drawer.getOrientation());
            }
        }), buttonParams());

        content.addView(caption("状态"), captionParams());
        statusView = new TextView(this);
        statusView.setTextSize(14.0f);
        statusView.setTextColor(SchemeHelper.onBackgroundColor());
        content.addView(statusView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        updateStatus();

        content.addView(caption("拖动"), captionParams());
        content.addView(action("setDragEnabled() 切换", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setDragEnabled(!drawer.isDragEnabled());
                updateStatus();
                toast("dragEnabled=" + drawer.isDragEnabled());
            }
        }), buttonParams());
        content.addView(hint("打开抽屉后在面板上左右拖动可关闭"), hintParams());

        content.addView(caption("内容"), captionParams());
        content.addView(action("getContentView()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast("contentView=" + (drawer.getContentView() != null));
            }
        }), buttonParams());
        content.addView(action("setContentView(替换内容)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentGeneration++;
                drawer.setContentView(createDrawerContent("第 " + contentGeneration + " 次内容"));
                toast("contentView replaced");
            }
        }), buttonParams());

        content.addView(caption("配色与尺寸"), captionParams());
        content.addView(action("setContainerColor(0xFFFFE082)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setContainerColor(0xFFFFE082);
                toast("containerColor=" + Integer.toHexString(drawer.getContainerColor()));
            }
        }), buttonParams());
        content.addView(action("clearContainerColor()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.clearContainerColor();
                toast("containerColor=" + Integer.toHexString(drawer.getContainerColor()));
            }
        }), buttonParams());
        content.addView(action("setScrimColor(0x99000000)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setScrimColor(0x99000000);
                toast("scrimColor=" + Integer.toHexString(drawer.getScrimColor()));
            }
        }), buttonParams());
        content.addView(action("clearScrimColor()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.clearScrimColor();
                toast("scrimColor=" + Integer.toHexString(drawer.getScrimColor()));
            }
        }), buttonParams());
        content.addView(action("setDrawerWidthDp(240)", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.setDrawerWidthDp(240.0f);
                updateStatus();
                toast("drawerWidthDp=" + drawer.getDrawerWidthDp());
            }
        }), buttonParams());
        content.addView(action("clearDrawerWidthDp()", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.clearDrawerWidthDp();
                updateStatus();
                toast("drawerWidthDp=" + drawer.getDrawerWidthDp());
            }
        }), buttonParams());

        setContentView(scrollView);
    }

    private LinearLayout createDrawerContent(String label) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(24.0f);
        root.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Drawer");
        title.setTextSize(20.0f);
        title.setTypeface(android.graphics.Typeface.create(
                "sans-serif-medium", android.graphics.Typeface.NORMAL));
        title.setTextColor(Lmc.publicColorScheme.getOnSurface());
        root.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(14.0f);
        labelView.setTextColor(Lmc.publicColorScheme.getOnSurfaceVariant());
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = dp(4.0f);
        root.addView(labelView, labelParams);

        String[] items = {"首页", "收藏", "设置"};
        for (int i = 0; i < items.length; i++) {
            final String item = items[i];
            TextView itemView = new TextView(this);
            itemView.setText(item);
            itemView.setTextSize(16.0f);
            itemView.setTextColor(Lmc.publicColorScheme.getOnSurface());
            itemView.setPadding(0, dp(16.0f), 0, dp(16.0f));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toast("drawer item: " + item);
                }
            });
            root.addView(itemView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        MaterialButton closeButton = new MaterialButton(this);
        closeButton.setText("close()");
        closeButton.setStyle(ButtonStyle.OUTLINED);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.close();
                updateStatus();
            }
        });
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.topMargin = dp(16.0f);
        root.addView(closeButton, closeParams);

        return root;
    }

    private void updateStatus() {
        if (statusView == null) {
            return;
        }
        statusView.setText("isOpen=" + drawer.isOpen()
                + "  orientation=" + drawer.getOrientation()
                + "  widthDp=" + drawer.getDrawerWidthDp()
                + "  drag=" + drawer.isDragEnabled());
    }

    private TextView hint(String text) {
        TextView hint = new TextView(this);
        hint.setText(text);
        hint.setTextSize(12.0f);
        hint.setTextColor(SchemeHelper.onBackgroundColor());
        return hint;
    }

    private LinearLayout.LayoutParams hintParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private MaterialButton action(String text, View.OnClickListener listener) {
        MaterialButton button = new MaterialButton(this);
        button.setText(text);
        button.setStyle(ButtonStyle.OUTLINED);
        button.setOnClickListener(listener);
        return button;
    }

    private TextView caption(String text) {
        TextView caption = new TextView(this);
        caption.setText(text);
        caption.setTextSize(12.0f);
        caption.setTypeface(android.graphics.Typeface.create(
                "sans-serif-medium", android.graphics.Typeface.NORMAL));
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

    private LinearLayout.LayoutParams buttonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(8.0f);
        return params;
    }

    private int dp(float valueDp) {
        return (int) (valueDp * getResources().getDisplayMetrics().density + 0.5f);
    }
}
