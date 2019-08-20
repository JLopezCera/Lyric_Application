package e.chuy7.spotifytrial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;


public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "";
    private static final String REDIRECT_URI = "simplespotifyintegration://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    private TextView current_song;
    private ImageView imageView;
    private ConstraintLayout layout;
    Button play, next, previous, repeat, shuffle, Lyrics;
    private int backgroundColor, textColor;

    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current_song = findViewById(R.id.songPlaying);
        play = findViewById(R.id.play_button);
        next = findViewById(R.id.next_button);
        previous = findViewById(R.id.previous_button);
        repeat = findViewById(R.id.repeat_Switch);
        shuffle = findViewById(R.id.shuffle_Switch);
        imageView = findViewById(R.id.imageView);
        layout = findViewById(R.id.whole_Layout);
        Lyrics = findViewById(R.id.lyricsTotal);
        findViewById(R.id.songPlaying).setSelected(true);
        final int imageSize = (int) getResources().getDimension(R.dimen.image_size);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .setPreferredImageSize(imageSize)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);
                    }
                });
    }


    private void connected() {
        mSpotifyAppRemote.getPlayerApi().resume();
        mSpotifyAppRemote.getPlayerApi().setShuffle(false);
        mSpotifyAppRemote.getPlayerApi().setRepeat(0);
        play.setText("Pause");
        shuffle.setText("Shuffling Off");
        repeat.setText("Repeating Off");
        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState().setEventCallback(playerState -> {
            final Track track = playerState.track;
            if (track != null) {
                current_song.setText(String.format("%s - %s - %s", track.artist.name, track.album.name, track.name));
                mSpotifyAppRemote.getImagesApi().getImage(playerState.track.imageUri).setResultCallback(bitmap -> imageView.setImageBitmap(bitmap));
                mSpotifyAppRemote.getImagesApi().getImage(playerState.track.imageUri).setResultCallback(this::createPaletteAsync);
            }

            play.setOnClickListener(view -> {
                if (playerState.isPaused) {
                    mSpotifyAppRemote.getPlayerApi().resume();
                    play.setText("Pause");
                }
                else {
                    mSpotifyAppRemote.getPlayerApi().pause();
                    play.setText("Play");
                }
            });
            repeat.setOnClickListener(view -> {
                if (playerState.playbackOptions.repeatMode == 0) {
                    repeat.setText("Repeating All");
                    mSpotifyAppRemote.getPlayerApi().setRepeat(2);
                }
                else if (playerState.playbackOptions.repeatMode == 2) {
                    repeat.setText("Repeating Song");
                    mSpotifyAppRemote.getPlayerApi().setRepeat(1);
                }
                else {
                    repeat.setText("Repeating Off");
                    mSpotifyAppRemote.getPlayerApi().setRepeat(0);
                }
            });
            shuffle.setOnClickListener(view -> {
                if (!playerState.playbackOptions.isShuffling){
                    shuffle.setText("Shuffling On");
                    mSpotifyAppRemote.getPlayerApi().setShuffle(true);
                }
                else {
                    shuffle.setText("Shuffling Off");
                    mSpotifyAppRemote.getPlayerApi().setShuffle(false);
                }
            });

            Lyrics.setOnClickListener(view -> {
                update_Lyrics(track.artist.name, track.name, backgroundColor, textColor);
            });
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate((palette -> {
            backgroundColor = palette.getDominantSwatch().getRgb();
            textColor = palette.getDominantSwatch().getBodyTextColor();
            Palette.Swatch screenBack = palette.getDarkVibrantSwatch();
            if (screenBack != null) {
                backgroundColor = screenBack.getRgb();
                textColor = screenBack.getBodyTextColor();
            }

            layout.setBackgroundColor(backgroundColor);
            current_song.setTextColor(textColor);
            play.setTextColor(textColor);
            next.setTextColor(textColor);
            previous.setTextColor(textColor);
            repeat.setTextColor(textColor);
            shuffle.setTextColor(textColor);
            getWindow().setNavigationBarColor(backgroundColor);
            Lyrics.setTextColor(textColor);
        }
        ));
    }

    public void update_Lyrics(String track, String artist,int backgroundColor, int textColor){
        Music music = new Music(artist, track, backgroundColor, textColor);
        Intent intent = new Intent(MainActivity.this, MusicActivity.class);
        intent.putExtra("Music", music);
        startActivity(intent);

    }
    public void next_Song(View view) {
        mSpotifyAppRemote.getPlayerApi().skipNext();
        play.setText("Pause");
    }

    public void previous_Song(View view) {
        mSpotifyAppRemote.getPlayerApi().skipPrevious();
        play.setText("Pause");
    }


}
