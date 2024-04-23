package com.example.newstg.adap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newstg.consts.ColorCons;
import com.example.newstg.databinding.ColorLayBinding;
import com.example.newstg.obj.Color;
import com.example.newstg.utils.ColorPickerCallback;

import java.util.ArrayList;
import java.util.List;

public class ColorPickerAd extends RecyclerView.Adapter<ColorPickerAd.ColPickViewHolder> {
    List<Color> colors = new ArrayList<>();
    Context ctx;
    Button btn;
    PopupWindow window;
    ColorPickerCallback callback;

    public ColorPickerAd(ColorPickerCallback callback) {
        this.callback = callback; // Initialize with callback
    }

    @NonNull
    @Override
    public ColorPickerAd.ColPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ColorLayBinding bnd = ColorLayBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ColorPickerAd.ColPickViewHolder(bnd);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPickerAd.ColPickViewHolder h, int p) {
        TextView colorTV = h.bnd.color;
        Color color = colors.get(p);
        colorTV.setText(color.getName());
        colorTV.setTextColor(color.getColor());
        colorTV.setOnClickListener(v -> {
            callback.onColorChosen(color.getColor()); // Use callback here
            window.dismiss();
        });
    }

    @Override
    public int getItemCount() { return colors.size(); }
    public static class ColPickViewHolder extends RecyclerView.ViewHolder {
        private final ColorLayBinding bnd;
        public ColPickViewHolder(@NonNull ColorLayBinding bnd) {
            super(bnd.getRoot());
            this.bnd = bnd;
        }
    }
    public void getColors(Context ctx, PopupWindow window, Button btn) {
        this.ctx = ctx;
        this.btn = btn;
        this.window = window;
        colors = ColorCons.getAllColors(ctx);

    }
}
