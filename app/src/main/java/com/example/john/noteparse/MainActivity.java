package com.example.john.noteparse;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import static android.text.Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE;

public class MainActivity extends AppCompatActivity {
    String HTML;
    private static int RESULT_LOAD_IMG = 1;
    private String shortFileName = "";
    private String path = Environment.getExternalStorageDirectory().getPath()+"/TextParserFolder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button highlight = findViewById(R.id.btn_highlight);
        Button bold = findViewById(R.id.btn_bold);
        Button italic = findViewById(R.id.btn_italic);
        Button save = findViewById(R.id.btn_save);
        Button web = findViewById(R.id.btn_web);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        /*
        * Creates the on click listener that converts the current text to html and opens it into a
        * web browser.
        * */
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString rough = new SpannableString(et.getText());
                String htmlString = Html.toHtml(rough, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                HTML = "<html><body>" + htmlString + "</body></html>";
                Intent intent = new Intent(context, Web.class);
                intent.putExtra("HTML", HTML);
                startActivity(intent);
            }
        });
        /*
        * Highlights the currently selected text and saves it to a spannable string
        * */
        highlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString str = new SpannableString(et.getText());
                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();
                str.setSpan(new BackgroundColorSpan(Color.YELLOW), start, end, 0);
                et.setText(str, TextView.BufferType.SPANNABLE);


            }
        });
        /*
        * emboldens the selected text
        * */
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString str = new SpannableString(et.getText());
                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();
                str.setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
                et.setText(str, TextView.BufferType.SPANNABLE);
            }
        });
        /*
        * italicizes the selected text
        * */
        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString str = new SpannableString(et.getText());
                int start = et.getSelectionStart();
                int end = et.getSelectionEnd();
                str.setSpan(new StyleSpan(Typeface.ITALIC), start, end, 0);
                et.setText(str, TextView.BufferType.SPANNABLE);
            }
        });

        /*
        * converts the current text to HTML and saves to a file that you can name
        * 
        * */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = (EditText) findViewById(R.id.textView);
                SpannableString rough = new SpannableString(et.getText());
                String htmlString = Html.toHtml(rough, TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
                HTML = "<html><body>" + htmlString + "</body></html>";

                AlertDialog.Builder fileDialog = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.filename_layout, null);
                fileDialog.setView(dialogView);
                fileDialog.setTitle("Input File Name");
                fileDialog.setCancelable(true);

                final EditText editText = dialogView.findViewById(R.id.filename_et);

                fileDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shortFileName = editText.getText().toString().trim();
                        //Log.i("shortfile2",shortFileName);
                        if (shortFileName.matches("")) {

                            Toast.makeText(getApplicationContext(), "Please Try Again and Input A File Name", Toast.LENGTH_SHORT).show();
                        }
                        String fileName = shortFileName + ".html";
                        saveFile(fileName, HTML, path);

                    }
                });
                fileDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        shortFileName = DateFormat.format("dd_MM_yyyy_hh_mm_ss", System.currentTimeMillis()).toString();
                        String fileName = shortFileName + ".html";
                        saveFile(fileName, HTML, path);

                    }
                });
                fileDialog.create();
                fileDialog.show();
            }
        });


    }
    /*
    * Starts an intent to select an image from the gallery
    * */
    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    /*
    * returns from the gallery and sets up the parser to extract text from the chosen image
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && data != null) {
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
                } catch (Exception e) {
                    Toast.makeText(this, "Text Detector Failed to Detect Text", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    /*
    * saves to a file named whatever you enter in the TextParser directory
    * */
    public void saveFile(String HTMLfileName, String HTMLContentString, String PathName) {
        File file = new File(PathName, HTMLfileName);
        Log.i("filename", PathName + HTMLfileName);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.i("working", "we are into permission check");
            File directory = new File(PathName);
            if (! directory.exists()){
                directory.mkdirs();
            }

            try {
                FileOutputStream out = new FileOutputStream(file);
                byte[] data = HTMLContentString.getBytes();
                out.write(data);
                out.close();
                Log.i("fileName", "File Save : " + file.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
       }


    }
    /*
    * inflater for the load menu
    * */
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    /*
    * loads the selected file from the directory into a webview
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        {
            //statements seem to be working, highscore is untested'ish' because i did not want to mess with the layout (as it appears blank)
            if(item.getItemId()== R.id.Load_File){
                File fileDirectory = new File(path);
                String[] listFiles = fileDirectory.list();
                Arrays.sort(listFiles);

                AlertDialog.Builder loadDialog = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View loadView = (View)  inflater.inflate(R.layout.dialog_list, null);
                loadDialog.setView(loadView);
                loadDialog.setTitle("Files:");
                ListView listView = (ListView)loadView.findViewById(R.id.listView);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listFiles);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selected = adapter.getItem(position);

                        try {
                            File fl = new File(path+"/"+selected);
                            FileInputStream fin = new FileInputStream(fl);
                            String ret = convertStreamToString(fin);
                            //Make sure you close all streams.
                            Toast.makeText(getApplicationContext(), ret, Toast.LENGTH_SHORT).show();

                            fin.close();
                            Intent intent = new Intent(getApplicationContext(), Web.class);
                            intent.putExtra("HTML", ret);
                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                loadDialog.create();
                loadDialog.show();


            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * This gets our permissions
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("request", String.valueOf(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)));
                } else {

                }
                return;
            }
        }
    }
    /*
    * a helper funtion to convert a stream to a string
    * */
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}