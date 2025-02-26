package ar.itba.edu.ss.model;

import ar.itba.edu.ss.utils.Rand;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Particle {

    private long id;
    private double x;
    private double y;
    private double radius;

    public Particle(long id, double x, double y, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Particle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public Particle(double x, double y) {
        this.x = x;
        this.y = y;
        this.radius = 0;
    }

    public Particle(double radius) {
        this.x = 0;
        this.y = 0;
        this.radius = radius;
    }

    public long getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    /*
        Mutable solo si necesito para optimizar
     */

    public void setX(double x) {
        this.x = x;
    }

    /*
        Mutable solo si necesito  para optimizar
    */
    public void setY(double y) {
        this.y = y;
    }

    public double distanceTo(Particle other) {
        double ctr_dist = Point.distance(this.getX(), this.getY(), other.getX(), other.getY());
        return ctr_dist == 0 ? 0 : ctr_dist - this.getRadius() - other.getRadius();
    }

    /*
    https://en.wikipedia.org/wiki/Periodic_boundary_conditions
    */

    public double periodicDistanceTo(Particle other, double l) {
        double dx = Math.abs(this.x - other.x);
        double dy = Math.abs(this.y - other.y);
        dx = (dx > l / 2) ? l - dx : dx;
        dy = (dy > l / 2) ? l - dy : dy;
        double ctr_dist = Math.sqrt(dx * dx + dy * dy);
        return ctr_dist == 0 ? 0 : ctr_dist - this.getRadius() - other.getRadius();
    }

    public boolean interacts(Particle other) {
        return Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2) < Math.pow(other.radius + this.radius, 2);
    }

    public boolean inBound(double lBound) {
        return x + radius < lBound && x - radius > 0 && y + radius < lBound && y - radius > 0;
    }

    public static Particle create(double lBound, double rBound) {
        return new Particle(Rand.getInstance().nextDouble() * lBound, Rand.getInstance().nextDouble() * lBound, Rand.getInstance().nextDouble() * rBound);
    }

    public static List<Particle> generate(int size, double lBound, double rBound) {
        List<Particle> particles = new LinkedList<>();
        int i = 0, attempts = 0;
        while (i != size) {
            Particle randomParticle = Particle.create(lBound, rBound);
            randomParticle.id = i;
            if (randomParticle.inBound(lBound)) {
                boolean valid = particles.stream().noneMatch(particle -> particle.interacts(randomParticle));
                if (valid) {
                    particles.add(randomParticle);
                    i++;
                    attempts = 0;
                }
                attempts++;
            }
        }
        return particles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Particle particle = (Particle) o;

        if (id != particle.id) return false;
        if (Double.compare(particle.x, x) != 0) return false;
        if (Double.compare(particle.y, y) != 0) return false;
        return Double.compare(particle.radius, radius) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(radius);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static Particle from(long id, String[] staticData, String[] dinamicData) {
        return new Particle(id, Double.parseDouble(dinamicData[0]), Double.parseDouble(dinamicData[1]), Double.parseDouble(staticData[0]));
    }

    /*
        Devolvemos  stringBuilder por si se quiere agregar mas data estatica asi optimizar
    */
    public StringBuilder staticData() {
        return (new StringBuilder()).append(radius);
    }

    /*
        Devolvemos  stringBuilder por si se quiere agregar mas data dinamica asi optimizar
     */
    public StringBuilder dinamicData() {
        return (new StringBuilder()).append(x).append(" ").append(y);
    }

    @Override
    public String toString() {
        return staticData().append(" ").append(dinamicData()).append("\n").toString();
    }
}
