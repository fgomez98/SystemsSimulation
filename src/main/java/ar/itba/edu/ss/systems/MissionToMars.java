package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.forces.Gravity;
import ar.itba.edu.ss.integrators.Beeman;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * a) El momento en el futuro (fecha y cuantos dias desde 06/04/2020) en el cual la nave debería partir para asegurar el arribo a marte. Para ello graficar distancia mínima a marte en función de la fecha de salida.
 * b) Tiempo de viaje.
 * c) Velocidad de la nave al llegar. Queda en órbita, pasa de largo o impacta sobre la superficie marciana?
 * d ) Repetir el análisis anterior para una velocidad V0 = 13 km/s. Optativo: Elegir una tercer velocidad propuesta y comparar.
 */

public class MissionToMars {

    public static String SIMULATION_FILENAME = "mars-travel.xyz";
    private double EPSILON = 0.00001;

    List<HardParticle> bodies;

    private HardParticle mars;
    private HardParticle earth;
    private HardParticle sun;
    private HardParticle spaceship;
    private HardParticle jupiter;
    private HardParticle venus;

    private Set<HardParticle> marsNeighbours;
    private Set<HardParticle> earthNeighbours;
    private Set<HardParticle> sunNeighbours;
    private Set<HardParticle> spaceshipNeighbours;
    private Set<HardParticle> jupiterNeighbours;
    private Set<HardParticle> venusNeighbours;

    private Integration marsIntegrator;
    private Integration earthIntegrator;
    private Integration sunIntegrator;
    private Integration spaceshipIntegrator;
    private Integration jupiterIntegrator;
    private Integration venusIntegrator;

    private double dt;
    private double dt2;
    private final int STATE_K = 4;
    private double time = 0;
    private double initialSpeed = 8.0;      /* 8 km/hr */

    private static String FUTURE_ARRIVAL = "future-arrival.txt";
    private static String TIME_OF_TRIP = "time-of-trip.txt";
    private static String FINAL_SPEED = "final-speed.txt";

    public MissionToMars(double dt, double spaceshipSpeed) {
        this.dt = dt;
        this.dt2 = dt * STATE_K;
        this.initialSpeed = spaceshipSpeed;
        initPlanets(false);
    }

    public void simulate(double simulationTimeTotal, double launchDay) throws IOException {
        simulate(simulationTimeTotal, launchDay, initialSpeed, true);
    }

    private TravelData simulate(double simulationTimeTotal, double launchDay, double initialVelocity, boolean outputOvito) {
        initialSpeed = initialVelocity;
        initPlanets(false);
        boolean hasLaunched = false;
        double minDist = Double.MAX_VALUE;
        double minTime = 0;
        double velocityAtMin = 0;
        double launchTime = launchDay * 86400; // -> a segundos
        double currentSimulationTime = 0;
        int frame = 0;

        if (outputOvito) {
            // creamos el archivo
            IOUtils.ovitoOutputParticles(SIMULATION_FILENAME, bodies, frame++, false);
        }

        while (currentSimulationTime <= simulationTimeTotal) {
            if (!hasLaunched && (currentSimulationTime >= launchTime)) {
                hasLaunched = true;
                launchTime = currentSimulationTime;
                addSpaceShip();
            }
            movePlanets();
            if (hasLaunched) {
                moveSpaceShip();
                double dist = Point.distance(spaceship.getX(), spaceship.getY(), mars.getX(), mars.getY());
                if (dist < minDist) {
                    minDist = dist;
                    minTime = currentSimulationTime;
                    velocityAtMin = Math.sqrt(Math.pow(spaceship.getXVelocity(), 2) + Math.pow(spaceship.getYVelocity(), 2));
                }
            }
            if (outputOvito) {
                // output del estado
                IOUtils.ovitoOutputParticles(SIMULATION_FILENAME, bodies, frame++, true);
            }
            currentSimulationTime += dt;
        }
        return new TravelData((minTime - launchTime), launchDay, minDist, velocityAtMin, initialSpeed);
    }

