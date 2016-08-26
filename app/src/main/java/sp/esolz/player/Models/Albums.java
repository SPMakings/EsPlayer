package sp.esolz.player.Models;

/**
 * Created by Saikat's Mac on 19/08/16.
 */

public class Albums {


    private String albumTitle = "", albumID = "", albumArt = "", noOfSongs = "";


    public String getNoOfSongs() {
        return noOfSongs;
    }

    public void setNoOfSongs(String noOfSongs) {
        this.noOfSongs = noOfSongs;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }
}
