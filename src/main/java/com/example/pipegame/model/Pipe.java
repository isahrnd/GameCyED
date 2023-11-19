package com.example.pipegame.model;

import com.example.pipegame.MainMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Pipe {

    private final ImageView image;
    private final int row;
    private final int col;
    private final PipeType type;

    public Pipe(int type, int row, int col) {
        PipeType type1;
        ImageView image1;
        this.row = row;
        this.col = col;
        image1 = null;
        type1 = null;
        if (type != -1){
            image1 = getImageView(type);
            type1 = getPipeType(type);
        }
        this.type = type1;
        image = image1;
    }

    private ImageView getImageView(int type){
        Image image = new Image("file:"+ MainMenu.getFile("images/pipe_"+type+".png").getPath());
        return new ImageView(image);
    }

    private PipeType getPipeType(int type){
        switch (type) {
            case 1 -> {return PipeType.VERTICAL;}
            case 2 -> {return PipeType.HORIZONTAL;}
            case 3 -> {return PipeType.ELBOW_UP_RIGHT;}
            case 4 -> {return PipeType.ELBOW_UP_LEFT;}
            case 5 -> {return PipeType.ELBOW_DOWN_RIGHT;}
            default -> {return PipeType.ELBOW_DOWN_LEFT;}
        }
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

    public PipeType getType() {
        return type;
    }
}