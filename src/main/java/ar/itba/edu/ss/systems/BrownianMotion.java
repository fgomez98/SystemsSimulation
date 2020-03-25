package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.model.Event;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;

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
        this.queue = new PriorityQueue<>();
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
        this.particles = HardParticle.generate(initialParticles, N - 1, (L - 2 * R2) + R2, R1, 2 * Math.PI, 0.1, M1);
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

    public void simulate(double simulationTime) {
        /*
            TODO: Cell index method para ver con cuales de mis vecinos estoy a distancia 0??, es decir me la puse
            El sistema es confinad por lo que no hay condicines de controno
         */
//        CellIndexMethod cim = new CellIndexMethod(M, L, N, 0, false);
//        cim.populate(particles);
//        Map<Particle, Set<Particle>> nearby = cim.cellIndexMethod();

        int frame = 0;

        IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, particles, frame++, false);

        computeNextCollision();

        while (simulationTime > 0) {
            Event event = getNextEvent().orElse(Event.from(1));
            /* Tiempo del el primer choque o incrementamos en 1 sino hay para redibujar el sistema */
            double tc = event.getTime();
            /*  Se evolucionan todas las partículas según sus ecuaciones de movimiento hasta tc */
            particles.forEach(p -> p.move(tc));
            /* Adelantamos el tiempo */
            simulationTime--;
            /* Collisionamos */
            collide(event);
            /* Output */
            IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, particles, frame++, true);
            /* Calculamos porximas colisiones */
            computeNextCollision();
        }
    }

    private void collide(Event event) {
        switch (event.getType()) {
            case PARTICLE:
                event.getA().get().bounce(event.getB().get());
                break;
            case VERTICAL_WALL:
                event.getA().get().bounceX();
                break;
            case HORIZONTAL_WALL:
                event.getA().get().bounceY();
                break;
            case RE_DRAW:
            default:
                break;
        }
    }

    private void computeNextCollision() {
        this.queue = new PriorityQueue<>(); // todo: por que no directamente calculo el proxmio para cada iteracion, si total hay que computar siempre porximas collisiones
        double collisionTime = 0;
        for (int i = 0; i < particles.size(); i++) {
            HardParticle aParticle = particles.get(i);
            collisionTime = aParticle.collidesX(0, L);
            if (collisionTime > 0) {
                addEvent(Event.from(collisionTime, aParticle, Event.CollisionType.VERTICAL_WALL));
            }
            collisionTime = aParticle.collidesY(0, L);
            if (collisionTime > 0) {
                addEvent(Event.from(collisionTime, aParticle, Event.CollisionType.HORIZONTAL_WALL));
            }
            for (int j = i + 1; j < particles.size(); j++) {
                HardParticle otherParticle = particles.get(j);
                collisionTime = aParticle.collides(otherParticle);
                if (collisionTime > 0) {
                    addEvent(Event.from(collisionTime, aParticle, otherParticle));
                }
            }
        }
    }


    public static void main(String args[]) {
        BrownianMotion bm = new BrownianMotion(100);
        bm.simulate(5000);

//        System.out.println("Starting simulation...");
//        long start = System.currentTimeMillis();
//
//
//        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
