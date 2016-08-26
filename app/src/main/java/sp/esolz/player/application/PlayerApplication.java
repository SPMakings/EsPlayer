package sp.esolz.player.application;

import android.app.Application;

import java.util.LinkedList;

import sp.esolz.player.Models.Albums;
import sp.esolz.player.Models.Song;

/**
 * Created by Saikat's Mac on 19/08/16.
 */

public class PlayerApplication extends Application {


    private LinkedList<Song> allSongs = null;
    private LinkedList<Albums> allAlbums = null;
    private static PlayerApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        if (allSongs == null) {
            allSongs = new LinkedList<Song>();
        }

        if (allAlbums == null) {
            allAlbums = new LinkedList<Albums>();
        }

    }

    public static synchronized PlayerApplication getInstance() {
        return mInstance;
    }


    public LinkedList<Song> getAllSongs() {
        return allSongs;
    }

    public LinkedList<Albums> getAllAlbums() {
        return allAlbums;
    }
}
