package com.example.pipegame.model;

import com.example.pipegame.MainMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pipe {

    private final ImageView image;
    private final int x;
    private final int y;

    public Pipe(int type, int x, int y) {
        ImageView image1;
        this.x = x;
        this.y = y;
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}