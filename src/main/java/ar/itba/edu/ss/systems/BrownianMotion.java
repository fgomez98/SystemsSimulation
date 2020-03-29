package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.model.Event;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static ar.itba.edu.ss.utils.IOUtils.*;

public class BrownianMotion {

    private static long COLLISIONS = 0;
    private static String PDF_COLLISIONS_FILENAME = "pdf-colisiones.txt";
    private static String PDF_VELOCITY_THIRD_FILENAME = "pdf-velocidad-third.txt"; /* TODO: que onda lo del ultimo tercio ??*/
    private static String PDF_VELOCITY_INITIAL_FILENAME = "pdf-velocidad-initial.txt"; /* TODO: que onda lo del ultimo tercio ??*/
    private static String BIG_PARTICLE_TRAJECTORY_FILENAME = "big-particle-trajectory.txt";

    private Queue<Event> queue;
    private List<HardParticle> particles;
    private int M; // dim matriz
    private final double L = 0.5; // ancho del cell en metros
    private int N; // numero de particulas
    private final double R1 = 0.005;
    private final double M1 = 0.1;
    private final double R2 = 0.05;
    private final double M2 = 100;
    private List<Double> collitionTimes;
    /*
     particula -> suma de modulo de velocidades para el ultimo tercio de la simulacion
    Con esto luego calculamos el promedio
    */
    private Map<Long, Double> thirdIterationVelocity;
    private Map<Long, Double> initialIterationVelocity;
    private long thirdIterations = 0;
    private List<Pair<Double, Double>> bigParticleTrayectory;
    private HardParticle bigParticle;


    public BrownianMotion(List<HardParticle> particles) {
        this.bigParticle = particles.stream().max(Comparator.comparingDouble(HardParticle::getMass).thenComparingDouble(HardParticle::getRadius)).get();
        this.queue = new PriorityQueue<>();
        this.particles = particles;
        this.N = particles.size();
        this.M = (int) Math.ceil(L / 2 * Math.max(R1, R2) + 0);    // M optimo, criterio: L/M > radio interaccion + 2 * radioMax
        this.collitionTimes = new LinkedList<>();
        this.thirdIterationVelocity = new HashMap<>();
        this.initialIterationVelocity = new HashMap<>();
        this.bigParticleTrayectory = new LinkedList<>();
    }