    private void simulateFutureArrivalV(double simulationTimeTotal, double launchDay, double vStart, double vEnd, double vStep) throws IOException {
        List<TravelData> distances = new ArrayList<>();
        vStep = (vStep == -1) ? 10 : vStep;
        vStart = (vStart == -1) ? initialSpeed : vStart;
        vEnd = (vEnd == -1) ? 120.0 : vEnd;
        while (vStart < vEnd) {
            distances.add(simulate(simulationTimeTotal, launchDay, vStart, false));
            vStart += vStep;
        }
        IOUtils.CSVWrite("distances_velocity_mars.txt", distances, "Initial Velocity,Distance\n", d -> d.velocityInitial + "," + d.dst + "\n", false);
        TravelData opt = distances.stream().min(Comparator.comparingDouble(TravelData::getDst)).get();
        System.out.println("Launch on day: " + opt.getDay() + " Distace: " + opt.getDst() + " Travel Time: " + opt.getTravelTime() + " Final Velocity: " + opt.getVelocityFinal() + "Initial Velocity: " + opt.getVelocityInitial());
    }

    private void simulateFutureArrivalD(double simulationTimeTotal, double dayFrom, double dayTo, double dSetp) throws IOException {
        List<TravelData> distances = new ArrayList<>();
        double days = (dayFrom == -1) ? 0 : dayFrom;
        double daysTotal = (dayTo == -1) ? (simulationTimeTotal / 86400) : dayTo;
        dSetp = (dSetp == -1) ? 1 : dSetp;
        while (days < daysTotal) { // dias desde 06/04/2020 cuando se efectua el lanzamiento
            distances.add(simulate(simulationTimeTotal, days, initialSpeed, false));
            days += dSetp;
        }
        IOUtils.CSVWrite("distances_mars.txt", distances, "Days,Distance\n", d -> d.day + "," + d.dst + "\n", false);
        TravelData opt = distances.stream().min(Comparator.comparingDouble(TravelData::getDst)).get();
        System.out.println("Launch on day: " + opt.getDay() + " Distace: " + opt.getDst() + " Travel Time: " + opt.getTravelTime() + " Final Velocity: " + opt.getVelocityFinal() + "Initial Velocity: " + opt.getVelocityInitial());
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
        locateSpaceship(spaceship, initialSpeed);

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

    private void movePlanets() {
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

        marsIntegrator = new Beeman(new Gravity(marsNeighbours));
        earthIntegrator = new Beeman(new Gravity(earthNeighbours));
        sunIntegrator = new Beeman(new Gravity(sunNeighbours));
    }

    private void moveSpaceShip() {
        spaceshipIntegrator.calculate(spaceship, dt);

        spaceshipNeighbours = new HashSet<>();
        spaceshipNeighbours.add(sun.copy());
        spaceshipNeighbours.add(earth.copy());
        spaceshipNeighbours.add(mars.copy());

        sunNeighbours.add(spaceship.copy());
        earthNeighbours.add(spaceship.copy());
        marsNeighbours.add(spaceship.copy());

        spaceshipIntegrator = new Beeman(new Gravity(spaceshipNeighbours));
    }

    private void initPlanets(boolean addPlanets) {
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

        /* JUPITER */
        /*
            Mass x10^24 (kg)      = 1898.13+-.19
            Vol. Mean Radius (km) = 69911+-6
            X = 1.231546427485162E+00 Y =-5.044220968142067E+00
            VX= 7.247395081155700E-03 VY= 2.147729550898136E-03
         */

        jupiter = (HardParticle) new HardParticle.Builder(2)
                .withMass(1898.13E24)
                .withVelocity(12.548551762297989853, 3.7187010145119026028)
                .withRadius(69911)
                .withCoordinates(184236723.21997219324, -754604716.17434585094)
                .build();

        /* VENUS */
        /*
            Vol. Mean Radius (km) =  6051.84+-0.01
            Mass x10^23 (kg)      =    48.685
            X =-6.695856951979585E-01 Y = 2.585014590403438E-01
            VX=-7.377780583932116E-03 VY=-1.896246682745872E-02
         */

        venus = (HardParticle) new HardParticle.Builder(2)
                .withMass(48.685E23)
                .withVelocity(-12.774308632500545713, -32.832692831101951469)
                .withRadius(6051.84)
                .withCoordinates(-100168594.25279380381, 38671267.845278695226)
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
        if (addPlanets) marsNeighbours.add(jupiter.copy());
        if (addPlanets) marsNeighbours.add(venus.copy());

        earthNeighbours = new HashSet<>();
        earthNeighbours.add(sun.copy());
        earthNeighbours.add(mars.copy());
        if (addPlanets) earthNeighbours.add(jupiter.copy());
        if (addPlanets) earthNeighbours.add(venus.copy());

        sunNeighbours = new HashSet<>();
        sunNeighbours.add(earth.copy());
        sunNeighbours.add(mars.copy());
        if (addPlanets) sunNeighbours.add(jupiter.copy());
        if (addPlanets) sunNeighbours.add(venus.copy());

        spaceshipNeighbours = new HashSet<>();
        spaceshipNeighbours.add(sun.copy());
        spaceshipNeighbours.add(earth.copy());
        spaceshipNeighbours.add(mars.copy());
        if (addPlanets) spaceshipNeighbours.add(jupiter.copy());
        if (addPlanets) spaceshipNeighbours.add(venus.copy());

        if (addPlanets) {
            jupiterNeighbours = new HashSet<>();
            jupiterNeighbours.add(earth.copy());
            jupiterNeighbours.add(mars.copy());
            jupiterNeighbours.add(sun.copy());
            jupiterNeighbours.add(venus.copy());

            venusNeighbours = new HashSet<>();
            venusNeighbours.add(earth.copy());
            venusNeighbours.add(mars.copy());
            venusNeighbours.add(sun.copy());
            venusNeighbours.add(jupiter.copy());
        }

        marsIntegrator = new Beeman(new Gravity(marsNeighbours));
        earthIntegrator = new Beeman(new Gravity(earthNeighbours));
        sunIntegrator = new Beeman(new Gravity(sunNeighbours));
        spaceshipIntegrator = new Beeman(new Gravity(spaceshipNeighbours));
        if (addPlanets) {
            jupiterIntegrator = new Beeman(new Gravity(jupiterNeighbours));
            venusIntegrator = new Beeman(new Gravity(venusNeighbours));
        }

        bodies = new ArrayList<>();
        bodies.add(mars);
        bodies.add(sun);
        bodies.add(earth);
        if (addPlanets) {
            bodies.add(jupiter);
            bodies.add(venus);
        }
    }

    public static void main(String[] args) {

        MissionToMars mars = new MissionToMars(360, 8); // definir esto // -> a segundos

        System.out.println("Starting simulation...");
        long start = System.currentTimeMillis();

        try {
            mars.simulateFutureArrivalD(2 * 365 * 24 * 3600, -1, -1, 30);  // 1 año = 365 dias, 1 dia con 24hrs, 1 hora con 3600
//            mars.simulateFutureArrivalV(2 * 365 * 24 * 3600, 113.0, 5, 15, 0.1);  // 1 año = 365 dias, 1 dia con 24hrs, 1 hora con 3600
//            mars.simulate(2 * 365 * 24 * 3600, 113.0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }

    private class TravelData {
        private double travelTime;
        private double day;
        private double dst;
        private double velocityFinal;
        private double velocityInitial;

        public TravelData(double travelTime, double day, double dst, double velocityFinal, double velocityInitial) {
            this.travelTime = travelTime;
            this.day = day;
            this.dst = dst;
            this.velocityFinal = velocityFinal;
            this.velocityInitial = velocityInitial;
        }

        @Override
        public String toString() {
            return "travelTime=" + travelTime +
                    ", day=" + day +
                    ", dst=" + dst +
                    ", velocity final=" + velocityFinal +
                    ", velocity initial=" + velocityInitial;
        }

        public double getTravelTime() {
            return travelTime;
        }

        public double getDay() {
            return day;
        }

        public double getDst() {
            return dst;
        }

        public double getVelocityFinal() {
            return velocityFinal;
        }

        public double getVelocityInitial() {
            return velocityInitial;
        }
    }
}
