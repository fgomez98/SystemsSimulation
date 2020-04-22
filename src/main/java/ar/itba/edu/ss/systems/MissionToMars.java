package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.forces.Gravity;
import ar.itba.edu.ss.integrators.Beeman;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;

import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * a) El momento en el futuro (fecha y cuantos dias desde 06/04/2020) en el cual la nave debería partir para asegurar el arribo a marte. Para ello graficar distancia mínima a marte en función de la fecha de salida.
 * b) Tiempo de viaje.
 * c) Velocidad de la nave al llegar. Queda en órbita, pasa de largo o impacta sobre la superficie marciana?
 * d ) Repetir el análisis anterior para una velocidad V0 = 13 km/s. Optativo: Elegir una tercer velocidad propuesta y comparar.
 */

public class MissionToMars {

    private double EPSILON = 0.0000000001;

    Set<HardParticle> planets = new HashSet<>();
    Map<HardParticle, Integration> integrationMap = new HashMap<>();

    private HardParticle mars;
    private HardParticle earth;
    private HardParticle sun;
    private HardParticle spaceship;

    private double dt;
    private double dt2;
    private final int STATE_K = 4;
    private int days = 0;
    private double time = 0;
    private int initialSpeed1 = 8;      /* 8 km/hr */
    private int initialSpeed2 = 13;      /* 13 km/hr */
    private Calendar calendar;

    private static String FUTURE_ARRIVAL = "future-arrival.txt";
    private static String TIME_OF_TRIP = "time-of-trip.txt";
    private static String FINAL_SPEED = "final-speed.txt";

    public MissionToMars (double dt, int spaceshipSpeed) {

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
        mars = (HardParticle) new HardParticle.Builder()
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
        earth = (HardParticle) new HardParticle.Builder()
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
        sun = (HardParticle) new HardParticle.Builder()
                .withMass(1988500E24)
                .withVelocity(0, 0)
                .withRadius(695700)
                .withCoordinates(0, 0).build();


        spaceship = (HardParticle) new HardParticle.Builder()
                .withMass(2E5)
                .withRadius(0.2)     /* a nuestro criterio */
                .build();
        locateSpaceship(spaceship, spaceshipSpeed);

        this.dt = dt;
        this.dt2 = dt * STATE_K;

        calendar = new GregorianCalendar(2020, Calendar.APRIL, 4);;

        planets.add(mars);
        planets.add(sun);
        planets.add(earth);
        integrationMap.put(mars, new Beeman(new Gravity(getNeighbours(mars))));
        integrationMap.put(earth, new Beeman(new Gravity(getNeighbours(earth))));
        integrationMap.put(sun, new Beeman(new Gravity(getNeighbours(sun))));
//        integrationMap.put(spaceship, new Beeman(new Gravity(getNeighbours(spaceship))));
    }

    private void locateSpaceship (HardParticle spaceship, int initialSpeed) {

        double sunToEarthDist = sun.distanceTo(earth);
        double spaceshipToEarthDist = earth.getRadius() + spaceship.getRadius() + 1500;

        double ex = (earth.getX() - sun.getX()) / sunToEarthDist;       /* coseno del angulo */
        double ey = (earth.getY() - sun.getY()) / sunToEarthDist;       /* seno  del angulo */

        double xCoord = earth.getX() + spaceshipToEarthDist*ex;
        double yCoord = earth.getY() + spaceshipToEarthDist*ey;

        double speed = initialSpeed + 7.12;

        /* direccion de la nave --> etx = -ey ; ety = ex */
        double vx = speed * (-ey);
        double vy = speed * ex;

        vx += earth.getXVelocity();
        vy += earth.getYVelocity();

        spaceship.setVelocity(vx, vy);
        spaceship.setCoordinates(xCoord, yCoord);

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

            for (HardParticle p : planets) {
                integrationMap.get(p).calculate(p, dt);
            }

            time += dt;
            updateDays();
        }

        outputCalculations();

    }

    // a) El momento en el futuro (fecha y cuantos dias desde 06/04/2020) en el cual la nave debería partir para asegurar el arribo a marte. Para ello graficar distancia mínima a marte en función de la fecha de salida.

    private double simulateFutureArrival(double simulationTimeFuture, double simulationTimeTotal) throws IOException {
        boolean hasLaunched = false;
        double launchTime = 0;
        double minDist = Double.MAX_VALUE;
        Set<HardParticle> savedPositions = planets;
        while (launchTime <= simulationTimeTotal) {
            planets = savedPositions;
            savedPositions = saveLastSimulationPositions();
            double currentSimulationTime = launchTime;
            while (currentSimulationTime <= simulationTimeTotal) {
                if (launchTime == currentSimulationTime) {
                    addSpaceShip();
                    hasLaunched = true;
                }
                for (HardParticle p : planets) {
                    integrationMap.get(p).calculate(p, dt);
                }
                if (hasLaunched) {
                    double dist = spaceship.distanceTo(mars);
                    if (dist < minDist) {
                        minDist = dist;

                    }
                    if (dist <= EPSILON) {
                        // arrived
                        break;
                    }
                }
                currentSimulationTime += dt;
            }
            launchTime += dt;
        }
        return launchTime;
    }

    private Set<HardParticle> saveLastSimulationPositions() {
        Set<HardParticle> positions = new HashSet<>();
        for (HardParticle p: planets ) {
            positions.add(p.copy());
        }
        return positions;
    }

    private void addSpaceShip() {
        // esto tendria que ser acorde a la posicion de la tierra y otras cosas ?
        planets.add(spaceship);
        // re calculamos los vecinos para las fuerzas
        integrationMap = new HashMap<>();
        integrationMap.put(mars, new Beeman(new Gravity(getNeighbours(mars))));
        integrationMap.put(earth, new Beeman(new Gravity(getNeighbours(earth))));
        integrationMap.put(sun, new Beeman(new Gravity(getNeighbours(sun))));
        integrationMap.put(spaceship, new Beeman(new Gravity(getNeighbours(spaceship))));
    }

    private void outputPredictions() throws IOException {

        List<HardParticle> data = new LinkedList<>();
        data.add(spaceship);
        SimpleDateFormat formattedDate
                = new SimpleDateFormat("dd-MMM-yyyy");

        IOUtils.CSVWrite(FUTURE_ARRIVAL,
                data,
                "",
                particle -> days + ", " + formattedDate.format(calendar.getTime()),
                false);
    }

    private boolean hasSpaceshipArrived() {

        double distance = spaceship.distanceTo(mars);

        return distance < EPSILON;
    }

    private void updateDays() {

        int simulationDays = (int) time / 3600;
        days += simulationDays - days;
    }

    private void restartCalendar () {

        calendar.set(2020, Calendar.APRIL, 4);
    }

    Set<HardParticle> getNeighbours(HardParticle planet) {
        Set<HardParticle> neigh = new HashSet<>();
        for (HardParticle p : planets) {
            if (!p.equals(planet)) {
                neigh.add(p);
            }
        }
        return neigh;
    }
}
