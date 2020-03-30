package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.MolecularDinamic;
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
    private static String DCM_PARTICLE_FILENAME = "dcm-particle.txt";

    private final double FPS = 10.0; // frames por segundo a usar para la animacion
    private final double SPF = 1.0/ FPS; // seconds per frame

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

    public void simulate(double simulationTime, MolecularDinamic.DCM dcm) throws IOException {
        HardParticle dcmParticle = null;
        switch (dcm) {
            case BIGG:
                dcmParticle = bigParticle;
                break;
            case SMALL:
                dcmParticle = particles.stream().filter(particle ->  !particle.equals(bigParticle)).findAny().get();
                break;
            default:
                // no hay calculo de dcm, dejamos en null dcmParticle
                break;
        }

        double dcmTime = 0; /* Distribuimos tiempos cada 1 segundo */
        int frame = 0;
        double time = 0;

        IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, particles, frame++, false);

        IOUtils.CSVWrite(DCM_PARTICLE_FILENAME,
                new LinkedList<>(),
                (dcmTime++) + ", " + dcmParticle.getX() + ", " + dcmParticle.getY() + "\n",
                null, // no hay problema ya que no hay datos a aplicar la funcion
                true);

        saveVelocities(initialIterationVelocity);

        computeCollisions(time);

        while (simulationTime > time) {
            Event event = getNextEvent().get();
            if (event.getType() == Event.CollisionType.HORIZONTAL_WALL || event.getType() == Event.CollisionType.VERTICAL_WALL) {
                if (event.getA().get().equals(bigParticle)) {
                    // se choco contra una pared
                    break;
                }
            }

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

            /* Output */
            int currentFrame = (int) Math.floor(time / SPF);
            while (frame < currentFrame) {
                ovitoOutputParticles(frame++, time - frame * SPF);
            }

            if (dcmParticle != null) {
                /* Output de Z(t) y t para la particula dcm elegida */
                if (time > dcmTime) {
                    // output en dcmTime
                    IOUtils.CSVWrite(DCM_PARTICLE_FILENAME,
                            new LinkedList<>(),
                            dcmTime + ", " + dcmParticle.getX() + ", " + dcmParticle.getY() + "\n",
                            null, // no hay problema ya que no hay datos a aplicar la funcion
                            true);
                    dcmTime = Math.ceil(time); // redondeamos para arriba hacia el proximo segundo de la simulacion
                }
            }

            /* Collisionamos */
            COLLISIONS++;
            collide(event, time);
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

    private void ovitoOutputParticles(int frame, double dt) {
        List<HardParticle> frameParticles = particles.stream().map(particle -> {
            HardParticle cpyParticle = HardParticle.from(particle);
            cpyParticle.setX(cpyParticle.getX() - cpyParticle.getXVelocity() * dt);
            cpyParticle.setY(cpyParticle.getY() - cpyParticle.getYVelocity() * dt);
            return cpyParticle;
        }).collect(Collectors.toList());
        IOUtils.ovitoOutputParticles(BROWNIAN_MOTION_SIMULATION_FILENAME, frameParticles, frame, true);
    }

    public static void main(String args[]) {
        BrownianMotion bm = new BrownianMotion(30);

        System.out.println("Starting simulation...");
        long start = System.currentTimeMillis();

        try {
            bm.simulate(100, MolecularDinamic.DCM.BIGG);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
