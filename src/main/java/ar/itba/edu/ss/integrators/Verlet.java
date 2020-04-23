package ar.itba.edu.ss.integrators;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;

public class Verlet implements Integration {

    private Force force;
    private Integration euler;
//    ri(t+ t)=ri(t)+ dtvi(t)+ dt^2/mi fi(t)+O( t3),
    //v(t+ t)=v(t)+  dt/2mi (f (t)+f(t+ dt))+O( t2) == v(t) +  dt/2mi*f(t) +  dt/2mi*f(t + dt)


    public Verlet(Force force) {
        this.force = force;
        this.euler = new Euler(force);
    }

    @Override
    public void calculate(HardParticle particle, double dt) {
        double rx = particle.getX() + (dt * particle.getXVelocity()) + (((dt * dt) / particle.getMass()) * force.getX(particle));
        double ry = particle.getY() + (dt * particle.getYVelocity()) + (((dt * dt) / particle.getMass()) * force.getY(particle));

        HardParticle nextParticleState = particle.copy();
        euler.calculate(nextParticleState, dt);
        nextParticleState.setCoordinates(rx, ry);

        double vx = particle.getXVelocity() + ((dt / 2) * (force.getX(particle)/particle.getMass())) + ((dt / 2) * (force.getX(nextParticleState)/nextParticleState.getMass()));
        double vy = particle.getYVelocity() + ((dt / 2) * (force.getY(particle)/particle.getMass())) + ((dt / 2) * (force.getY(nextParticleState)/nextParticleState.getMass()));

//        // todo: usar predictor de euler
//        double vx_aux = particle.getXVelocity() + ((dt / (2 * particle.getMass())) * force.getX(particle));
//        double vy_aux = particle.getYVelocity() + ((dt / (2 * particle.getMass())) * force.getY(particle));
//
//        double vx = vx_aux + ((dt / (2 * particle.getMass())) * force.getX(new double[]{rx, ry}, new double[]{vx_aux, vy_aux}, particle.getMass()));
//        double vy = vy_aux + ((dt / (2 * particle.getMass())) * force.getY(new double[]{rx, ry}, new double[]{vx_aux, vy_aux}, particle.getMass()));

        particle.setCoordinates(rx, ry);
        particle.setVelocity(vx, vy);
    }
}
