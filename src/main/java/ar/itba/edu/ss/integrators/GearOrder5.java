package ar.itba.edu.ss.integrators;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;

public class GearOrder5 implements Integration {

    private Force force;
    private double[] r = new double[6];

    public GearOrder5(Force force) {
        this.force = force;
    }

    @Override
    public HardParticle calculate(HardParticle particle, double dt) {

        return null;
    }

    private double[] rxDerive(HardParticle particle) {
        double[] rx = new double[6];
        rx[0] = particle.getX();
        rx[1] = particle.getXVelocity();

    }

//    F = m r2 = -k (r-r0)
//    r2 = -k/m (r-r0)
//    r3 = -k/m r1 ;
//    r4 = -k/m r2 = (k/m) ^2 (r-r0);
//    r5 = -k/m r3 = (k/m) ^2 r1
}
