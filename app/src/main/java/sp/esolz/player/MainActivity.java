package sp.esolz.player;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import sp.esolz.player.Models.Albums;
import sp.esolz.player.fragments.AlbumDetailsFragment;
import sp.esolz.player.fragments.AlbumWiseSongs;
import sp.esolz.player.listener.onAlbumSelected;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;
    private AlbumWiseSongs albumsFragment = null;

    //============fragment management

    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        //===============Bottom sheet management.

        mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        mBottomSheetBehavior.setPeekHeight(240);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //=====================================Fragment Management
        albumsFragment = new AlbumWiseSongs();
        albumsFragment.registerListener(new onAlbumSelected() {
            @Override
            public void onAlbumSelect(Albums data) {

                fireAlbumDetails(data.getAlbumID(), data.getAlbumTitle(), data.getAlbumArt());

            }
        });

        fireAlbumWise();
    }


    //========fetching all songs

    public void fireAlbumWise() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_bucket, albumsFragment);
        fragmentTransaction.commit();
    }


    public void fireAlbumDetails(final String ALBUM_ID, final String ALBUM_NAME, final String ALBUM_IMAGE) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_bucket, AlbumDetailsFragment.getInstance(ALBUM_ID, ALBUM_NAME, ALBUM_IMAGE));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
