package com.intel.bayetrax.barcodegenerator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*public class BarcodeExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_example);
    }
}*/

import java.util.EnumMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;

public class BarcodeExampleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout l = new LinearLayout(this);
        l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        l.setOrientation(LinearLayout.VERTICAL);
        l.setGravity(Gravity.CENTER);

        setContentView(l);

        Intent intent = getIntent();
        // barcode data
        String barcode_data = intent.getStringExtra("data");
        Log.d("XXX", "barcode_data ="+barcode_data);
        int width = (intent.getIntExtra("width", 600));
        Log.d("XXX", "width ="+String.valueOf(width));
        int height = (intent.getIntExtra("height", 600));
        Log.d("XXX", "height ="+String.valueOf(height));
        BarcodeFormat format = (BarcodeFormat) intent.getSerializableExtra("BarcodeFormat");
        Log.d("XXX", "format ="+String.valueOf(format));

        // barcode image
        Bitmap bitmap = null;
        ImageView iv = new ImageView(this);

        try {

            bitmap = encodeAsBitmap(barcode_data, format, width, height);
            iv.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        l.addView(iv);

        //barcode text
        TextView tv = new TextView(this);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(barcode_data);

        l.addView(tv);

    }

    /**************************************************************
     * getting from com.google.zxing.client.android.encode.QRCodeEncoder
     *
     * See the sites below
     * http://code.google.com/p/zxing/
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/EncodeActivity.java
     * http://code.google.com/p/zxing/source/browse/trunk/android/src/com/google/zxing/client/android/encode/QRCodeEncoder.java
     */

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);

        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            Log.d("XXX4", "Unsupported format -" + iae);
            Toast.makeText(this, iae.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return null;
        } catch (Exception e) {
            Toast.makeText(this, "Sorry Buddy - Try Changing Content", Toast.LENGTH_LONG).show();
            return null;
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];


        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        if (format == BarcodeFormat.DATA_MATRIX) {
            bitmap = drawScaledBitmap(bitmap, new Size(img_width, img_height));
        }
        return bitmap;


    }

    public Bitmap drawScaledBitmap(Bitmap bitmap, Size size) {
        final int bmWidth = bitmap.getWidth();
        final int bmHeight = bitmap.getHeight();
        final int wScalingFactor = size.getWidth() / bmWidth;
        final int hScalingFactor = size.getHeight() / bmHeight;
        final int scalingFactor = Math.min(wScalingFactor, hScalingFactor);
        return scalingFactor > 1 ? Bitmap.createScaledBitmap(bitmap, bmWidth * scalingFactor, bmHeight * scalingFactor, false) : bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

}