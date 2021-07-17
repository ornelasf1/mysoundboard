package io.github.ornelasf1.soundboard;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class customActivity extends AppCompatActivity {
    private String LT = "LOG";

    Button buttonStart, buttonStop, buttonPlay, buttonStopRecording, buttonDelete;
    TextView labelRecordStatus;
    EditText newFileName;
    String filename, inputFilename;
    int numOfFiles;
    String path = null;
    MediaRecorder recorder;
    MediaPlayer player;
    public static final int RequestPermissionCode = 1;
    public static int amtOfRecords = 0;
    private boolean timerStatus = false;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        //path = "android.resource://" + getPackageName() + "raw/AudioRecording.3gp";

        buttonStart = (Button)findViewById(R.id.bStart);
        buttonStop = (Button)findViewById(R.id.bStop);
        buttonPlay = (Button)findViewById(R.id.bPlay);
        buttonStopRecording = (Button)findViewById(R.id.bStopRecording);
        buttonDelete = (Button)findViewById(R.id.bDiscard);
        labelRecordStatus = (TextView)findViewById(R.id.recordingStatus);
        newFileName = (EditText)findViewById(R.id.editFileName);

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard");
        directory.mkdirs();
        File[] files = directory.listFiles();
        numOfFiles = files.length;

        buttonStop.setEnabled(false);
        buttonPlay.setEnabled(false);
        buttonStopRecording.setEnabled(false);
        buttonDelete.setEnabled(false);

        labelRecordStatus.setText("-Start Recording-");
        //recordAudio("test");
    }

    public void log(String s){
        Log.d(LT, "customActivity: " + s);
    }

    public void clickStart(View v){
        log("clickedStart here");
        if(checkPermission()) {
            labelRecordStatus.setText("-Recording...-");
            newFileName.setEnabled(false);
            createPath();
            mediaRecorderReady();
            try {
                recorder.prepare();
                recorder.start();
                timedRecording();
            } catch (IOException e) {
                e.printStackTrace();
            }
            buttonStart.setEnabled(false);
            buttonPlay.setEnabled(false);
            buttonStop.setEnabled(true);
        }else{
            requestPermission();
        }
    }

    public void timedRecording(){
        timerStatus = true;

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                timerStatus = false;
                clickStop(null);
            }
        };

        handler.postDelayed(runnable, 10000);
    }

    public void clickStop(View v){
        log("clicked Stop here");
        if(timerStatus){
            handler.removeCallbacks(runnable);
            timerStatus = false;
        }
        recorder.stop();
        buttonStop.setEnabled(false);
        buttonPlay.setEnabled(true);
        buttonStart.setEnabled(false);
        buttonStopRecording.setEnabled(true);
        buttonDelete.setEnabled(true);
        labelRecordStatus.setText("- -");
    }

    public void clickPlay(View v){
        log("clicked play here");
        labelRecordStatus.setText("-Playing-");
        buttonStop.setEnabled(false);
        buttonStart.setEnabled(false);
        buttonStopRecording.setEnabled(true);

        player = new MediaPlayer();
        try{
            player.setDataSource(path);
            player.prepare();
        }catch(IOException e){
            e.printStackTrace();
        }

        player.start();
        buttonPlay.setEnabled(false);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonPlay.setEnabled(true);
                labelRecordStatus.setText("- -");
            }
        });
    }

    public void clickStopRecording(View v){
        log("clicked stop recording");
        labelRecordStatus.setText("-Entry Saved-");
        buttonStop.setEnabled(false);
        buttonStart.setEnabled(true);
        buttonStopRecording.setEnabled(false);
        buttonPlay.setEnabled(true);
        buttonDelete.setEnabled(false);
        if(player != null){
            log("player is not null, player and recorder are released");
            player.stop();
            player.release();
            recorder.release();
            recorder=null;
            mediaRecorderReady();
            buttonPlay.setEnabled(false);
            newFileName.setEnabled(true);
        }
    }

    public void clickDelete(View v){
        log("clicked delete");
        buttonPlay.setEnabled(false);
        buttonStopRecording.setEnabled(false);
        buttonStart.setEnabled(true);
        buttonDelete.setEnabled(false);
        File fileDel = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard/" + filename + ".3gp");
        fileDel.delete();
        newFileName.setEnabled(true);
        if(player != null) {
            player.release();
        }
    }

    public void createPath(){
        inputFilename = newFileName.getText().toString();
        if(inputFilename.length() == 0){
            filename = "customSound_" + Integer.toString(numOfFiles+1);
        }else{
            filename = inputFilename;
        }
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundBoard/" + filename + ".3gp";
    }

    public void mediaRecorderReady(){
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        recorder.setOutputFile(path);
    }

    public void requestPermission(){
        log("requesting for permission");
        ActivityCompat.requestPermissions(customActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    log("Permission was granted");
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        labelRecordStatus.setText("Permission Granted");
                    } else {
                        labelRecordStatus.setText("Permission Denied");
                    }
                }
                break;
        }
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }


    /*public void recordAudio(String fileName) {
        final MediaRecorder recorder = new MediaRecorder();
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile("C:\\Users\\Defsin\\Documents\\android_workspace\\SoundBoard\\app\\src\\main\\res\\raw\\" + fileName);
        try {
            recorder.prepare();
        } catch (Exception e){
            e.printStackTrace();
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(customActivity.this);
        mProgressDialog.setTitle(R.string.lbl_recording);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setButton("Stop recording", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mProgressDialog.dismiss();
                recorder.stop();
                recorder.release();
            }
        });

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface p1) {
                recorder.stop();
                recorder.release();
            }
        });
        recorder.start();
        mProgressDialog.show();
    }*/
}
