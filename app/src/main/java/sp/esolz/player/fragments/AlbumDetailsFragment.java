package sp.esolz.player.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sp.esolz.player.Models.Song;
import sp.esolz.player.R;
import sp.esolz.player.adapters.SonglistAdapters;
import sp.esolz.player.application.PlayerApplication;

/**
 * Created by Saikat's Mac on 24/08/16.
 */

public class AlbumDetailsFragment extends Fragment {


    private RecyclerView allSongs = null;
    private SonglistAdapters songAdpter = null;
    private String ALBUM_ID = "", ALBUM_NAME = "", ALBUM_IMAGE = "";


    public static AlbumDetailsFragment getInstance(final String ALBUM_ID, final String ALBUM_NAME, final String ALBUM_IMAGE) {
        AlbumDetailsFragment frag_ = new AlbumDetailsFragment();
        frag_.ALBUM_ID = ALBUM_ID;
        frag_.ALBUM_NAME = ALBUM_NAME;
        frag_.ALBUM_IMAGE = ALBUM_IMAGE;
        return frag_;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album_details, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allSongs = (RecyclerView) view.findViewById(R.id.song_list);
        allSongs.setHasFixedSize(false);
        allSongs.setLayoutManager(new LinearLayoutManager(getActivity()));

        getAllSongsOfaAlbum(ALBUM_ID);
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

        Cursor cursor = getActivity().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        PlayerApplication.getInstance().getAllSongs().clear();//=====adding a blang instance at the top as Header

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

        songAdpter = new SonglistAdapters(getActivity(), PlayerApplication.getInstance().getAllSongs(), ALBUM_IMAGE, ALBUM_NAME);
        allSongs.setAdapter(songAdpter);

    }

}
