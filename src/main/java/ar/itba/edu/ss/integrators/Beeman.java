package ar.itba.edu.ss.integrators;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;

public class Beeman implements Integration {

    private Force force;
    /*
        estimar las posiciones y velocidades anteriores lo cual puede hacerse con Euler evaluado en -Δt.
     */
    private Integration euler;

    public Beeman(Force force) {
        this.force = force;
        this.euler = new Euler(force);
    }

    @Override
    public HardParticle calculate(HardParticle particle, double dt) {
        double rx = particle.getX();
        double ry = particle.getY();
        double vx = particle.getXVelocity();
        double vy = particle.getYVelocity();

        if (force.isVelocitydependant()) {
            HardParticle prevParticleState = euler.calculate(particle, -dt);

            rx = rx + (dt * particle.getXVelocity()) + ((2.0 / 3.0) * (dt * dt) * (force.getX(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt * dt) * (force.getX(prevParticleState) / particle.getMass()));
            ry = ry + (dt * particle.getYVelocity()) + ((2.0 / 3.0) * (dt * dt) * (force.getY(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt * dt) * (force.getY(prevParticleState) / particle.getMass()));

            double vx_prediction = vx + ((3.0 / 2.0) * (dt) * (force.getX(particle) / particle.getMass())) - ((1.0 / 2.0) * (dt) * (force.getX(prevParticleState) / particle.getMass()));
            double vy_prediction = vy + ((3.0 / 2.0) * (dt) * (force.getY(particle) / particle.getMass())) - ((1.0 / 2.0) * (dt) * (force.getY(prevParticleState) / particle.getMass()));

            HardParticle nextParticleState = (HardParticle) new HardParticle.Builder().withMass(particle.getMass()).withVelocity(vx_prediction, vy_prediction).withCoordinates(rx, ry).build();

            double vx_corected = vx + ((1.0 / 3.0) * (dt) * (force.getX(nextParticleState) / particle.getMass())) + ((5.0 / 6.0) * (dt) * (force.getX(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt) * (force.getX(prevParticleState) / particle.getMass()));
            double vy_corected = vy + ((1.0 / 3.0) * (dt) * (force.getY(nextParticleState) / particle.getMass())) + ((5.0 / 6.0) * (dt) * (force.getY(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt) * (force.getY(prevParticleState) / particle.getMass()));
            vx = vx_corected;
            vy = vy_corected;
        } else {
            rx = rx + (dt * particle.getXVelocity()) + ((2.0 / 3.0) * (dt * dt) * (force.getX(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt * dt) * (force.getX(particle) / particle.getMass()));
            ry = ry + (dt * particle.getYVelocity()) + ((2.0 / 3.0) * (dt * dt) * (force.getY(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt * dt) * (force.getY(particle) / particle.getMass()));
            vx = vx + ((1.0 / 3.0) * (dt) * (force.getX(particle) / particle.getMass())) + ((5.0 / 6.0) * (dt) * (force.getX(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt) * (force.getX(particle) / particle.getMass()));
            vy = vy + ((1.0 / 3.0) * (dt) * (force.getY(particle) / particle.getMass())) + ((5.0 / 6.0) * (dt) * (force.getY(particle) / particle.getMass())) - ((1.0 / 6.0) * (dt) * (force.getY(particle) / particle.getMass()));
        }

        particle.setCoordinates(rx, ry);
        particle.setVelocity(vx, vy);
        return (HardParticle) new HardParticle.Builder().withMass(particle.getMass()).withVelocity(vx, vy).withCoordinates(rx, ry).build();
    }
}
