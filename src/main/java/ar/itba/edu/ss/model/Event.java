package ar.itba.edu.ss.model;

import java.util.Objects;
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
        if (type == CollisionType.RE_DRAW || type == CollisionType.PARTICLE) {
            throw new IllegalArgumentException("Invalid type argument");
        }
        this.time = time;
        this.a = a;
        this.b = a;
        this.aCollisions = a.getCollisionCount();
        this.bCollisions = b.getCollisionCount();
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

    /*
        Dos eventos son igulaes si son del mismo tipo, ocurrieron al mismo tiempo y [( a1 = a2 y b1 = b2 ) o (a1 = b2 y b1 = a2)]
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Double.compare(event.time, time) == 0
                && (
                (aCollisions == event.aCollisions && bCollisions == event.bCollisions && Objects.equals(a, event.a) && Objects.equals(b, event.b))
                ||
                (aCollisions == event.bCollisions && bCollisions == event.aCollisions && Objects.equals(a, event.b) && Objects.equals(b, event.a))
                ) &&
                type == event.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, a, b, aCollisions, bCollisions, type);
    }
}
