package com.alexeyosadchy.giphy.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexeyosadchy.giphy.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;
import java.util.Random;

public class GifListAdapter extends RecyclerView.Adapter<GifListAdapter.GifHolder> {

    private List<GifView> mGifs;

    public GifListAdapter(List<GifView> gifs) {
        mGifs = gifs;
    }

    @Override
    public GifHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_gif, parent, false);
        return new GifHolder(view);
    }

    @Override
    public void onBindViewHolder(GifHolder holder, int position) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(mGifs.get(position).getUri())
                .setAutoPlayAnimations(true)
                .build();
        holder.mSimpleDraweeView.setController(controller);
        float ratio = (float) mGifs.get(position).getWidth() / mGifs.get(position).getHeight();
        holder.mSimpleDraweeView.setAspectRatio(ratio);
        float scaleFactor = (float) 1080f / mGifs.get(position).getWidth();
        holder.mCardView.getLayoutParams().height = (int) ((float) mGifs.get(position).getHeight() * scaleFactor);
        holder.mSimpleDraweeView.getHierarchy().setPlaceholderImage(getPlaceHolderColor());
    }

    @Override
    public int getItemCount() {
        return mGifs.size();
    }

    public class GifHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private SimpleDraweeView mSimpleDraweeView;

        public GifHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view_gif);
            mSimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.my_image_view);
        }
    }

    private int getPlaceHolderColor() {
        switch (new Random().nextInt(11)) {
            case 0:
                return R.color.place_holder_1;
            case 1:
                return R.color.place_holder_2;
            case 2:
                return R.color.place_holder_3;
            case 3:
                return R.color.place_holder_4;
            case 4:
                return R.color.place_holder_5;
            case 5:
                return R.color.place_holder_6;
            case 6:
                return R.color.place_holder_7;
            case 7:
                return R.color.place_holder_8;
            case 8:
                return R.color.place_holder_9;
            case 9:
                return R.color.place_holder_10;
            case 10:
                return R.color.place_holder_11;
            default:
                return R.color.place_holder_1;
        }
    }
}