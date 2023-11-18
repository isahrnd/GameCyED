package com.example.pipegame.model;

import com.example.pipegame.MainMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pipe {

    private final ImageView image;
    private final int row;
    private final int col;
    private final int type;

    public Pipe(int type, int row, int col) {
        ImageView image1;
        this.row = row;
        this.col = col;
        this.type = type;
        image1 = null;
        if (type != -1){
            image1 = getImageView(type);
        }
        image = image1;
    }

    private ImageView getImageView(int type){
        Image image = new Image("file:"+ MainMenu.getFile("images/pipe_"+type+".png").getPath());
        return new ImageView(image);
    }

    public ImageView getImage() {
        return image;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getType() {
        return type;
    }
}