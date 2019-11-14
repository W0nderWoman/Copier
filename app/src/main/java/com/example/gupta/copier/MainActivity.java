package com.example.gupta.copier;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imgph,b_speak;
    TextView t_content;
    TextToSpeech obj;
    String b_sp_state="Speak";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgph=findViewById(R.id.img);
        t_content=findViewById(R.id.t_content);
        b_speak=findViewById(R.id.b_speak);

        //text to speech object
        obj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    obj.setLanguage(Locale.UK);
                }
            }
        });
    }

    public void capture(View view){
        final CharSequence[] options = { "Capture", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Add Image!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Capture")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imgph.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imgph.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    public void speak(View view){
        if(b_sp_state.equals("Speak")){
            String text=t_content.getText().toString();

            //obj.speak(text,TextToSpeech.QUEUE_FLUSH,null);

            //TODO----add animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Path path = new Path();
                path.arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true);
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.X, View.Y, path);
                animator.setDuration(2000);
                animator.start();
            } else {
                // Create animator without using curved path
            }

            //change to pause button
            b_speak.setImageDrawable(getResources().getDrawable(R.drawable.pausecirclebutton));
            b_sp_state="Pause";
        }
        else if(b_sp_state.equals("Pause")){
            //change to play button
            b_speak.setImageDrawable(getResources().getDrawable(R.drawable.playcirclebutton));
            b_sp_state="Play";
        }
    }
}
//TODO----Fab drawer
//TODO----adding camera permission in manifest causing error
//TODO----change speak button symbols to pause, stop