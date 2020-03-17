package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.model.Cell;
import ar.itba.edu.ss.model.Particle;

import java.util.*;

public class CellIndexMethod {

    private int M; // dim matriz
    private double L; // ancho del cell
    private int N; // numero de particulas
    private Cell[] matrix; // mapa con todas las celdas matrix[ i ][ j ] = array[ i * m + j ].
    private List<? extends Particle> particles;
    private double radio_interaccion; // radio de ....
    private boolean contorno;

    public CellIndexMethod(int m, double l, int n, double radio_interaccion, boolean contorno) {
        M = m;
        L = l;
        N = n;
        this.radio_interaccion = radio_interaccion;
        this.contorno = contorno;
        this.matrix = new Cell[M * M];
        for (int i = 0; i < M * M; i++) {
            this.matrix[i] = new Cell();
        }
    }

    public List<? extends Particle> getParticles() {
        return particles;
    }

    public void populate(List<? extends Particle> particles) {
        this.particles = particles;
        for (int i = 0; i < M * M; i++) {
            this.matrix[i].getParticles().clear();
        }
        int i, j;
        for (Particle particle : particles) {
            i = (int) Math.floor(particle.getX() * M / L);
            j = (int) Math.floor(particle.getY() * M / L);
            matrix[(i) * M + (j)].getParticles().add(particle);
        }
    }

    public Map<Particle, Set<Particle>> cellIndexMethod() {
        Map<Particle, Set<Particle>> nearbyParticles = new HashMap<>();
        particles.forEach(particle -> nearbyParticles.put(particle, new HashSet<>()));
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < M; j++) {
                for (Particle particle : matrix[(i) * M + (j)].getParticles()) {
                    doLMethod(nearbyParticles, particle, i, j);
                }
            }
        }
        return nearbyParticles;
    }

    private void doLMethod(Map<Particle, Set<Particle>> nearbyParticles, Particle particle, final int i, final int j) {
        if (contorno) {
            // Math.floorMod() ==  (a % b + b) % b
            Direcctions.L_DIRECTIONS.forEach(direcction -> matrix[(Math.floorMod(i + direcction.getX(), M)) * M + (Math.floorMod(j + direcction.getY(), M))].getParticles().forEach(otherParticle -> areNearBy(nearbyParticles, particle, otherParticle)));
        } else {
            Direcctions.L_DIRECTIONS.forEach(direcction -> {
                        if ((i + direcction.getX() >= 0) && (i + direcction.getX() < M) && (j + direcction.getY() >= 0) && (j + direcction.getY() < M)) {
                            matrix[(i + direcction.getX()) * M + (j + direcction.getY())].getParticles().forEach(otherParticle -> areNearBy(nearbyParticles, particle, otherParticle));
                        }
                    }
            );
        }
    }

    private void areNearBy(Map<Particle, Set<Particle>> nearbyParticles, Particle particle, Particle otherParticle) {
        double dist = contorno ? particle.periodicDistanceTo(otherParticle, L) : particle.distanceTo(otherParticle);
        if (dist <= radio_interaccion && dist != 0) {
            nearbyParticles.get(particle).add(otherParticle);
            nearbyParticles.get(otherParticle).add(particle);
        }
    }

    public Map<Particle, Set<Particle>> forceBruteMethod() {
        Map<Particle, Set<Particle>> nearbyParticles = new HashMap<>();
        particles.forEach(particle -> nearbyParticles.put(particle, new HashSet<>()));
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                areNearBy(nearbyParticles, particles.get(i), particles.get(j));
            }
        }
        return nearbyParticles;
    }
}
