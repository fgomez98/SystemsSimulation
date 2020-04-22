package ar.itba.edu.ss.integrators;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;

public class GearOrder5 implements Integration {

    private Force force;
    private double[] rx = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] ry = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] rxPrediction = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] ryPrediction = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] rxCorected = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] ryCorected = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    private double[] alpha = {3.0 / 20.0, 251.0 / 360.0, 1.0, 11.0 / 18.0, 1.0 / 6.0, 1.0 / 60.0};
    private double[] factorials = {1.0, 1.0, 2.0, 6.0, 24.0, 120.0};

    public GearOrder5(Force force) {
        this.force = force;
    }

    @Override
    public HardParticle calculate(HardParticle particle, double dt) {
        if (force.isVelocitydependant()) {
            alpha[0] = (3.0 / 16.0);
        }

        rDerive(particle);
        rPredict(dt);

        //  ([ r2(t+Δt) - r2p(t+Δt) ] * dt^2 )/ 2!
        // 2!=2
        double dr2x = ((rx[2] - rxPrediction[2]) * (dt * dt)) / factorials[2];
        double dr2y = ((ry[2] - ryPrediction[2]) * (dt * dt)) / factorials[2];

        rCorrect(dr2x, dr2y, dt);

        particle.setCoordinates(rxCorected[0], ryCorected[0]);
        particle.setVelocity(rxCorected[1], ryCorected[1]);

        return (HardParticle) new HardParticle.Builder().withMass(particle.getMass()).withVelocity(rxCorected[1], ryCorected[1]).withCoordinates(rxCorected[0], rxCorected[0]).build();
    }

    private void rCorrect(double dr2x, double dr2y, double dt) {
        for (int i = 0; i < 6; i++) {
            rxCorected[i] = rxPrediction[i] + ((alpha[i] * dr2x) * (factorials[i] / Math.pow(dt, i)));
            ryCorected[i] = ryPrediction[i] + ((alpha[i] * dr2y) * (factorials[i] / Math.pow(dt, i)));
        }
    }

    private void rPredict(double dt) {
        rxPrediction[0] = rx[0] + rx[1] * dt + rx[2] * (Math.pow(dt, 2) / factorials[2]) + rx[3] * (Math.pow(dt, 3) / factorials[3]) + rx[4] * (Math.pow(dt, 4) / factorials[4]) + rx[5] * (Math.pow(dt, 5) / factorials[5]);
        rxPrediction[1] = rx[1] + rx[2] * dt + rx[3] * (Math.pow(dt, 2) / factorials[2]) + rx[4] * (Math.pow(dt, 3) / factorials[3]) + rx[5] * (Math.pow(dt, 4) / factorials[4]);
        rxPrediction[2] = rx[2] + rx[3] * dt + rx[4] * (Math.pow(dt, 2) / factorials[2]) + rx[5] * (Math.pow(dt, 3) / factorials[3]);
        rxPrediction[3] = rx[3] + rx[4] * dt + rx[5] * (Math.pow(dt, 2) / factorials[2]);
        rxPrediction[4] = rx[4] + rx[5] * dt;
        rxPrediction[5] = rx[5];

    }

    private void rDerive(HardParticle particle) {
        // f = ma
        // f = mr^2
        //fuerza / masa = aceleracion
        rx[0] = particle.getX();
        ry[0] = particle.getY();

        rx[1] = particle.getXVelocity();
        ry[1] = particle.getYVelocity();

        rx[2] = force.getX(new double[]{rx[0], ry[0]}, new double[]{rx[1], ry[1]}) / particle.getMass();
        ry[2] = force.getY(new double[]{rx[0], ry[0]}, new double[]{rx[1], ry[1]}) / particle.getMass();

        rx[3] = force.getX(new double[]{rx[1], ry[1]}, new double[]{rx[2], ry[2]}) / particle.getMass();
        ry[3] = force.getY(new double[]{rx[1], ry[1]}, new double[]{rx[2], ry[2]}) / particle.getMass();

        rx[4] = force.getX(new double[]{rx[2], ry[2]}, new double[]{rx[3], ry[3]}) / particle.getMass();
        ry[4] = force.getY(new double[]{rx[2], ry[2]}, new double[]{rx[3], ry[3]}) / particle.getMass();

        rx[5] = force.getX(new double[]{rx[3], ry[3]}, new double[]{rx[4], ry[4]}) / particle.getMass();
        ry[5] = force.getY(new double[]{rx[3], ry[3]}, new double[]{rx[4], ry[4]}) / particle.getMass();
    }
}
