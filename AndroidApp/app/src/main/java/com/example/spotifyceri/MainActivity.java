package com.example.spotifyceri;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spotifyceri.Demo.Song;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener{

    // constant for storing audio permission
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    RecyclerView recyclerView;
    TextView noMusicTextView;
    //ftp://206.81.26.59
    ImageButton Record;
    String stream="http://134.209.226.109:8080";
    MediaPlayer mediaPlayer;
    MediaRecorder mRecorder;
    EditText search;
    IceServers api = new IceServers();

    // string variable is created for storing a file name
    private static String mFileName = null;
    RecyclerViewAdapter adapter;
    ProgressBar progressBar;
    List<Song> fetched = new ArrayList<>(Arrays.asList(api.server1.getAllSongs()));
    List<Song> recyclerList = new ArrayList<>();

    // creating a variable for exoplayerview.
    SimpleExoPlayerView exoPlayerView;

    // creating a variable for exoplayer
    SimpleExoPlayer exoPlayer;
    AudioManager audioManager;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark, this.getTheme()));
        setContentView(R.layout.activity_main);
        Log.d("CURRENTDIR", String.valueOf(getFilesDir()));
        noMusicTextView = findViewById(R.id.no_songs_text);
        Record = findViewById(R.id.record_btn);
        Play = findViewById(R.id.play_pause_button);
        //progressBar = findViewById(R.id.preparing_progress);
        //progressBar.setVisibility(View.VISIBLE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Play.setEnabled(false);
        Play.setText("Loading...");
        search = findViewById(R.id.search_text);
        for(Song s : fetched) {
            Log.d("SONGS FETCHED", "Song{" +
                    "id=" + s.rowid +
                    ", titre='" + s.titre + '\'' +
                    ", artiste='" + s.artiste + '\'' +
                    ", path='" + s.path + '\'' +
                    ", album='" + s.album + '\'' +
                    ", duration=" + s.duration +
                    '}');
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerList.addAll(fetched);
        adapter = new RecyclerViewAdapter(this, recyclerList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(stream);
            Toast toast=Toast.makeText(getApplicationContext(),"URL Set",Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Toast toast=Toast.makeText(getApplicationContext(),"Prepared",Toast.LENGTH_SHORT);
                toast.setMargin(50,50);
                toast.show();
                //progressBar.setVisibility(View.GONE);
                Play.setEnabled(true);
                mediaPlayer.start();
                Play.setText("Pause");
            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("tap");
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Play.setText("Play");
                }
                else {
                    mediaPlayer.start();
                    Play.setText("Pause");
                }
            }
        });

        Record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("RECORD_KEY_EVENT", "BOUTON APPUYE");
                        startRecording();
                        mediaPlayer.pause();
                        Play.setText("Play");
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d("RECORD_KEY_EVENT", "BOUTON RELACHE");
                        stopRecording();
                        break;
                }
                return false;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerList.clear();
                if(charSequence.length()>0) {
                    recyclerList.addAll((ArrayList<Song>) fetched.stream().filter(song -> (song.titre + song.artiste).toLowerCase().contains(charSequence.toString().toLowerCase())).collect(Collectors.toList()));
                    if(recyclerList.size()<1){
                        noMusicTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        noMusicTextView.setVisibility(View.GONE);
                    }
                }
                else {
                    recyclerList.addAll(fetched);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.dark, this.getTheme()));
        setContentView(R.layout.activity_main);
        Log.d("CURRENTDIR", String.valueOf(getFilesDir()));
        noMusicTextView = findViewById(R.id.no_songs_text);
        Record = findViewById(R.id.record_btn);
        //Play = findViewById(R.id.play_pause_button);
        //progressBar = findViewById(R.id.preparing_progress);
        //progressBar.setVisibility(View.VISIBLE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //Play.setEnabled(false);
        //Play.setText("Loading...");
        search = findViewById(R.id.search_text);
        for(Song s : fetched) {
            Log.d("SONGS FETCHED", "Song{" +
                    "id=" + s.rowid +
                    ", titre='" + s.titre + '\'' +
                    ", artiste='" + s.artiste + '\'' +
                    ", path='" + s.path + '\'' +
                    ", album='" + s.album + '\'' +
                    ", duration=" + s.duration +
                    '}');
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerList.addAll(fetched);
        adapter = new RecyclerViewAdapter(this, recyclerList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        exoPlayerView = findViewById(R.id.idExoPlayerVIew);
        try {

            initPlayer(stream);

        } catch (Exception e) {
            // below line is used for
            // handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }

        /*Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("tap");
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    Play.setText("Play");
                }
                else {
                    mediaPlayer.start();
                    Play.setText("Pause");
                }
            }
        });*/
        Record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("RECORD_KEY_EVENT", "BOUTON APPUYE");
                        startRecording();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d("RECORD_KEY_EVENT", "BOUTON RELACHE");
                        stopRecording();
                        break;
                }
                return false;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerList.clear();
                if(charSequence.length()>0) {
                    recyclerList.addAll((ArrayList<Song>) fetched.stream().filter(song -> (song.titre + song.artiste).toLowerCase().contains(charSequence.toString().toLowerCase())).collect(Collectors.toList()));
                    if(recyclerList.size()<1){
                        noMusicTextView.setVisibility(View.VISIBLE);
                    }
                    else {
                        noMusicTextView.setVisibility(View.GONE);
                    }
                }
                else {
                    recyclerList.addAll(fetched);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initPlayer(String url) {
        // bandwisthmeter is used for
        // getting default bandwidth
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        // track selector is used to navigate between
        // video using a default seekbar.
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

        // we are adding our track selector to exoplayer.
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        // we are parsing a video url
        // and parsing its video uri.
        Uri videouri = Uri.parse(url);

        // we are creating a variable for datasource factory
        // and setting its user agent as 'exoplayer_view'
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_view");

        // we are creating a variable for extractor factory
        // and setting it to default extractor factory.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // we are creating a media source with above variables
        // and passing our event handler as null,
        MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

        // inside our exoplayer view
        // we are setting our player
        exoPlayerView.setPlayer(exoPlayer);

        // we are preparing our exoplayer
        // with media source.
        exoPlayer.prepare(mediaSource);

        // we are setting our exoplayer
        // when it is ready.
        exoPlayer.setPlayWhenReady(true);
    }

    private void stopRecording() {
        // below method will stop
        // the audio recording.
        mRecorder.stop();
        // below method will release
        // the media recorder class.
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        File speechFile = new File(mFileName);
        byte[] bytesArray;
        exoPlayer.setPlayWhenReady(true);
        Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG).show();
        if(speechFile.canRead()){
            try {
                bytesArray = Files.readAllBytes(Paths.get(mFileName));
                String speech = api.asrServer.recognize(bytesArray);
                Log.d("ASR", speech);
                if(!TextUtils.isEmpty(speech)){
                    String command = api.nlpServer.process(speech.toLowerCase());
                    Toast toast=Toast.makeText(getApplicationContext(),"Command = "+ command,Toast.LENGTH_SHORT);
                    toast.show();
                    Log.d("NLP", command);
                    if(command.startsWith("lancer+")){
                        String songTitle=command.replaceAll("lancer\\+","");
                        Log.d("NLP", "songTitle="+songTitle);
                        ArrayList<Song> filteredSongList= (ArrayList<Song>) fetched.stream().filter(song -> (song.titre+song.artiste).toLowerCase().contains(songTitle.toLowerCase())).collect(Collectors.toList());
                        if(filteredSongList.size()>0){
                            Log.d("NLP", filteredSongList.get(0).titre);
                            api.serverVLC.playSong(filteredSongList.get(0).rowid);
                            toast=Toast.makeText(getApplicationContext(),"Commande = lancer la musique"+ filteredSongList.get(0).titre,Toast.LENGTH_SHORT);
                            exoPlayer.release();
                            initPlayer(stream);
                            toast.show();
                        }
                        else {
                            Log.d("NLP", "Aucun titre trouve");
                            toast=Toast.makeText(getApplicationContext(),"Aucune musique trouvée, veuillez réessayer",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    else if(command.equals("monter")){
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if((currentVolume + 6) < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 6, 0);
                        }
                        else {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        }
                        toast=Toast.makeText(getApplicationContext(),"Commande = Monter le volume. Nouveau volume = "+ audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(command.equals("baisser")){
                        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        if((currentVolume - 6) > audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC)) {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - 6, 0);
                        }
                        else {
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC), 0);
                        }
                        toast=Toast.makeText(getApplicationContext(),"Commande = Biasser le volume. Nouveau volume = "+ audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(command.equals("couper")){
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC), 0);
                        toast=Toast.makeText(getApplicationContext(),"Commande = Couper le volume",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(command.equals("max")){
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                        toast=Toast.makeText(getApplicationContext(),"Commande = Mettre le volume au maximum",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(command.equals("pause")){
                        exoPlayer.setPlayWhenReady(false);
                        toast=Toast.makeText(getApplicationContext(),"Commande = Mettre pause",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(command.equals("play")){
                        exoPlayer.setPlayWhenReady(true);
                        toast=Toast.makeText(getApplicationContext(),"Commande = Mettre play",Toast.LENGTH_SHORT);
                        toast.show();

                    }
                    else {
                        Log.d("NLP", "dont start with =" + command);
                    }
                }
                else {
                    Log.d("NLP", "Aucune instruction trouvée");
                    Toast toast=Toast.makeText(getApplicationContext(),"Je n'ai pas compris votre commande, veuillez réessayer.",Toast.LENGTH_SHORT);
                    toast.show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG).show();
    }

    private void startRecording() {
        if (CheckPermissions()) {
            exoPlayer.setPlayWhenReady(false);
            mFileName = getExternalFilesDir( null).getAbsolutePath();
            mFileName += "/speech.wav";

            mRecorder = new MediaRecorder();

            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(96000);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            mRecorder.setOutputFile(mFileName);
            try {
                // below method will prepare
                // our audio recorder class
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("TAG", "prepare() failed");
            }
            // start method will start
            // the audio recording.
            mRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
        } else {
            RequestPermissions();
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onItemClick(View view, int position) {
        exoPlayer.release();
        initPlayer(stream);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}