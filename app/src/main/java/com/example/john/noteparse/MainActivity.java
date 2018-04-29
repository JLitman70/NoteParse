package com.example.john.noteparse;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button load = findViewById(R.id.btn_load);
        Button highlight = findViewById(R.id.btn_highlight);
        Button copyText = findViewById(R.id.btn_copy);



        copyText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                EditText et = (EditText) findViewById(R.id.textView);

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(text, et);
                clipboard.setPrimaryClip(clip);
            }
        });

        highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableStringBuilder str = (SpannableStringBuilder) et.getText();


                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();

                str.setSpan(new BackgroundColorSpan(Color.YELLOW),start,end,0);
                et.setText(str);
            }
        });

    }
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG
                    && data != null) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Context context = getApplicationContext();
                //Instantiating the text recognizer and setting it's processor to the  custom processor.
                TextRecognizer textDetector = new TextRecognizer.Builder(context).build();
                textDetector.setProcessor(new TextDetector());
                //this is a bitmap used for testing, it will be replaced with the bitmap we select.
                //creates a frame builder and creates a frame from the bitmap.
                Frame.Builder fb = new Frame.Builder();
                fb.setBitmap(bitmap);
                Frame frame = fb.build();
                //detects textdata from the frame and stuffs it in the sparse array, then puts it in a string
              try {
                  SparseArray<TextBlock> textBlocks = textDetector.detect(frame);
                  String s = textBlocks.get(0).getValue();
                  //creating a testview to show the test
                  EditText et = (EditText) findViewById(R.id.textView);
                  et.setText(s);
              }catch (Exception e){
                  Toast.makeText(this, "Text Detector Failed to Detect Text", Toast.LENGTH_LONG)
                          .show();
              }

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

}
