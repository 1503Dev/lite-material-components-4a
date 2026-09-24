package dev1503.litematerial.lmc4a.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String[] TITLES = {
            "MaterialButton",
            "MaterialSwitch",
            "MaterialSlider",
            "MaterialRangeSlider",
            "MaterialRadioButton",
            "MaterialBottomSheet",
            "MaterialDialog",
            "MaterialNavigationRail",
            "MaterialFloatingActionButton",
            "MaterialCardView",
            "MaterialTopAppBar",
            "MaterialTextField",
    };

    private static final Class<?>[] DEMO_CLASSES = {
            dev1503.litematerial.lmc4a.app.demo.ButtonDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.SwitchDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.SliderDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.RangeSliderDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.RadioButtonDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.BottomSheetDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.DialogDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.NavigationRailDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.FloatingActionButtonDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.CardViewDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.TopAppBarDemoActivity.class,
            dev1503.litematerial.lmc4a.app.demo.TextFieldDemoActivity.class,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchemeHelper.applyWindowBackground(this);
        ListView listView = new ListView(this);
        listView.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, TITLES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(SchemeHelper.onBackgroundColor());
                return view;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, DEMO_CLASSES[position]));
            }
        });
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(listView);
    }
}
