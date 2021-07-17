package io.github.ornelasf1.soundboard;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

public class SoundsActivity extends AppCompatActivity {
    //final MediaPlayer soundOne = MediaPlayer.create(this, R.raw.jacked);
    int confirmInt = 0;
    MediaPlayer soundFromExtern = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sounds);

        TextView title = (TextView) findViewById(R.id.soundsTitle);
        Typeface solid_font = Typeface.createFromAsset(getAssets(), "fonts/FREEDOM.otf");
        title.setTypeface(solid_font);

        importRawSounds();
        initTable();
    }

    public void initTable() {
        final TableLayout scrollTb = (TableLayout) findViewById(R.id.soundsTable);
        File projectDir = new File(getBaseContext().getPackageName() + "/raw");
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard");
        File[] files = dir.listFiles();

        for (int i = 0; i < files.length; i++) {
            TableRow tbRows = new TableRow(this);

            final ViewSwitcher renSwitcher = new TextSwitcher(this);
            final TextView tbNames = new TextView(this);
            final EditText tbRenNames = new EditText(this);
            final String nameOfFile = files[i].getName();
            String stripName = nameOfFile.substring(0, nameOfFile.lastIndexOf("."));
            tbNames.setText(stripName);
            tbNames.setTextColor(Color.WHITE);
            tbNames.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tbNames.setTextSize(30);
            tbRenNames.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tbRenNames.setTextSize(30);
            tbRenNames.setWidth(300);
            tbRenNames.setSingleLine(true);
            renSwitcher.addView(tbNames);
            renSwitcher.addView(tbRenNames);
            tbRows.addView(renSwitcher);

            Button tbPlayButts = new Button(this);
            tbPlayButts.setText(R.string.playButt);

            tbPlayButts.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    soundFromExtern = new MediaPlayer();
                    //soundFromRaw.create(getApplicationContext(), getResources().getIdentifier(nameOfFile, "SoundBoard", getPackageName()));
                    try {
                        soundFromExtern.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard/" + nameOfFile);
                        soundFromExtern.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    soundFromExtern.start();
                    soundFromExtern.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                }
            });


            //releaseMedia(soundFromExtern);

            final Button tbDelButts = new Button(this);
            tbDelButts.setText(R.string.delButt);
            tbDelButts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tbDelButts.setText("You sure?");
                    confirmInt++;
                    if (confirmInt == 2) {
                        File fileDel = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard/" + nameOfFile);
                        fileDel.delete();
                        scrollTb.removeAllViewsInLayout();
                        importRawSounds();
                        initTable();
                        confirmInt = 0;
                    }
                }
            });

            final Button tbRenButts = new Button(this);
            tbRenButts.setText(R.string.renButt);
            tbRenButts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renSwitcher.showNext();
                    String newName = tbRenNames.getText().toString();
                    if (newName.length() != 0) {
                        tbNames.setText(newName);
                        File oldFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard", nameOfFile);
                        File newFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard", newName + ".3gp");
                        oldFileName.renameTo(newFileName);
                    }

                }
            });
            tbRows.addView(tbRenButts);
            tbRows.addView(tbDelButts);
            tbRows.addView(tbPlayButts);
            scrollTb.addView(tbRows);

        }
    }

    public void importRawSounds() {
        File dir = new File(getBaseContext().getPackageName() + "/raw/");
        File[] listOfFiles = dir.listFiles();
        Field[] fields = R.raw.class.getFields();
        //File externDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard");
        for (int i = 0; i < fields.length; i++) {
            TableLayout scrollTb = (TableLayout) findViewById(R.id.soundsTable);
            TableRow tbRows = new TableRow(this);
            TextView tbNames = new TextView(this);
            tbNames.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tbNames.setTextSize(30);
            tbNames.setText(fields[i].getName());
            tbNames.setTextColor(Color.WHITE);
            tbRows.addView(tbNames);
            Button tbPlayButts = new Button(this);
            tbPlayButts.setText(R.string.playButt);
            ;
            try {
                int filePos = fields[i].getInt(fields[i]);
                final MediaPlayer soundFromRaw = MediaPlayer.create(this, filePos);
                tbPlayButts.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!soundFromRaw.isPlaying()) {
                            soundFromRaw.start();
                            /*soundFromRaw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });*/
                        }
                    }
                });
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            tbRows.addView(tbPlayButts);
            scrollTb.addView(tbRows);
        }

    }

    public boolean isPlaying(MediaPlayer audio) {
        if (audio.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    /*@Override
    public void onPause() {
        super.onPause();
        soundFromExtern.stop();
        soundFromExtern.release();
        soundFromExtern = null;
    }*/
}
    /*public void onClickButton_1(View v){
        final MediaPlayer soundOne = MediaPlayer.create(this, R.raw.jacked);
        soundOne.start();
    }

    public void onClickButton_2(View v){
        final MediaPlayer soundTwo = MediaPlayer.create(this, R.raw.bootleg_fireworks);
        soundTwo.start();
    }

    public void onClickButton_3(View v){
        final MediaPlayer soundThree = MediaPlayer.create(this, R.raw.piernotass);
        soundThree.start();
    }

    public void onClickButton_4(View v){
        final MediaPlayer soundFour = MediaPlayer.create(this, R.raw.milk);
        soundFour.start();
    }
*/
