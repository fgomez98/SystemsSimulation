package ar.itba.edu.ss.model;

import java.util.Optional;

public class Event implements Comparable<Event> {

    private double time;
    private HardParticle a; //  possibly null
    private HardParticle b; // possibly null
    private long aCollisions;
    private long bCollisions;
    private CollisionType type;

    private Event(double time, HardParticle a, HardParticle b) {
        this.time = time;
        this.a = a;
        this.b = b;
        this.aCollisions = a.getCollisionCount();
        this.bCollisions = b.getCollisionCount();
        this.type = CollisionType.PARTICLE;
    }

    private Event(double time, HardParticle a, CollisionType type) {
        this.time = time;
        this.a = a;
        this.aCollisions = a.getCollisionCount();
        this.type = type;
    }

    private Event(double time) {
        this.time = time;
        this.type = CollisionType.RE_DRAW;
    }

    public static Event from(double time, HardParticle a, HardParticle b){
        return new Event(time, a, b);
    }

    public static Event from(double time, HardParticle a, CollisionType type){
        return new Event(time, a, type);
    }

    public static Event from(double time){
        return new Event(time);
    }

    public double getTime() {
        return time;
    }

    public Optional<HardParticle> getA() {
        return Optional.of(a);
    }

    public  Optional<HardParticle> getB() {
        return Optional.of(b);
    }

    public CollisionType getType() {
        return type;
    }

    /*
            Comparador que define el orden natural de los eventos
         */
    @Override
    public int compareTo(Event other) {
        return Double.compare(this.getTime(), other.getTime());
    }

    public boolean isValid() {
        return aCollisions == a.getCollisionCount() && bCollisions == b.getCollisionCount();
    }

    public static enum CollisionType {
        VERTICAL_WALL, HORIZONTAL_WALL, PARTICLE, RE_DRAW;
    }
}
