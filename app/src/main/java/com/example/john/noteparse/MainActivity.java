package com.example.john.noteparse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.noteparse.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Comparator;

import static android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;

public class MainActivity extends AppCompatActivity {
    String back_s;
    ArrayList<ViewSpan> spans = new ArrayList<>();
    private static int RESULT_LOAD_IMG = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button load = findViewById(R.id.btn_load);
        Button highlight = findViewById(R.id.btn_highlight);
        Button bold = findViewById(R.id.btn_bold);
        Button italic = findViewById(R.id.btn_italic);
        Button save = findViewById(R.id.btn_save);
        Button web = findViewById(R.id.btn_web);

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                startActivity(intent);
            }
        });

        highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString str = new SpannableString(et.getText());


                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();

                ViewSpan span = new ViewSpan();
                span.start=start;
                span.end = end;
                span.type = 'h';
                spans.add(span);
                Toast.makeText(getApplicationContext(), start+" "+end + " "+'h', Toast.LENGTH_LONG)
                        .show();

                str.setSpan(new BackgroundColorSpan(Color.YELLOW),start,end,0);
                et.setText(str, TextView.BufferType.SPANNABLE);


            }
        });
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString str = new SpannableString(et.getText());


                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();
                ViewSpan span = new ViewSpan();
                span.start=start;
                span.end = end;
                span.type ='b';
                spans.add(span);
                Toast.makeText(getApplicationContext(), start+" "+end + " "+'b', Toast.LENGTH_LONG)
                        .show();
                str.setSpan(new StyleSpan(Typeface.BOLD),start,end,0);

                et.setText(str, TextView.BufferType.SPANNABLE);
            }
        });
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString str = new SpannableString(et.getText());


                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();


                ViewSpan span = new ViewSpan();
                span.start=start;
                span.end = end;
                span.type ='i';
                spans.add(span);
                Toast.makeText(getApplicationContext(), start+" "+end + " "+'i', Toast.LENGTH_LONG)
                        .show();
                str.setSpan(new StyleSpan(Typeface.ITALIC),start,end,0);
                et.setText(str, TextView.BufferType.SPANNABLE);
            }
        });

        /*
        * This needs some adjusting to get the first bit to copy
        * 
        * */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comparator<ViewSpan> c = new Comparator<ViewSpan>() {
                    @Override
                    public int compare(ViewSpan item, ViewSpan t1) {
                        if(item.start == t1.start){
                            return 0;
                        }else if(item.start > t1.start){
                            return 1;
                        }else {
                            return -1;
                        }
                    }
                };
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString rough =new SpannableString(et.getText());

                
                // saveable = new StringBuilder();
                //ViewSpan temp = new ViewSpan();
               //ArrayList<ViewSpan> vs = new ArrayList<>();
                //saveable.append("");
                String htmlString = Html.toHtml(rough, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                et.setText(htmlString, TextView.BufferType.SPANNABLE);
        /*
                if(spans.size()==0){
                    et.setText(rough);
                }else{
                    spans.sort(c);

                    if(spans.get(0).start != 0) {
                        temp.type = 'n';
                        temp.start = 0;
                        temp.end = spans.get(0).start-2;
                        spans.add(temp);
                        spans.sort(c);
                    }

                    for (int i = 0; i < spans.size() - 1; i++) {
                        temp.start = spans.get(i).end + 1;
                        temp.end = spans.get(i + 1).start-2;
                        temp.type='n';
                        vs.add(temp);

                    }

                    if(spans.get(spans.size()-1).end != rough.length()) {
                        temp.type = 'n';
                        temp.start = spans.get(spans.size()-1).end+1;
                        temp.end = rough.length();
                        vs.add(temp);
                    }
                    spans.addAll(vs);
                    spans.sort(c);
                    //now to copy words and whatnot

                    for(int i =0;i<spans.size()-1;i++){
                        if(spans.get(i).type == 'n'){
                            saveable.append(rough.substring(spans.get(i).start,spans.get(i).end));
                        }else {
                            saveable.append("<"+spans.get(i).type+">"+rough.substring(spans.get(i).start,spans.get(i).end)+"</"+spans.get(i).type+">");
                        }
                    }

                    et.setText(saveable.toString());
                }
                */
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
                  back_s = s;
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
