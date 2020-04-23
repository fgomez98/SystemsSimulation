package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.forces.Gravity;
import ar.itba.edu.ss.integrators.Beeman;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * a) El momento en el futuro (fecha y cuantos dias desde 06/04/2020) en el cual la nave debería partir para asegurar el arribo a marte. Para ello graficar distancia mínima a marte en función de la fecha de salida.
 * b) Tiempo de viaje.
 * c) Velocidad de la nave al llegar. Queda en órbita, pasa de largo o impacta sobre la superficie marciana?
 * d ) Repetir el análisis anterior para una velocidad V0 = 13 km/s. Optativo: Elegir una tercer velocidad propuesta y comparar.
 */

public class MissionToMars {

    private double EPSILON = 0.00001;

    List<HardParticle> bodies;

    private HardParticle mars;
    private HardParticle earth;
    private HardParticle sun;
    private HardParticle spaceship;

    private Set<HardParticle> marsNeighbours;
    private Set<HardParticle> earthNeighbours;
    private Set<HardParticle> sunNeighbours;
    private Set<HardParticle> spaceshipNeighbours;

    private Integration marsIntegrator;
    private Integration earthIntegrator;
    private Integration sunIntegrator;
    private Integration spaceshipIntegrator;

    private double dt;
    private double dt2;
    private final int STATE_K = 4;
    private int days = 0;
    private double time = 0;
    private double initialSpeed1 = 8.0;      /* 8 km/hr */
    private double initialSpeed2 = 13.0;      /* 13 km/hr */
    private Calendar calendar;

    private static String FUTURE_ARRIVAL = "future-arrival.txt";
    private static String TIME_OF_TRIP = "time-of-trip.txt";
    private static String FINAL_SPEED = "final-speed.txt";

    public MissionToMars(double dt, int spaceshipSpeed) {
        this.dt = dt;
        this.dt2 = dt * STATE_K;

        calendar = new GregorianCalendar(2020, Calendar.APRIL, 4);

        initPlanets();
    }

    // a) El momento en el futuro (fecha y cuantos dias desde 06/04/2020) en el cual la nave debería partir para asegurar el arribo a marte. Para ello graficar distancia mínima a marte en función de la fecha de salida.

    private void simulateFutureArrival(double simulationTimeTotal) throws IOException {
        List<Pair<Double, Double>> distances = new ArrayList<>();
        double days = 140;
        while (days < 200) { // dias desde 06/04/2020 cuando se efectua el lanzamiento
            // posicionamos los planetas en sus posiciones iniciales
            initPlanets();
            boolean hasLaunched = false;
            double minDist = Double.MAX_VALUE;
            double launchTime = days * 86400; // -> a segundos
            double currentSimulationTime = 0;
            int frame = 0;
            IOUtils.ovitoOutputParticles("mars.xyz",
                    bodies,
                    frame++,
                    false);

            while (currentSimulationTime <= simulationTimeTotal) { // tiempo total a simular -> en segundos por que la velocidad esta en km/s
                if (!hasLaunched && (currentSimulationTime >= launchTime)) {
                    hasLaunched = true;
                    addSpaceShip();
                }
                marsIntegrator.calculate(mars, dt);
                earthIntegrator.calculate(earth, dt);
                sunIntegrator.calculate(sun, dt);

                marsNeighbours = new HashSet<>();
                marsNeighbours.add(earth.copy());
                marsNeighbours.add(sun.copy());

                earthNeighbours = new HashSet<>();
                earthNeighbours.add(sun.copy());
                earthNeighbours.add(mars.copy());

                sunNeighbours = new HashSet<>();
                sunNeighbours.add(earth.copy());
                sunNeighbours.add(mars.copy());

                if (hasLaunched) {
                    spaceshipIntegrator.calculate(spaceship, dt);
                    double dist = Point.distance(spaceship.getX(), spaceship.getY(), mars.getX(), mars.getY());
                    if (dist < minDist) {
                        minDist = dist;
                    }

                    spaceshipNeighbours = new HashSet<>();
                    spaceshipNeighbours.add(sun.copy());
                    spaceshipNeighbours.add(earth.copy());
                    spaceshipNeighbours.add(mars.copy());

                    sunNeighbours.add(spaceship.copy());
                    earthNeighbours.add(spaceship.copy());
                    marsNeighbours.add(spaceship.copy());

                    spaceshipIntegrator = new Beeman(new Gravity(spaceshipNeighbours));
                }

                marsIntegrator = new Beeman(new Gravity(marsNeighbours));
                earthIntegrator = new Beeman(new Gravity(earthNeighbours));
                sunIntegrator = new Beeman(new Gravity(sunNeighbours));

                IOUtils.ovitoOutputParticles("mars.xyz",
                        bodies,
                        frame++,
                        true);

                currentSimulationTime += dt; // paso temporal a considerar en segundos otra vez
            }
            distances.add(new Pair(days, minDist));
            days++;
        }
        IOUtils.CSVWrite("distances_mars.txt",
                distances,
                "Days,Distance\n",
                p -> p.getKey() + "," + p.getValue() + "\n",
                false);
    }

    private void locateSpaceship(HardParticle spaceship, double initialSpeed) {
        double sunToEarthDist = Point.distance(sun.getX(), sun.getY(), earth.getX(), earth.getY());
        double spaceshipToEarthDist = earth.getRadius() + spaceship.getRadius() + 1500;

        double ex = (earth.getX() - sun.getX()) / sunToEarthDist;       /* coseno del angulo */
        double ey = (earth.getY() - sun.getY()) / sunToEarthDist;       /* seno  del angulo */

        double xCoord = earth.getX() + spaceshipToEarthDist * ex;
        double yCoord = earth.getY() + spaceshipToEarthDist * ey;

        double speed = initialSpeed + 7.12;

        /* direccion de la nave --> etx = -ey ; ety = ex */
        double vx = speed * (-ey);
        double vy = speed * ex;

        vx += earth.getXVelocity();
        vy += earth.getYVelocity();

        spaceship.setVelocity(vx, vy);
        spaceship.setCoordinates(xCoord, yCoord);
    }

