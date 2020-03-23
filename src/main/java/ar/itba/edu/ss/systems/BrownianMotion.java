package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;
import ar.itba.edu.ss.utils.Rand;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static ar.itba.edu.ss.utils.IOUtils.*;

public class BrownianMotion {

    private List<HardParticle> particles;
    private int M; // dim matriz
    private final double L = 0.5; // ancho del cell en metros
    private int N; // numero de particulas
    private final double R1 = 0.005;
    private final double M1 = 0.1;
    private final double R2 = 0.05;
    private final double M2 = 100;

    private BrownianMotion() {

    }

    public BrownianMotion(List<HardParticle> particles) {
        this.particles = particles;
        this.N = particles.size();
        this.M = (int) Math.ceil(L / 2 * Math.max(R1, R2) + 0);    // M optimo, criterio: L/M > radio interaccion + 2 * radioMax
    }

    public BrownianMotion(int n) {
        this();
        this.N = n;
        this.M = (int) Math.ceil(L / 2 * Math.max(R1, R2) + 0); // M optimo, criterio: L/M > radio interaccion + 2 * radioMax
        HardParticle particle = new HardParticle(L / 2, L / 2, R2, Rand.getInstance().nextDouble() * 2 * Math.PI, 0, M2);
        List<HardParticle> initialParticles = new LinkedList<>();
        initialParticles.add(particle);
        this.particles = HardParticle.generate(initialParticles, N - 1, L, R1, 2 * Math.PI, 0.1, M1);
        try {
            IOUtils.CSVWriteParticles(this.particles, BROWNIAN_MOTION_STATIC_FILENAME, BROWNIAN_MOTION_DINAMIC_FILENAME, this.particles.size(), L);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        BrownianMotion bm = new BrownianMotion(300);
        IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, bm.particles, 0);

//        System.out.println("Starting simulation...");
//        long start = System.currentTimeMillis();
//
//
//        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
