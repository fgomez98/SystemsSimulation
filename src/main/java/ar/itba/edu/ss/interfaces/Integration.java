package ar.itba.edu.ss.interfaces;

import ar.itba.edu.ss.model.Particle;

public interface Integration {

    void calculate(Particle particle, double dt);
}
