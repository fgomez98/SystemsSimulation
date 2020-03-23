package ar.itba.edu.ss.model;

import java.util.List;
import java.util.stream.Collectors;

public class MovingParticle extends Particle {

    private double angle;
    private double velocity;

    public static List<MovingParticle> generate(int size, double lBound, double rBound, double aBound, double velocity) {
        return Particle.generate(size, lBound, rBound).stream().map(particle -> new Builder(particle)
                .withRandomAngle(aBound)
                .withVelocity(velocity)
                .build())
                .collect(Collectors.toList());
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

        MovingParticle that = (MovingParticle) o;

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
    public StringBuilder dinamicData() {
        return super.dinamicData().append(" ").append(this.getXVelocity()).append(" ").append(this.getYVelocity());
    }

    @Override
    public String toString() {
        double normalizedAngle = angle > 0 ? angle : angle + Math.PI * 2;
        StringBuilder sb = new StringBuilder(Long.toString(getId()));
        return sb.append(" ").append(dinamicData()).append(" ").append(normalizedAngle).append("\n").toString();
    }

    public static class Builder extends Particle.Builder {

        private double angle;
        private double velocity;

        public Builder() {
            super();
        }

        public Builder(long id) {
            super(id);
        }

        public Builder(Particle particle) {
            this(particle.getId());
            withCoordinates(particle.getX(), particle.getY());
            withRadius(particle.getRadius());
        }

        public Builder withVelocity(double velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder withRandomVelocity(double vBound) {
            this.velocity = this.getRandom(vBound);
            return this;
        }

        public Builder withAngle(double angle) {
            this.angle = angle;
            return this;
        }

        public Builder withRandomAngle(double aBound) {
            this.angle = this.getRandom(aBound);
            return this;
        }

        public double getAngle() {
            return angle;
        }

        public double getVelocity() {
            return velocity;
        }

        public MovingParticle build() {
            return new MovingParticle(this);
        }
    }

    protected MovingParticle(Builder builder) {
        super(builder);
        this.angle = builder.angle;
        this.velocity = builder.velocity;
    }
}
