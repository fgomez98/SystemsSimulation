package ar.itba.edu.ss.model;

import java.util.ArrayList;
import java.util.List;

public class Cell {

    private List<Particle> particles;

    public Cell() {
        particles = new ArrayList<>();
    }

    public Cell(List<Particle> particles) {
        this.particles = particles;
    }

    public List<Particle> getParticles() {
        return particles;
    }
}
