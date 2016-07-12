package com.mylovemhz.floordoorrecords.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylovemhz.floordoorrecords.R;
import com.mylovemhz.floordoorrecords.net.AlbumResponse;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.ViewHolder> {

    protected List<AlbumResponse> albums;
    protected Set<Integer> selections;
    private Callback callback;

    public DownloadsAdapter(List<AlbumResponse> albumResponses){
        this.albums = albumResponses;
        this.selections = new HashSet<>();
    }

    public DownloadsAdapter(List<AlbumResponse> albumResponses, Callback callback){
        this(albumResponses);
        setCallback(callback);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_download,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AlbumResponse albumResponse = albums.get(position);
        holder.artistText.setText(albumResponse.getArtist());
        holder.titleText.setText(albumResponse.getTitle());

        try{
            Picasso.with(holder.itemView.getContext())
                    .load(albumResponse.getImageUrl())
                    .into(holder.albumImage);
        }catch(IllegalArgumentException e){
            //no image
        }
        holder.checkBox.setChecked(selections.contains(albumResponse.getAlbumId()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    selections.add(albumResponse.getAlbumId());
                }else{
                    selections.remove(albumResponse.getAlbumId());
                }
                if(callback != null) callback.onSelectionChanged(selections);
            }
        });
    }

    public Set<Integer> getSelections() {
        return selections;
    }

    public void setSelections(Set<Integer> selections) {
        this.selections = selections;
        notifyDataSetChanged();
    }

    public void setCallback(DownloadsAdapter.Callback callback){
        this.callback = callback;
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public interface Callback{
        void onSelectionChanged(Set<Integer> selection);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public ImageView albumImage;
        public TextView artistText;
        public TextView titleText;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            albumImage = (ImageView) itemView.findViewById(R.id.albumImage);
            artistText = (TextView) itemView.findViewById(R.id.artistText);
            titleText = (TextView) itemView.findViewById(R.id.titleText);
        }
    }
}
