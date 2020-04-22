package ar.itba.edu.ss.forces;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.model.HardParticle;

public class Gravity implements Force {

    private HardParticle planet1;
    private final double G = 6.603E-11;

    public Gravity (HardParticle particle) {
        this.planet1 = particle;
    }

    @Override
    public double getX(HardParticle planet2) {
        return 0;
    }

    @Override
    public double getY(HardParticle planet2) {
        return 0;
    }

    @Override
    public double getX(double[] r, double[] v) {
        return 0;
    }

    @Override
    public double getY(double[] r, double[] v) {
        return 0;
    }

    @Override
    public boolean isVelocityDependant() {
        return true;
    }
}