    private void addSpaceShip() {
        locateSpaceship(spaceship, 8.0);
        bodies.add(spaceship);
        // re calculamos los vecinos para las fuerzas
        marsNeighbours.add(spaceship.copy());
        earthNeighbours.add(spaceship.copy());
        sunNeighbours.add(spaceship.copy());
    }

    private boolean hasSpaceshipArrived() {
        double distance = spaceship.distanceTo(mars);
        return distance <= EPSILON;
    }

    private void outputPredictions() throws IOException {
        List<HardParticle> data = new LinkedList<>();
        data.add(spaceship);
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd-MMM-yyyy");

        IOUtils.CSVWrite(FUTURE_ARRIVAL,
                data,
                "",
                particle -> days + ", " + formattedDate.format(calendar.getTime()),
                false);
    }

    private void updateDays() {
        int simulationDays = (int) time / 3600;
        days += simulationDays - days;
    }

    private void restartCalendar() {
        calendar.set(2020, Calendar.APRIL, 4);
    }

//    Set<HardParticle> getNeighbours(HardParticle planet) {
//        Set<HardParticle> neigh = new HashSet<>();
//        for (Pair<HardParticle, Integration> p : bodies) {
//            if (!p.getKey().equals(planet)) {
//                neigh.add(p.getKey());
//            }
//        }
//        return neigh;
//    }

    private void initPlanets() {
        /* sacamos las condiciones iniciales del link  https://ssd.jpl.nasa.gov/horizons.cgi#top al día 06/04
        * UNIDADES
        *   Posición en km
            Velocidad en km/s
            Peso en kg
            Radio en km
        * */

        /* MARTE */
        /*
            radio 3389.92+-0.04 km
            Mass x10^23 (kg)      =    6.4171
            X =-1.697831879172063E-01 Y =-1.452533227422469E+00         AU
            VX= 1.442541741631660E-02 VY=-3.732586868630861E-04         AU/DAY
         */
        mars = (HardParticle) new HardParticle.Builder(1)
                .withMass(6.4171E23)
                .withVelocity(24.976987609255310474, -0.64628130526615434892)
                .withRadius(3389.92)
                .withCoordinates(-25399203.393072031438, -217295877.94340020418)
                .build();

        /* TIERRA */
        /*
            radio 6371.01+-0.02 km
            Mass x10^24 (kg)= 5.97219+-0.0006
            X =-9.646530350221529E-01 Y =-2.750306360859890E-01
            VX= 4.564771838790553E-03 VY=-1.656625701516261E-02
         */
        earth = (HardParticle) new HardParticle.Builder(2)
                .withMass(5.97219E24)
                .withVelocity(7.9037054087313700634, -28.683758969181297971)
                .withRadius(6371.01)
                .withCoordinates(-144310040.00360658765, -41143997.535730540752)
                .build();

        /* SOL */
        /*
            radio 695700 km
            Mass, 10^24 kg        = ~1988500
         */
        sun = (HardParticle) new HardParticle.Builder(3)
                .withMass(1988500E24)
                .withVelocity(0, 0)
                .withRadius(695700)
                .withCoordinates(0, 0).build();


        spaceship = (HardParticle) new HardParticle.Builder(4)
                .withMass(2E5)
                .withRadius(50)     /* a nuestro criterio */
                .build();


        /* Metodos de integracion, la nave no va posicionada aun */
        marsNeighbours = new HashSet<>();
        marsNeighbours.add(earth.copy());
        marsNeighbours.add(sun.copy());

        earthNeighbours = new HashSet<>();
        earthNeighbours.add(sun.copy());
        earthNeighbours.add(mars.copy());

        sunNeighbours = new HashSet<>();
        sunNeighbours.add(earth.copy());
        sunNeighbours.add(mars.copy());

        spaceshipNeighbours = new HashSet<>();
        spaceshipNeighbours.add(sun.copy());
        spaceshipNeighbours.add(earth.copy());
        spaceshipNeighbours.add(mars.copy());

        marsIntegrator = new Beeman(new Gravity(marsNeighbours));
        earthIntegrator = new Beeman(new Gravity(earthNeighbours));
        sunIntegrator = new Beeman(new Gravity(sunNeighbours));
        spaceshipIntegrator = new Beeman(new Gravity(spaceshipNeighbours));

        bodies = new ArrayList<>();
        bodies.add(mars);
        bodies.add(sun);
        bodies.add(earth);
    }

    private void outputCalculations() throws IOException {
        /* TODO: corregir esto */
        List<HardParticle> data = new LinkedList<>();
        data.add(spaceship);

        IOUtils.CSVWrite(TIME_OF_TRIP,
                data,
                "",
                particle -> days + "\n",
                false);

        IOUtils.CSVWrite(FINAL_SPEED,
                data,
                "",
                particle -> particle.getVelocity() + "\n",
                false);
    }

    public void simulate(double simulationTime) throws IOException {

        while (!hasSpaceshipArrived() || simulationTime > time) {

            time += dt;
            updateDays();
        }

        outputCalculations();

    }

    public static void main(String[] args) {

        MissionToMars mars = new MissionToMars(500, 8); // definir esto // -> a segundos

        System.out.println("Starting simulation...");
        long start = System.currentTimeMillis();

        try {
            mars.simulateFutureArrival((365/2) * 24 * 3600);  // 1 año = 365 dias, 1 dia con 24hrs, 1 hora con 3600
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
