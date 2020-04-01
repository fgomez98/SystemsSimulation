package ar.itba.edu.ss.utils;

import ar.itba.edu.ss.model.Particle;

@FunctionalInterface
public interface ReadParticle<T extends Particle > {

    T read(long id, String[] staticNextLine, String[] dinamicNextLine);

}
