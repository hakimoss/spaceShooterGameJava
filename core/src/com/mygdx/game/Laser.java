package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Laser {

    //laser phisical characteristics
    float movementSpeed;    //world unit per seconds

    //position and dimentions
    Rectangle boundingBox;

    //graphics
    TextureRegion textureRegion;

    public Laser(float movementSpeed, float xPosition, float yPosition, float width, float height, TextureRegion textureRegion) {
        this.movementSpeed = movementSpeed;

        this.boundingBox = new Rectangle(xPosition,yPosition,width,height);

        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        batch.draw(textureRegion,boundingBox.x - boundingBox.width/2,boundingBox.y,boundingBox.width,boundingBox.height);
    }

//    public Rectangle getBoundingBox() {
//        return boundingBox;
//    }
}