    public BrownianMotion(int n) {
        this.collitionTimes = new LinkedList<>();
        this.thirdIterationVelocity = new HashMap<>();
        this.initialIterationVelocity = new HashMap<>();
        this.bigParticleTrayectory = new LinkedList<>();
        this.queue = new PriorityQueue<>();
        this.N = n;
        this.M = (int) Math.ceil(L / 2 * Math.max(R1, R2) + 0); // M optimo, criterio: L/M > radio interaccion + 2 * radioMax
        this.bigParticle = (HardParticle) new HardParticle.Builder()
                .withMass(M2)
                .withRandomAngle(2 * Math.PI)
                .withVelocity(0)
                .withCoordinates(L / 2, L / 2)
                .withRadius(R2)
                .build();
        List<HardParticle> initialParticles = new LinkedList<>();
        initialParticles.add(this.bigParticle);
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

    public void simulate(double simulationTime) throws IOException {
        /*
            TODO: Cell index method para ver con cuales de mis vecinos estoy a distancia 0??, es decir me la puse
            El sistema es confinad por lo que no hay condicines de controno
         */
//        CellIndexMethod cim = new CellIndexMethod(M, L, N, 0, false);
//        cim.populate(particles);
//        Map<Particle, Set<Particle>> nearby = cim.cellIndexMethod();

        int frame = 0;
        double time = 0;
        IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, particles, frame++, false);
        saveVelocities(initialIterationVelocity);

        computeCollisions(time);

        while (simulationTime > time) {
            Event event = getNextEvent().get();
            /* Tiempo del el primer choque o incrementamos en 1 sino hay para redibujar el sistema */
            double tc = event.getTime();
            double auxTime = tc - time; // --> tiempo entre colsiones
            collitionTimes.add(auxTime);
            /*  Se evolucionan todas las partículas según sus ecuaciones de movimiento hasta tc */
            particles.forEach(p -> p.move(auxTime));
            /* Adelantamos el tiempo */
            time = tc;

            if (time < ((1 / 3.0) * simulationTime)) { // último tercio de la simulación
                saveVelocities(thirdIterationVelocity);
                thirdIterations++;
            }
            saveBigParticlePosition(simulationTime, time);

            /* Collisionamos */
            COLLISIONS++;
            collide(event, time);
            /* Output */
            IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, particles, frame++, true);
        }
        outputCalculations(simulationTime);
    }

    private void collide(Event event, double currentTime) {
        switch (event.getType()) {
            case PARTICLE:
                event.getA().get().bounce(event.getB().get());
                nextCollision(event.getA().get(), currentTime);
                nextCollision(event.getB().get(), currentTime);
                break;
            case VERTICAL_WALL:
                event.getA().get().bounceX();
                nextCollision(event.getA().get(), currentTime);
                break;
            case HORIZONTAL_WALL:
                event.getA().get().bounceY();
                nextCollision(event.getA().get(), currentTime);
                break;
            case RE_DRAW:
                // creo que al pedo
            default:
                break;
        }
    }

    private void computeCollisions(double currentTime) {
        double collisionTime = 0;
        for (int i = 0; i < particles.size(); i++) {
            HardParticle aParticle = particles.get(i);
            collisionTime = aParticle.collidesX(0, L);
            if (collisionTime > 0) {
                addEvent(Event.from(collisionTime + currentTime, aParticle, Event.CollisionType.VERTICAL_WALL));
            }
            collisionTime = aParticle.collidesY(0, L);
            if (collisionTime > 0) {
                addEvent(Event.from(collisionTime + currentTime, aParticle, Event.CollisionType.HORIZONTAL_WALL));
            }
            for (int j = i + 1; j < particles.size(); j++) {
                HardParticle otherParticle = particles.get(j);
                collisionTime = aParticle.collides(otherParticle);
                if (collisionTime > 0) {
                    addEvent(Event.from(collisionTime + currentTime, aParticle, otherParticle));
                }
            }
        }
    }

    private void nextCollision(HardParticle aParticle, double currentTime) {
        double collisionTime = aParticle.collidesX(0, L);
        if (collisionTime > 0) {
            addEvent(Event.from(collisionTime + currentTime, aParticle, Event.CollisionType.VERTICAL_WALL));
        }
        collisionTime = aParticle.collidesY(0, L);
        if (collisionTime > 0) {
            addEvent(Event.from(collisionTime + currentTime, aParticle, Event.CollisionType.HORIZONTAL_WALL));
        }
        for (HardParticle otherParticle : particles) {
            if (!otherParticle.equals(aParticle)) {
                collisionTime = aParticle.collides(otherParticle);
                if (collisionTime > 0) {
                    addEvent(Event.from(collisionTime + currentTime, aParticle, otherParticle));
                }
            }
        }
    }

    private void outputCalculations(double simulationTime) throws IOException {
        IOUtils.CSVWrite(PDF_COLLISIONS_FILENAME,
                collitionTimes,
                collitionTimes.size() / simulationTime + "\n\n",
                time -> time + "\n",
                false);

        Double initialVelocitySum = initialIterationVelocity.entrySet().stream().reduce(0.0, (acc, entry) -> entry.getValue() + acc, Double::sum);

        IOUtils.CSVWrite(PDF_VELOCITY_INITIAL_FILENAME,
                initialIterationVelocity.values(),
                initialVelocitySum / (double) initialIterationVelocity.size() + "\n\n",
                velocity -> velocity + "\n",
                false);

        Double thirdIterationVelocitySum = thirdIterationVelocity.entrySet().stream().reduce(0.0, (acc, entry) -> (entry.getValue() / (double) thirdIterations) + acc, Double::sum);

        IOUtils.CSVWrite(PDF_VELOCITY_THIRD_FILENAME,
                thirdIterationVelocity.values(),
                thirdIterationVelocitySum / (double) thirdIterationVelocity.size() + "\n\n",
                velocity -> velocity / (double) thirdIterations + "\n",
                false);

        IOUtils.CSVWrite(BIG_PARTICLE_TRAJECTORY_FILENAME,
                bigParticleTrayectory,
                "",
                pair -> pair.getKey() + ", " + pair.getValue() + "\n",
                false);
    }

    private void saveBigParticlePosition(double simulationTime, double currentTime) {
        bigParticleTrayectory.add(new Pair<>(bigParticle.getX(), bigParticle.getY()));
    }

    private void saveVelocities(Map<Long, Double> velocityMap) {
        for (HardParticle particle : particles) {
            if (!velocityMap.containsKey(particle.getId())) {
                velocityMap.put(particle.getId(), particle.getVelocity());
            } else {
                Double partialVelocitySum = velocityMap.get(particle.getId());
                velocityMap.put(particle.getId(), partialVelocitySum + particle.getVelocity());
            }
        }
    }

    public static void main(String args[]) {
        BrownianMotion bm = new BrownianMotion(30);

        System.out.println("Starting simulation...");
        long start = System.currentTimeMillis();

        try {
            bm.simulate(100);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
