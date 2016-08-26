package sp.esolz.player;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import sp.esolz.player.Models.Song;
import sp.esolz.player.adapters.SonglistAdapters;
import sp.esolz.player.application.PlayerApplication;
import sp.esolz.player.services.MediaPlayerService;

public class AlbumActivity_Scrolling extends AppCompatActivity {


    private RecyclerView allSongs = null;
    private SonglistAdapters songAdpter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_activity__scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allSongs = (RecyclerView) findViewById(R.id.song_list);
        allSongs.setHasFixedSize(true);
        allSongs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//=======setting value

        Glide.with(getApplicationContext()).load("" + getIntent().getStringExtra("ALBUM_ART")).centerCrop().into((ImageView) findViewById(R.id.album_image));


        //===========Showing all songs of a ALbum


        getAllSongsOfaAlbum(getIntent().getStringExtra("ALBUM_ID"));


//        if (songAdpter != null) {
//            songAdpter.setonItemSelectedListener(new SonglistAdapters.onItemSelectedListener() {
//                @Override
//                public void onItemSelected(final String path) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent i = new Intent(AlbumActivity_Scrolling.this, MediaPlayerService.class);
//                            i.setAction(MediaPlayerService.ACTION_PLAY);
//                            i.putExtra("DATA_SOURCE", path);
//                            startService(i);
//                        }
//                    });
//                }
//            });
//        }


    }


    public void getAllSongsOfaAlbum(final String ALBUM_ID) {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.ALBUM_ID + " = " + ALBUM_ID;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        PlayerApplication.getInstance().getAllSongs().clear();

        while (cursor.moveToNext()) {

            Song sngTemp_ = new Song();
            sngTemp_.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            sngTemp_.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            sngTemp_.setSongTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            sngTemp_.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
            sngTemp_.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            sngTemp_.setAlbumID(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            sngTemp_.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
            sngTemp_.setSongID(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

            PlayerApplication.getInstance().getAllSongs().add(sngTemp_);
            //Log.i("TAG", "" + sngTemp_.getPath());
        }

        cursor.close();

//        songAdpter = new SonglistAdapters(AlbumActivity_Scrolling.this, PlayerApplication.getInstance().getAllSongs());
//        allSongs.setAdapter(songAdpter);

    }
}
