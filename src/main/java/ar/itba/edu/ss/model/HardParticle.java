package ar.itba.edu.ss.model;

import ar.itba.edu.ss.utils.MathUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class HardParticle extends MovingParticle {

    private double mass;
    private long collisionCount = 0;

    public static HardParticle from(HardParticle hardParticle) {
        return (HardParticle) new HardParticle.Builder(hardParticle.getId())
                .withMass(hardParticle.mass)
                .withAngle(hardParticle.getAngle())
                .withVelocity(hardParticle.getVelocity())
                .withRadius(hardParticle.getRadius())
                .withCoordinates(hardParticle.getX(), hardParticle.getY())
                .build();
    }

    public static List<HardParticle> generate(List<HardParticle> initialParticles, int size, double lBound, double radius, double aBound, double vBound, double mass) {
        List<HardParticle> particles = new LinkedList<>(initialParticles);
        int i = initialParticles.size();
        while (i != size + initialParticles.size()) {
            HardParticle randomParticle = (HardParticle) new Builder(i).withMass(mass).withRandomAngle(aBound).withRandomVelocity(vBound).withRandomCoordinates(lBound).withRadius(radius).build();
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
            return (wallY1 + getRadius() - getY()) / getYVelocity();
        }
        return -1;
    }

    private double calculateD(double[] deltaR, double[] deltaV, double sigma) {
        return Math.pow(MathUtils.dot(deltaV, deltaR), 2) - (MathUtils.dot(deltaV, deltaV) * (MathUtils.dot(deltaR, deltaR) - Math.pow(sigma, 2)));
    }

    public double collides(HardParticle b) {
        double sigma = getRadius() + b.getRadius();
        double[] deltaR = {b.getX() - this.getX(), b.getY() - this.getY(),};
        double[] deltaV = {b.getXVelocity() - this.getXVelocity(), b.getYVelocity() - this.getYVelocity()};
        double d = calculateD(deltaR, deltaV, sigma);
        double dotVR = MathUtils.dot(deltaV, deltaR);
        if (d < 0 || (dotVR >= 0)) {
            return -1;
        }
        return -((dotVR + Math.sqrt(d)) / MathUtils.dot(deltaV, deltaV));
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

    private double calculateJ(double[] mass, double[] deltaR, double[] deltaV, double sigma) {
        return (2 * mass[0] * mass[1] * MathUtils.dot(deltaV, deltaR)) / (sigma * (mass[0] + mass[1]));
    }

    public void bounce(HardParticle b) {
        double[] mass = {getMass(), b.getMass()};
        double deltaX = b.getX() - this.getX();
        double deltaY = b.getY() - this.getY();
        double sigma = getRadius() + b.getRadius();
        double[] deltaR = {deltaX, deltaY};
        double[] deltaV = {b.getXVelocity() - this.getXVelocity(), b.getYVelocity() - this.getYVelocity()};

        double j = calculateJ(mass, deltaR, deltaV, sigma);
        double jx = (j * deltaX) / sigma;
        double jy = (j * deltaY) / sigma;

        // vxid = vxia + Jx/mi
        // Modificamos la velocidad de esta particula
        double vxAux = getXVelocity() + (jx / getMass());
        double vyAux = getYVelocity() + (jy / getMass());
        setAngle(Math.atan2(vyAux, vxAux));
        setVelocity(Math.sqrt(Math.pow(vxAux, 2) + Math.pow(vyAux, 2)));

        // vyjd = vyja - Jy/mj
        // Modificamos la velocidad de la otra particula

        vxAux = b.getXVelocity() - (jx / b.getMass());
        vyAux = b.getYVelocity() - (jy / b.getMass());
        b.setAngle(Math.atan2(vyAux, vxAux));
        b.setVelocity(Math.sqrt(Math.pow(vxAux, 2) + Math.pow(vyAux, 2)));

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
    public StringBuilder staticData() {
        return super.staticData().append(" ").append(this.getMass());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Long.toString(getId()));
        return sb.append(" ").append(staticData()).append(" ").append(dinamicData()).append(" ").append(getVelocity()).append("\n").toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HardParticle that = (HardParticle) o;
        return Double.compare(that.mass, mass) == 0 &&
                collisionCount == that.collisionCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mass, collisionCount);
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
