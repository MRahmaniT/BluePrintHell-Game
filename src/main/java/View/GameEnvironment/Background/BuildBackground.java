package View.GameEnvironment.Background;

import View.Render.GameShapes.System.GameShape;
import View.Render.GameShapes.System.RectangleShape;

import java.awt.*;
import java.util.List;

public class BuildBackground {

    public static void buildBackground(int screenSizeX,int screenSizeY, List<GameShape> shapes){
        //Add Parameters
        RectangleShape rectangleShape;

        //shape 1
        rectangleShape = new RectangleShape(0,-(screenSizeY-(0.15f*screenSizeY))/2,screenSizeX,0.15f*screenSizeY, Color.LIGHT_GRAY);
        shapes.add(rectangleShape);
        //shape 2
        rectangleShape = new RectangleShape(-0.95f*(screenSizeX-(0.1f*screenSizeX))/2,-(screenSizeY-(0.15f*screenSizeY))/2,0.1f*screenSizeX,0.1f*screenSizeY,Color.DARK_GRAY);
        shapes.add(rectangleShape);
        //shape 3
        rectangleShape = new RectangleShape(-(screenSizeX-(0.2f*screenSizeX))/2,(0.15f*screenSizeY)/2,0.2f*screenSizeX,screenSizeY-(0.15f*screenSizeY),Color.GRAY);
        shapes.add(rectangleShape);
        //shape 4
        rectangleShape = new RectangleShape(-(screenSizeX-(0.2f*screenSizeX))/2,-((screenSizeY-(0.05f*screenSizeY))/2-0.15f*screenSizeY),0.2f*screenSizeX,0.05f*screenSizeY,Color.DARK_GRAY);
        shapes.add(rectangleShape);

    }
}
