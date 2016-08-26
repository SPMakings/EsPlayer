package sp.esolz.player.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sp.esolz.player.Models.Albums;
import sp.esolz.player.R;
import sp.esolz.player.adapters.AlbumsAllAdapters;
import sp.esolz.player.application.PlayerApplication;
import sp.esolz.player.listener.onAlbumSelected;

/**
 * Created by Saikat's Mac on 19/08/16.
 */

public class AlbumWiseSongs extends Fragment {


    private RecyclerView albumListing = null;
    private onAlbumSelected callback = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albumwise, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        albumListing = (RecyclerView) view.findViewById(R.id.album_listing);
        albumListing.setHasFixedSize(true);
        albumListing.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        makingAlbumReady();

    }


    private void makingAlbumReady() {

        Cursor c = getActivity().getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART,
                        MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.NUMBER_OF_SONGS},
                null,
                null,
                null);


        if (PlayerApplication.getInstance().getAllAlbums().size() != c.getCount()) {
            PlayerApplication.getInstance().getAllAlbums().clear();
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                Albums albTemp_ = new Albums();
                albTemp_.setAlbumTitle(c.getString(c.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                albTemp_.setAlbumID(c.getString(c.getColumnIndex(MediaStore.Audio.Albums._ID)));
                albTemp_.setAlbumArt(c.getString(c.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                albTemp_.setNoOfSongs(c.getString(c.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                PlayerApplication.getInstance().getAllAlbums().add(albTemp_);
            }
        }

        albumListing.setAdapter(new AlbumsAllAdapters(getActivity(), PlayerApplication.getInstance().getAllAlbums(), getScreenWidth(), new AlbumsAllAdapters.onItemSelectedListener() {
            @Override
            public void onItemSelected(final Albums data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        callback.onAlbumSelect(data);
//                        Intent i = new Intent(getActivity(), AlbumDetails.class);
//                        i.putExtra("ALBUM_ID", "" + data.getAlbumID());
//                        i.putExtra("ALBUM_ART", "" + data.getAlbumArt());
//                        i.putExtra("ALBUM_TITLE", "" + data.getAlbumTitle());
//                        startActivity(i);
                    }
                });
            }
        }));

    }


    public float getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }


    public void registerListener(final onAlbumSelected callback) {
        this.callback = callback;
    }


}
