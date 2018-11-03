package e.chuy7.spotifytrial;

import android.os.Parcel;
import android.os.Parcelable;


public class Music implements Parcelable {
    // book basics
    private String song_Title;
    private String artist;
    private int background_color;
    private int lyric_color;

    // main constructor
    public Music(String song_Title, String artist, int background_color, int lyric_color) {
        this.song_Title = song_Title;
        this.artist = artist;
        this.background_color = background_color;
        this.lyric_color = lyric_color;
    }

    // getters
    public String getTitle() { return song_Title; }
    public String getArtist() { return artist; }
    public int getBackground_color() { return background_color; }
    public int getLyric_color() { return lyric_color; }

    // write object values to parcel for storage
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(song_Title);
        dest.writeString(artist);
        dest.writeInt(background_color);
        dest.writeInt(lyric_color);
    }

    public Music(Parcel parcel) {
        song_Title = parcel.readString();
        artist = parcel.readString();
        background_color = parcel.readInt();
        lyric_color = parcel.readInt();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {

        @Override
        public Music createFromParcel(Parcel parcel) {
            return new Music(parcel);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[0];
        }
    };

    public int describeContents() {
        return hashCode();
    }
}