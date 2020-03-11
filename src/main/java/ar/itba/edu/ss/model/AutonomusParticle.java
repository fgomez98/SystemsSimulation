package ar.itba.edu.ss.model;

import ar.itba.edu.ss.utils.Rand;

import java.util.List;
import java.util.stream.Collectors;

public class AutonomusParticle extends Particle {

    private double angle;
    private double velocity;

    public AutonomusParticle(Particle particle, double angle, double velocity) {
        super(particle.getId(), particle.getX(), particle.getY(), particle.getRadius());
        this.angle = angle;
        this.velocity = velocity;
    }

    public AutonomusParticle(long id, double x, double y, double radius, double angle, double velocity) {
        super(id, x, y, radius);
        this.angle = angle;
        this.velocity = velocity;
    }

    public AutonomusParticle(double x, double y, double radius, double angle, double velocity) {
        super(x, y, radius);
        this.angle = angle;
        this.velocity = velocity;
    }

    public AutonomusParticle(double x, double y, double angle, double velocity) {
        super(x, y);
        this.angle = angle;
        this.velocity = velocity;
    }

    public AutonomusParticle(double radius, double angle, double velocity) {
        super(radius);
        this.angle = angle;
        this.velocity = velocity;
    }

    public AutonomusParticle(double radius) {
        super(radius);
        this.angle = 0;
        this.velocity = 0;
    }

    /*
        La velocidad es fija
     */

    public static AutonomusParticle create(double lBound, double rBound, double aBound, double velocity) {
        return new AutonomusParticle(Particle.create(lBound, rBound), Rand.getInstance().nextDouble() * aBound, velocity);
    }

    /*
        La velocidad es fija para todas las particulas
     */

    public static List<AutonomusParticle> generate(int size, double lBound, double rBound, double aBound, double velocity) {
        return Particle.generate(size, lBound, rBound).stream().map(particle -> new AutonomusParticle(particle, Rand.getInstance().nextDouble() * aBound, velocity)).collect(Collectors.toList());
    }

    public double getVelocity() {
        return velocity;
    }

    public double getXVelocity() {
        return Math.cos(angle) * velocity;
    }

    public double getYVelocity() {
        return Math.sin(angle) * velocity;
    }

    public double getAngle() {
        return angle;
    }

    /*
       Mutable solo si necesito  para optimizar
   */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /*
       Mutable solo si necesito  para optimizar
   */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    /*
        tiempo en segundos
     */
    public void move(int time_interval) {
        this.setX(this.getX() + Math.cos(this.getAngle()) * this.getVelocity() * time_interval);
        this.setY(this.getY() + Math.sin(this.getAngle()) * this.getVelocity() * time_interval);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AutonomusParticle that = (AutonomusParticle) o;

        if (Double.compare(that.angle, angle) != 0) return false;
        return Double.compare(that.velocity, velocity) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(angle);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(velocity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public StringBuilder staticData() {
        return super.staticData();
    }

    @Override
    public StringBuilder dinamicData() {
        return super.dinamicData().append(" ").append(this.getXVelocity()).append(" ").append(this.getYVelocity()).append(" ").append(this.getAngle());
    }
}
