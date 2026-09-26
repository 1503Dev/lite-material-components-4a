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

public class V3EActivity extends Activity {

    private static final String[] TITLES = {
            "MaterialButton",
    };

    private static final Class<?>[] DEMO_CLASSES = {
            dev1503.litematerial.lmc4a.app.demo.v3e.ButtonDemoActivity.class,
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
                startActivity(new Intent(V3EActivity.this, DEMO_CLASSES[position]));
            }
        });
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(listView);
    }
}
