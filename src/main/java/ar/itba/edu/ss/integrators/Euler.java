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
    public void calculate(HardParticle particle, double dt) {
        double rx = particle.getX();
        double ry = particle.getY();
        double vx = particle.getXVelocity();
        double vy = particle.getYVelocity();
        double fx = force.getX(particle);
        double fy = force.getY(particle);

        vx += ((dt * fx) / particle.getMass());
        vy += ((dt * fy) / particle.getMass());

        rx += (dt * vx) + ((dt * dt * fx) / (2 * particle.getMass()));
        ry += (dt * vy) + ((dt * dt * fy) / (2 * particle.getMass()));

        particle.setCoordinates(rx, ry);
        particle.setVelocity(vx, vy);
    }
}
