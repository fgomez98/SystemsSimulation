package ar.itba.edu.ss.model;

import ar.itba.edu.ss.utils.MathUtils;
import ar.itba.edu.ss.utils.Rand;

import java.util.List;

public class HardParticle extends MovingParticle {

    private double mass;
    private long collisionCount = 0;

    public HardParticle(long id, double x, double y, double radius, double angle, double velocity, double mass) {
        super(id, x, y, radius, angle, velocity);
        this.mass = mass;
    }

    public HardParticle(double x, double y, double radius, double angle, double velocity, double mass) {
        super(x, y, radius, angle, velocity);
        this.mass = mass;
    }

    public HardParticle(Particle particle, double angle, double velocity, double mass) {
        this(particle.getId(), particle.getX(), particle.getY(), particle.getRadius(), angle, velocity, mass);
        this.mass = mass;
    }

    public static HardParticle create(double lBound, double radius, double aBound, double vBound, double mass) {
        return new HardParticle(new Particle.Builder().withRandomCoordinates(lBound).withRadius(radius).build(), Rand.getInstance().nextDouble() * aBound, Rand.getInstance().nextDouble() * vBound, mass);
    }

    public static List<Particle> generate(List<Particle> initialParticles, int size, double lBound, double radius, double aBound, double vBound, double mass) {
        List<Particle> particles = initialParticles;
        int i = 0;
        while (i != size) {
            HardParticle randomParticle = HardParticle.create(lBound, radius, aBound, vBound, mass);
            randomParticle.setId(i);
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
}
