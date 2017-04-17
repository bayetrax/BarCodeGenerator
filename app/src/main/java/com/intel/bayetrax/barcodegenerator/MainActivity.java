package com.intel.bayetrax.barcodegenerator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ShapeAdapter adapter;
    List<Map<String, String>> groupData = new ArrayList<>();
    Map<String, String> groupMap = new HashMap<>();
    String[] groupFrom = new String[]{"GROUP"};
    int[] groupTo = new int[]{android.R.id.text1};

    List<List<Map<String, String>>> childData = new ArrayList<>();
    List<Map<String, String>> childList = new ArrayList<>();
    Map<String, String> childMap = new HashMap<>();
    String[] childFrom = new String[]{"CHILD"};
    int[] childTo = new int[]{android.R.id.text1};

    private String groupItems[] = {"Data matrix", "Linear barcodes", "2D barcodes"};
    private String[][] childItems = {{"ECC 200"},{"Code 39", "Code 128", "EAN-8", "ITF", "UPC-A"}, {"QR", "PDF-417"}};

    EditText data;
    EditText width;
    EditText height;
    BarcodeFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_listview);
        data = (EditText) findViewById(R.id.data);
        width = (EditText) findViewById(R.id.width);
        height = (EditText) findViewById(R.id.height);


        for (int groupCount = 0; groupCount < groupItems.length; groupCount++) {
            groupMap = new HashMap<>();
            groupMap.put(groupFrom[0], groupItems[groupCount]);

            childList = new ArrayList<>();
            for (int childCount = 0; childCount < childItems[groupCount].length; childCount++) {
                childMap = new HashMap<>();
                childMap.put(childFrom[0], childItems[groupCount][childCount]);
                childList.add(childMap);
            }


            childData.add(childList);

            groupData.add(groupMap);

        }


        adapter = new ShapeAdapter(this, groupData, android.R.layout.simple_expandable_list_item_1, groupFrom, groupTo,
                childData, android.R.layout.simple_expandable_list_item_2, childFrom, childTo);
        expandableListView.setAdapter(adapter);


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(MainActivity.this, BarcodeExampleActivity.class);
                String dataValue = String.valueOf(data.getText());
                intent.putExtra("data", dataValue.equals("")?"HATS SYSTEM":dataValue);
                int widthValue;
                try {
                    widthValue = Integer.parseInt(width.getText().toString());
                } catch (Exception e) {
                    widthValue = 800;
                }
                intent.putExtra("width", widthValue);
                int heightValue;
                try {
                    heightValue = Integer.parseInt(height.getText().toString());
                } catch (Exception e) {
                    heightValue = 800;
                }
                intent.putExtra("height", heightValue);
                switch (groupPosition) {
                    case 0:
                        switch (childPosition) {
                            case 0:
                                format = BarcodeFormat.DATA_MATRIX;
                            break;
                        }
                        break;
                    case 1:
                        switch (childPosition) {
                            case 0:
                                format = BarcodeFormat.CODE_39;
                                break;
                            case 1:
                                format = BarcodeFormat.CODE_128;
                                break;
                            case 2:
                                format = BarcodeFormat.EAN_8;
                                break;
                            case 3:
                                format = BarcodeFormat.ITF;
                                break;
                            case 4:
                                format = BarcodeFormat.UPC_A;
                                break;
                        }
                        break;
                    case 2:
                        switch (childPosition) {
                            case 0:
                                format = BarcodeFormat.QR_CODE;
                                break;
                            case 1:
                                format = BarcodeFormat.PDF_417;
                                break;
                        }
                        break;
                }

                intent.putExtra("BarcodeFormat", format);

                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    class ShapeAdapter extends SimpleExpandableListAdapter {

        public ShapeAdapter(Context context,
                            List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo,
                            List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
            super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        }


    }
}
