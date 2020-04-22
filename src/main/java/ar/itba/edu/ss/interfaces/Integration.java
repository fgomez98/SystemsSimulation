package ar.itba.edu.ss.interfaces;

import ar.itba.edu.ss.model.HardParticle;

public interface Integration {

    void calculate(HardParticle particle, double dt);
}
