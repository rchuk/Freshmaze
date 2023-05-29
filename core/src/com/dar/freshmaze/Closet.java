package com.dar.freshmaze;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.dar.freshmaze.entities.EnemyOld;

import java.util.ArrayList;

public class Closet {
    private static World world = null;
    private static ArrayList<Actor> actors = new ArrayList<Actor>();
    private static ArrayList<Rectangle> rooms = new ArrayList<>();
    public static World getWorld() {
        if(world == null)
            world = new World(Vector2.Zero, true);
        return world;
    }
    public static void addRoom(Rectangle r) {
        rooms.add(r);
    }
    public static void addActor(Rectangle r) {
        actors.add(new EnemyOld(r));
    }
    public static ArrayList<Actor> getActors(){
        return actors;
    }
    public static ArrayList<Rectangle> getRooms() {
        return rooms;
    }
}