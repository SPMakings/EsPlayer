package sp.esolz.player.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

import sp.esolz.player.Models.Song;
import sp.esolz.player.R;

/**
 * Created by Saikat's Mac on 10/08/16.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {


    private final int IS_IN_PAUSE = 2;
    private final int IS_IN_PLAY = 1;

    private int STATE = 0;
    private int CURRENT_INDEX = 0;

    public static final String ACTION_READY = "sp.esolz.player.MediaPlayerService.ready";
    public static final String ACTION_PLAY = "sp.esolz.player.MediaPlayerService.start";
    public static final String ACTION_PAUSE = "sp.esolz.player.MediaPlayerService.pause";
    public static final String ACTION_STOP = "sp.esolz.player.MediaPlayerService.stop";
    public static final String ACTION_PREV = "sp.esolz.player.MediaPlayerService.prev";
    public static final String ACTION_NEXT = "sp.esolz.player.MediaPlayerService.next";

    private LinkedList<Song> currentData = null;
    private MediaPlayer mPlayer = null;
    private MediaSession mSession;
    private MediaController mController = null;
    private String CURRENT_PATH = "";
    private Bitmap albumBmp = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {


//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            try {
//                                albumBmp = Glide.with(getApplicationContext())
//                                        .load("content://media/external/audio/media/" + currentData.get(CURRENT_INDEX).getSongID() + "/albumart")
//                                        .asBitmap()
//                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                        .into(100, 100)
//                                        .get();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            } catch (ExecutionException e) {
//                                e.printStackTrace();
//                            }
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                            if (albumBmp != null) {
//                                albumBmp.recycle();
//                            }
//                        }
//                    }
//                });


                if (mPlayer == null) {
                    initPlayer();
                }

                //========session management


                if (intent.getStringExtra("ALBUM_ID") != null) {
                    //------controlling from Activity
                    getAllSongsOfaAlbum(intent.getStringExtra("ALBUM_ID"));
                    STATE = 0;
                }


                if (STATE != IS_IN_PAUSE) {


                    CURRENT_INDEX = intent.getIntExtra("DATA_SOURCE", 0);
                    CURRENT_PATH = currentData.get(CURRENT_INDEX).getPath();

//                    if (albumBmp != null) {
//                        albumBmp.recycle();
//                    }

//                    try {
//                        albumBmp = Glide.with(getApplicationContext())
//                                .load(currentData.get(CURRENT_INDEX).getThumbPath())
//                                .asBitmap()
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .into(100, 100)
//                                .get();
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }

//
//                    (new BackTaskAgain(new onBitmapLoad() {
//                        @Override
//                        public void onLoadSuccess() {
//
//                        }
//
//                        @Override
//                        public void onLoadFailed() {
//
//                        }
//                    })).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


                    AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                                                        @Override
                                                        public void onAudioFocusChange(int focusChange) {

                                                        }
                                                    }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


                    mSession.setActive(true);
                    PlaybackState state = new PlaybackState.Builder()
                            .setActions(
                                    PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                            PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                            PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                            .build();
                    mSession.setPlaybackState(state);
                    mSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

                    Glide.with(this)
                            .load("content://media/external/audio/media/" + currentData.get(CURRENT_INDEX).getSongID() + "/albumart")
                            .asBitmap()
                            .into(new SimpleTarget<Bitmap>(500, 500) {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
//                                    metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
//                                    mSession.setMetadata(metadataBuilder.build());
                                    albumBmp = bitmap;
                                    Log.i("TAG", bitmap.toString());

                                    mSession.setMetadata(new MediaMetadata.Builder()
                                            .putString(MediaMetadata.METADATA_KEY_ARTIST, "" + currentData.get(CURRENT_INDEX).getArtist())
                                            .putString(MediaMetadata.METADATA_KEY_ALBUM, "" + currentData.get(CURRENT_INDEX).getAlbum())
                                            .putString(MediaMetadata.METADATA_KEY_TITLE, "" + currentData.get(CURRENT_INDEX).getDisplayName())
                                            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap)
                                            .build());


                                    mController = new MediaController(getApplicationContext(), mSession.getSessionToken());


                                    //=============listener


                                    mSession.setCallback(new MediaSession.Callback() {
                                        @Override
                                        public void onPause() {
                                            super.onPause();
                                            Log.i("TAG", "onPause");

                                            if (mPlayer != null && mPlayer.isPlaying()) {
                                                STATE = IS_IN_PAUSE;
                                                mPlayer.pause();
                                                //stopForeground(true);
                                                notificationManagement(createAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                                            }

                                        }

                                        @Override
                                        public void onSkipToNext() {
                                            super.onSkipToNext();
                                            Log.i("TAG", "onSkipToNext" + CURRENT_INDEX);

                                            STATE = 0;
                                            if (CURRENT_INDEX < (currentData.size() - 1)) {
                                                CURRENT_INDEX++;
                                            } else {
                                                CURRENT_INDEX = 0;
                                            }

                                            Log.i("TAG", "onSkipToNext" + CURRENT_INDEX);

                                            CURRENT_PATH = currentData.get(CURRENT_INDEX).getPath();
                                            mController.getTransportControls().play();

                                        }

                                        @Override
                                        public void onSkipToPrevious() {
                                            super.onSkipToPrevious();
                                            Log.i("TAG", "onSkipToPrevious");

                                            STATE = 0;
                                            if (CURRENT_INDEX <= 0) {
                                                CURRENT_INDEX = currentData.size() - 1;
                                            } else {
                                                CURRENT_INDEX--;
                                            }

                                            Log.i("TAG", "onSkipToNext" + CURRENT_INDEX);

                                            CURRENT_PATH = currentData.get(CURRENT_INDEX).getPath();
                                            mController.getTransportControls().play();

                                        }

                                        @Override
                                        public void onStop() {
                                            super.onStop();
                                            Log.i("TAG", "onStop");
                                            STATE = 0;
                                            mSession.release();
                                            mPlayer.stop();
                                            mPlayer.release();
                                            mPlayer = null;
                                            stopForeground(false);

                                        }

                                        @Override
                                        public void onSeekTo(long pos) {
                                            super.onSeekTo(pos);
                                            Log.i("TAG", "onSeekTo");


                                        }

                                        @Override
                                        public void onPlay() {
                                            super.onPlay();
                                            Log.i("TAG", "onPlay");

                                            if (STATE != IS_IN_PAUSE) {
                                                try {

                                                    Log.i("TAG", "Reset and Release....");
                                                    mPlayer.stop();
                                                    mPlayer.reset();

                                                    mPlayer.setDataSource(CURRENT_PATH);
                                                    mPlayer.prepareAsync();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                STATE = IS_IN_PLAY;
                                                mPlayer.start();
                                            }
                                            notificationManagement(createAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                                        }

                                        @Override
                                        public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                                            return super.onMediaButtonEvent(mediaButtonIntent);
                                        }

                                        @Override
                                        public void onFastForward() {
                                            super.onFastForward();
                                        }
                                    });
                                    //========================

                                    mController.getTransportControls().play();

                                }
                            });


//                    mSession.setMetadata(new MediaMetadata.Builder()
//                            .putString(MediaMetadata.METADATA_KEY_ARTIST, "" + currentData.get(CURRENT_INDEX).getArtist())
//                            .putString(MediaMetadata.METADATA_KEY_ALBUM, "" + currentData.get(CURRENT_INDEX).getAlbum())
//                            .putString(MediaMetadata.METADATA_KEY_TITLE, "" + currentData.get(CURRENT_INDEX).getDisplayName())
//                            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, albumBmp)
//                            .build());

                    // Indicate you're ready to receive media commands


                } else {
                    mController.getTransportControls().play();
                }


            } else if (intent.getAction().equals(ACTION_PAUSE)) {
                mController.getTransportControls().pause();
            } else if (intent.getAction().equals(ACTION_STOP)) {
                mController.getTransportControls().stop();
            } else if (intent.getAction().equals(ACTION_NEXT)) {
                mController.getTransportControls().skipToNext();
            } else if (intent.getAction().equals(ACTION_PREV)) {
                mController.getTransportControls().skipToPrevious();
            }
        }


        return START_STICKY;
    }


    //=========service listener management

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    @Override
//    public boolean onUnbind(Intent intent) {
//        mSession.release();
//        return super.onUnbind(intent);
//    }

    //=========player listener management

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCompletion(MediaPlayer mp) {

        STATE = 0;
        if (currentData.size() < CURRENT_INDEX) {
            CURRENT_INDEX++;
            CURRENT_PATH = currentData.get(CURRENT_INDEX).getPath();
            mController.getTransportControls().play();
        } else {
            CURRENT_INDEX = 0;
            mSession.release();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            stopForeground(false);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayer.start();
        STATE = IS_IN_PLAY;
    }

    //=======

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);

        //==========================
        mSession = new MediaSession(this, "SaikatTag");


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void notificationManagement(Notification.Action action) {


        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);

        // Create a new Notification
        final Notification noti = new Notification.Builder(this).setVisibility(Notification.VISIBILITY_PUBLIC)
                // Hide the timestamp
                .setShowWhen(false).setPriority(Notification.PRIORITY_HIGH)
                        // Set the Notification style

                        // Set the Notification color
                .setColor(0xFFDB4437)
                        // Set the large and small icons
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(albumBmp)
                        // Set Notification content information
                .setContentText("by " + currentData.get(CURRENT_INDEX).getArtist() + " from " + currentData.get(CURRENT_INDEX).getAlbum())
//                .setContentInfo(currentData.get(CURRENT_INDEX).getArtist())
                .setContentTitle(currentData.get(CURRENT_INDEX).getSongTitle())
//                .setOngoing(true)
                        // Add some playback controls
                .addAction(createAction(android.R.drawable.ic_media_previous, "Prev", ACTION_PREV))
                .addAction(action)
                .addAction(createAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT))
                .setDeleteIntent(pendingIntent)
                .setStyle(new Notification.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mSession.getSessionToken()).setShowActionsInCompactView(new int[]{0, 1, 2}))
                .build();

        if (STATE == IS_IN_PAUSE) {
            stopForeground(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, noti);
        } else {
            startForeground(1, noti);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private Notification.Action createAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        //Icon icn = Icon.createWithResource(getApplicationContext(), icon);
        return new Notification.Action.Builder(icon, title, pendingIntent).build();

    }


    //================


    public void getAllSongsOfaAlbum(final String ALBUM_ID) {


        currentData = new LinkedList<Song>();

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

        currentData.clear();//=====adding a blang instance at the top as Header

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

            currentData.add(sngTemp_);
            //Log.i("TAG", "" + sngTemp_.getPath());
        }

        cursor.close();

    }


    //=================


    public class BackTaskAgain extends AsyncTask<Void, Void, Void> {

        private onBitmapLoad callback = null;

        public BackTaskAgain(onBitmapLoad callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                albumBmp = Glide.with(getApplicationContext())
                        .load("content://media/external/audio/media/" + currentData.get(CURRENT_INDEX).getSongID() + "/albumart")
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(100, 100)
                        .get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callback.onLoadSuccess();
        }


    }


    private interface onBitmapLoad {
        void onLoadSuccess();

        void onLoadFailed();
    }


}
