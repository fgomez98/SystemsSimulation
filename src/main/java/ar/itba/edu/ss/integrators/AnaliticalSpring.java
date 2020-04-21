package ar.itba.edu.ss.integrators;

import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;

public class AnaliticalSpring implements Integration {

    private double k;
    private double gamma;
    private double mass;
    private double amplitud;
    private double time;

    public AnaliticalSpring(double k, double gamma, double mass, double amplitud) {
        this.k = k;
        this.gamma = gamma;
        this.mass = mass;
        this.amplitud = amplitud;
        time = 0.0;
    }

    @Override
    public HardParticle calculate(HardParticle particle, double dt) {
        time += dt;
        double r = amplitud * Math.exp(-1 * ((gamma) / (2 * mass)) * time) * Math.cos(Math.sqrt((k / mass) - ((gamma * gamma) / (4 * mass * mass))) * time);
        particle.setX(r);
        return (HardParticle) new HardParticle.Builder().withCoordinates(r, 0).build();
    }
}
