package e.chuy7.spotifytrial;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.util.concurrent.ExecutionException;

import e.chuy7.spotifytrial.Music;
import e.chuy7.spotifytrial.R;

public class MusicActivity extends AppCompatActivity {

    private static final String MUSIC_MATCH_KEY = "";

    private TextView lyricsText;
    private MusixMatch musixMatch;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        layout = findViewById(R.id.linearLayout);
        lyricsText = findViewById(R.id.lyrics);
        Intent intent = getIntent();
        Music music = intent.getParcelableExtra("Music");
        musixMatch = new MusixMatch(MUSIC_MATCH_KEY);
        start(music.getTitle(), music.getArtist(), music.getBackground_color(), music.getLyric_color());

    }


    public void start(String song_Title, String artist, int backgroundColor, int lyricColor) {
        myTaskParams params = new myTaskParams(song_Title, artist, backgroundColor, lyricColor);
        new Lyr().execute(params);
    }

    private class myTaskParams{
        String artist;
        String songTitle;
        int backgroundColor;
        int lyricColor;

        myTaskParams(String artist, String songTitle, int backgroundColor, int lyricColor) {
            this.artist = artist;
            this.songTitle = songTitle;
            this.backgroundColor = backgroundColor;
            this.lyricColor = lyricColor;
        }
    }

    private class Lyr extends AsyncTask<myTaskParams, Void, Void>{

        @Override
        protected Void doInBackground(myTaskParams... params) {
            String artist = params[0].artist;
            String song_Title = params[0].songTitle;
            int backgroundColor = params[0].backgroundColor;
            int lyricColor = params[0].lyricColor;
            int track_id;
            Track track = null;
            try {
                track = musixMatch.getMatchingTrack(song_Title, artist);
            } catch (MusixMatchException e) {
                e.printStackTrace();
            }
            TrackData trkData = track.getTrack();
            track_id = trkData.getTrackId();
            System.out.println("The track id is                 " + track_id);

            Lyrics lyrics = null;

            try {
                System.out.println("Error");
                lyrics = musixMatch.getLyrics(track_id);
                System.out.println("Error");
            } catch (MusixMatchException e) {

                e.printStackTrace();
            }

            System.out.println(lyrics.getLyricsId());
            System.out.println(lyrics.getLyricsBody());
            if (lyrics.getLyricsBody() == ""){
                runOnUiThread(() ->
                        lyricsText.setText("No Lyrics Found"));
                lyricsText.setTextColor(lyricColor);
                layout.setBackgroundColor(backgroundColor);
                getWindow().setNavigationBarColor(backgroundColor);
            }
            else{
                Lyrics finalLyrics = lyrics;
                runOnUiThread(() ->
                        lyricsText.setText(finalLyrics.getLyricsBody()));
                lyricsText.setTextColor(lyricColor);
                layout.setBackgroundColor(backgroundColor);
                getWindow().setNavigationBarColor(backgroundColor);
            }
            return null;
        }
     }
}
