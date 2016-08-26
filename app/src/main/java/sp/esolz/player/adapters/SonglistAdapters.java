package sp.esolz.player.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import sp.esolz.player.Models.Song;
import sp.esolz.player.R;

/**
 * Created by Saikat's Mac on 18/08/16.
 */
public class SonglistAdapters extends RecyclerView.Adapter<SonglistAdapters.ViewHolder> {


    private Context context = null;
    private String album_Image = "";
    private String album_Name = "";
    private LinkedList<Song> maindata = null;
    private LayoutInflater infleter = null;
    private onItemSelectedListener callback;

    public SonglistAdapters(final Context context, final LinkedList<Song> maindata, final String album_Image, final String album_Name) {
        this.context = context;
        this.maindata = maindata;
        this.maindata.add(0, null);
        this.album_Image = album_Image;
        this.album_Name = album_Name;
        infleter = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ViewHolder(infleter.inflate(R.layout.items_songs, parent, false), viewType);
        } else {
            return new ViewHolder(infleter.inflate(R.layout.items_song_new, parent, false), viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {

            if (itemType(position) == 1) {
                holder.songName.setText(album_Name);
                Glide.with(context).load(album_Image).centerCrop().into(holder.albumImage);

            } else {
                holder.songName.setText(maindata.get(position).getDisplayName());
                holder.songName.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onItemSelected(position - 1);
                        }
                    }
                });
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;//=======Header
        } else {
            return 0;
        }
    }


    private int itemType(int position) {
        if (position == 0) {
            return 1;//=======Header
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return maindata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private TextView songName = null;
        private ImageView albumImage = null;

        public ViewHolder(View itemView, final int viewType) {
            super(itemView);
            if (viewType == 0) {
                songName = (TextView) itemView.findViewById(R.id.song_name);
            } else {
                songName = (TextView) itemView.findViewById(R.id.albumName);
                albumImage = (ImageView) itemView.findViewById(R.id.alb_img);
            }
        }


    }


    public void setonItemSelectedListener(onItemSelectedListener callback) {
        this.callback = callback;
    }

    public interface onItemSelectedListener {
        void onItemSelected(final int path);
    }
}
