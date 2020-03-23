package ar.itba.edu.ss.model;

import ar.itba.edu.ss.utils.MathUtils;

import java.util.LinkedList;
import java.util.List;

public class HardParticle extends MovingParticle {

    private double mass;
    private long collisionCount = 0;

    public static List<HardParticle> generate(List<HardParticle> initialParticles, int size, double lBound, double radius, double aBound, double vBound, double mass) {
        List<HardParticle> particles = new LinkedList<>(initialParticles);
        int i = initialParticles.size();
        while (i != size + initialParticles.size()) {
            HardParticle randomParticle =  (HardParticle) new Builder(i).withMass(mass).withRandomAngle(aBound).withRandomVelocity(vBound).withRandomCoordinates(lBound).withRadius(radius).build();
            if (randomParticle.inBound(lBound)) {
                boolean valid = particles.stream().noneMatch(particle -> particle.interacts(randomParticle));
                if (valid) {
                    particles.add(randomParticle);
                    i++;

                }
            }
        }
        return particles;
    }

    /*
        Paredes verticlaes
        Sean xp1 < xp2 las coordenadas de las paredes verticales.
    */
    public double collidesX(double wallX1, double wallX2) {
        if (getXVelocity() > 0) {
            return (wallX2 - getRadius() - getX()) / getXVelocity();
        } else if (getXVelocity() < 0) {
            return (wallX1 + getRadius() - getX()) / getXVelocity();
        }
        return -1;
    }

    /*
       Paredes Horizontales
       Sean yp1 < yp2 las coordenadas de las paredes horizontales.
   */
    public double collidesY(double wallY1, double wallY2) {
        if (getYVelocity() > 0) {
            return (wallY2 - getRadius() - getY()) / getYVelocity();
        } else if (getYVelocity() < 0) {
            return (wallY2 + getRadius() - getY()) / getYVelocity();
        }
        return -1;
    }

    private double calculateD(double[] deltaR, double[] deltaV, double[] radius) {
        return Math.pow(MathUtils.dot(deltaV, deltaR), 2) - MathUtils.dot(deltaV, deltaV) * (MathUtils.dot(deltaR, deltaR) - (radius[0] + radius[1]));
    }

    public double collides(HardParticle b) {
        double[] radius = {getRadius(), b.getRadius()};
        double[] deltaX = {getX(), b.getX()};
        double[] deltaY = {getY(), b.getY()};
        double[] deltaVX = {getXVelocity(), b.getXVelocity()};
        double[] deltaVY = {getYVelocity(), b.getYVelocity()};
        double[] deltaR = MathUtils.delta(deltaX, deltaY);
        double[] deltaV = MathUtils.delta(deltaVX, deltaVY);
        double d = calculateD(deltaR, deltaV, radius);
        double dotVR = MathUtils.dot(deltaV, deltaR);
        if (d < 0 || (dotVR >= 0)) {
            return -1;
        }
        return -1 * ((dotVR + Math.sqrt(d)) / MathUtils.dot(deltaV, deltaV));
    }

    /*
        Paredes verticales
        si choca con pared Vertical ➔ (-vx, vy)
    */
    public void bounceX() {
        setAngle(Math.atan2(getYVelocity(), (getXVelocity() * -1)));
        collisionCount++;
    }

    /*
        Paredes Horizontales
        si choca con pared Horizontal ➔ (vx, -vy)
    */
    public void bounceY() {
        setAngle(Math.atan2((getYVelocity() * -1), getXVelocity()));
        collisionCount++;
    }

    private double calculateJ(double[] mass, double[] deltaR, double[] deltaV, double[] radius) {
        return (2 * mass[0] * mass[1] * (MathUtils.dot(deltaV, deltaR))) / ((radius[0] + radius[1]) * (mass[0] + mass[1]));
    }

    public void bounce(HardParticle b) {
        double[] mass = {getMass(), b.getMass()};
        double[] radius = {getRadius(), b.getRadius()};
        double[] deltaX = {getX(), b.getX()};
        double[] deltaY = {getY(), b.getY()};
        double[] deltaVX = {getXVelocity(), b.getXVelocity()};
        double[] deltaVY = {getYVelocity(), b.getYVelocity()};
        double[] deltaR = MathUtils.delta(deltaX, deltaY);
        double[] deltaV = MathUtils.delta(deltaVX, deltaVY);

        double j = calculateJ(mass, deltaR, deltaV, radius);
        double jx = j * MathUtils.delta(deltaX) / (radius[0] + radius[1]);
        double jy = j * MathUtils.delta(deltaY) / (radius[0] + radius[1]);

        // vxid = vxia + Jx/mi
        // Modificamos la velocidad de esta particula
        double vxAux = getXVelocity() + jx / getMass();
        double vyAux = getYVelocity() + jy / getMass();
        setVelocity(Math.sqrt(Math.pow(vxAux, 2) + Math.pow(vyAux, 2)));
        setAngle(Math.atan2(vyAux, vxAux));

        // vyjd = vyja - Jy/mj
        // Modificamos la velocidad de la otra particula

        vxAux = b.getXVelocity() - jx / b.getMass();
        vyAux = b.getYVelocity() - jy / b.getMass();
        b.setVelocity(Math.sqrt(Math.pow(vxAux, 2) + Math.pow(vyAux, 2)));
        b.setAngle(Math.atan2(vyAux, vxAux));

        collisionCount++;
        b.collisionCount++;
    }

    public long getCollisionCount() {
        return collisionCount;
    }

    public double getMass() {
        return mass;
    }

    @Override
    public StringBuilder dinamicData() {
        return super.dinamicData().append(" ").append(this.getMass());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Long.toString(getId()));
        return sb.append(" ").append(dinamicData()).append("\n").toString();
    }

    public static class Builder extends MovingParticle.Builder {

        private double mass;

        public Builder() {
        }

        public Builder(long id) {
            super(id);
        }

        public Builder withMass(double mass) {
            this.mass = mass;
            return this;
        }

        public Builder withRandomMass(double mBound) {
            this.mass = this.getRandom(mBound);
            return this;
        }

        public double getMass() {
            return mass;
        }

        public HardParticle build() {
            return new HardParticle(this);
        }
    }

    protected HardParticle(Builder builder) {
        super(builder);
        this.mass = builder.mass;
    }
}
