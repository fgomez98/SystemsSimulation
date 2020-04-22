package ar.itba.edu.ss.forces;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.model.HardParticle;

public class SpringOscillatorForce implements Force {

    private double k; // constante del resorte
    private double gamma; // Kg/s

    public SpringOscillatorForce(double k, double gamma) {
        this.k = k;
        this.gamma = gamma;
    }

    @Override
    public double getX(HardParticle particle) {
        return -1 * (particle.getX() * k + particle.getXVelocity() * gamma);
    }

    @Override
    public double getY(HardParticle particle) {
        return 0.0;
    }

    @Override
    public double getX(double[] r, double[] v) {
        return -1 * ((r[0] * k) + (v[0] * gamma));
    }

    @Override
    public double getY(double[] r, double[] v) {
        return 0.0;
    }

    @Override
    public boolean isVelocityDependant() {
        return false;
    }
}
