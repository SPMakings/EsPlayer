package sp.esolz.player.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import sp.esolz.player.Models.Albums;
import sp.esolz.player.R;

/**
 * Created by Saikat's Mac on 19/08/16.
 */
public class AlbumsAllAdapters extends RecyclerView.Adapter<AlbumsAllAdapters.ViewHolder> {


    private Context context = null;
    private LinkedList<Albums> maindata = null;
    private LayoutInflater infleter = null;
    private onItemSelectedListener callback;
    private int rowHight = 0;

    public AlbumsAllAdapters(final Context context, final LinkedList<Albums> maindata, final float screenWidth, final onItemSelectedListener callback) {
        this.context = context;
        this.maindata = maindata;
        infleter = LayoutInflater.from(context);
        rowHight = rowHight(screenWidth, 3.0f);
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = infleter.inflate(R.layout.items_album_all, parent, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHight));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            holder.albumName.setText(maindata.get(position).getAlbumTitle());
            Log.i("TAGADAP", "" + maindata.get(position).getAlbumArt());


            if (maindata.get(position).getAlbumArt() == null) {
                Glide.with(context).load("http://www.spyderonlines.com/images/wallpapers/music-pictures/music-pictures-21.jpg").centerCrop().into(holder.albumImage);
            } else {
                Glide.with(context).load(maindata.get(position).getAlbumArt()).centerCrop().into(holder.albumImage);
//                Glide.with(context).load("content://media/external/audio/media/" + maindata.get(position).getAlbumID() + "/albumart").centerCrop().into(holder.albumImage);
            }

            holder.albumImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onItemSelected(maindata.get(position));
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return maindata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private TextView albumName = null;
        private ImageView albumImage = null;

        public ViewHolder(View itemView) {
            super(itemView);
            albumName = (TextView) itemView.findViewById(R.id.textView2);
            albumImage = (ImageView) itemView.findViewById(R.id.album_image);
        }


    }


    public void setonItemSelectedListener(onItemSelectedListener callback) {
        this.callback = callback;
    }

    public interface onItemSelectedListener {
        void onItemSelected(final Albums data);
    }

    public int rowHight(final float screenWidth, final float noOfRows) {
        int hight = Math.round(screenWidth / noOfRows);
        hight = hight + Math.round(hight / 3.0f);
        return hight;
    }
}

