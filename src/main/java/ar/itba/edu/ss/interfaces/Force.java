package ar.itba.edu.ss.interfaces;

import ar.itba.edu.ss.model.HardParticle;

public interface Force {

    double getX(HardParticle particle);

    double getY(HardParticle particle);

    double getX(double[] r, double[] v);

    double getY(double[] r, double[] v);
}
