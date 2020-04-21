package ar.itba.edu.ss.integrators;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;

public class Euler implements Integration {

    private Force force;

    public Euler(Force force) {
        this.force = force;
    }

    @Override
    public HardParticle calculate(HardParticle particle, double dt) {
        double rx = particle.getX();
        double ry = particle.getY();
        double vx = particle.getXVelocity();
        double vy = particle.getYVelocity();
        double fx = force.getX(new double[]{rx, ry}, new double[]{vx, vy});
        double fy = force.getY(new double[]{rx, ry}, new double[]{vx, vy});

        vx += (dt / particle.getMass()) * (fx);
        vy += (dt / particle.getMass()) * (fy);

        rx += (dt * vx) + ((dt * dt * fx) / (2 * particle.getMass()));
        ry += (dt * vy) + ((dt * dt * fy) / (2 * particle.getMass()));

        particle.setCoordinates(rx, ry);
        particle.setVelocity(vx, vy);

        return (HardParticle) new HardParticle.Builder().withMass(particle.getMass()).withVelocity(vx, vy).withCoordinates(rx, ry).build();
    }
}
