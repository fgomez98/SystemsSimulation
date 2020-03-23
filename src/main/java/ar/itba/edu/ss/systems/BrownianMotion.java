package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.model.Event;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;
import ar.itba.edu.ss.utils.Rand;

import java.io.IOException;
import java.util.*;

import static ar.itba.edu.ss.utils.IOUtils.*;

public class BrownianMotion {

    private Queue<Event> queue;
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
        this.queue = new PriorityQueue<>();
        this.particles = particles;
        this.N = particles.size();
        this.M = (int) Math.ceil(L / 2 * Math.max(R1, R2) + 0);    // M optimo, criterio: L/M > radio interaccion + 2 * radioMax
    }

    public BrownianMotion(int n) {
        this();
        this.N = n;
        this.M = (int) Math.ceil(L / 2 * Math.max(R1, R2) + 0); // M optimo, criterio: L/M > radio interaccion + 2 * radioMax
        HardParticle particle = (HardParticle) new HardParticle.Builder()
                .withMass(M2)
                .withRandomAngle(2 * Math.PI)
                .withVelocity(0)
                .withCoordinates(L / 2, L / 2)
                .withRadius(R2)
                .build();
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

    private Optional<Event> getNextEvent() {
        Event next = null;
        while (!queue.isEmpty() && !(next = queue.poll()).isValid()) ;
        return Optional.of(next);
    }

    private void addEvent(Event event) {
        queue.offer(event);
    }

    public static void main(String args[]) {
        BrownianMotion bm = new BrownianMotion(300);
        IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, bm.particles, 0, false);

//        System.out.println("Starting simulation...");
//        long start = System.currentTimeMillis();
//
//
//        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
