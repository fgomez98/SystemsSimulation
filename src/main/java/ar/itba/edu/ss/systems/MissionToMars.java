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
    private boolean missionComplete = false;
    private Calendar calendar;

    private static String FUTURE_ARRIVAL = "future-arrival.txt";
    private static String TIME_OF_TRIP = "time-of-trip.txt";
    private static String FINAL_SPEED = "final-speed.txt";

    public MissionToMars(double dt) {

        /* sacamos las condiciones iniciales del link  https://ssd.jpl.nasa.gov/horizons.cgi#top al día 06/04 */

        /* MARTE */
        /*
            radio 3389.92+-0.04 km
            Mass x10^23 (kg)      =    6.4171
            X =-1.697831879172063E-01 Y =-1.452533227422469E+00     TODO: unidades?
            VX= 1.442541741631660E-02 VY=-3.732586868630861E-04     TODO: unidades?
         */
        mars = (HardParticle) new HardParticle.Builder()
                .withMass(6.4171E23)
                .withVelocity(1.442541741631660E-02, -3.732586868630861E-04)
                .withRadius(3389.92)
                .withCoordinates(-1.697831879172063E-01, -1.452533227422469E+00)
                .build();

        /* TIERRA */
        /*
            radio 6371.01+-0.02 km
            Mass x10^24 (kg)= 5.97219+-0.0006
            X =-9.646530350221529E-01 Y =-2.750306360859890E-01     TODO: unidades?
            VX= 4.564771838790553E-03 VY=-1.656625701516261E-02     TODO: unidades?
         */
        earth = (HardParticle) new HardParticle.Builder()
                .withMass(5.97219E24)
                .withVelocity(4.564771838790553E-03, -1.656625701516261E-02)
                .withRadius(6371.01)
                .withCoordinates(-9.646530350221529E-01, -2.750306360859890E-01)
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

        /* TODO: radio?
         * TODO: velocidad inicial 8 km por hora. tangente a la orbita de la tierra
         *       + velocidad orbital respecto de la tierra es de 7,12 km/s
         * TODO: coordenadas: a 1500 km de la tierra (alineado con el sol)
         * */
        /* 1500^2 = x^2 + y^2 con x = y --> 1500^2 = 2*x^2 '--> x = 1500/2^(1/2) */
        double distance = 1500 / Math.sqrt(2);
        spaceship = (HardParticle) new HardParticle.Builder()
                .withMass(2E5)
                .withVelocity(-8.200470787101123E-06, -2.915722838615252E-06)
                .withCoordinates(-9.646530350221529E-01 + distance, -2.750306360859890E-01 + distance)
                .build();

        this.dt = dt;
        this.dt2 = dt * STATE_K;

        calendar = new GregorianCalendar(2020, 06, 04);

        planets.add(mars);
        planets.add(spaceship);
        planets.add(sun);
        planets.add(earth);
        integrationMap.put(mars, new Beeman(new Gravity(getNeighbours(mars))));
        integrationMap.put(earth, new Beeman(new Gravity(getNeighbours(earth))));
        integrationMap.put(sun, new Beeman(new Gravity(getNeighbours(sun))));
        integrationMap.put(spaceship, new Beeman(new Gravity(getNeighbours(spaceship))));
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

        while (!missionComplete || simulationTime > time) {

            /* usar los 3 integradores para actualizar la posicion de la nave y los planetas y el sol en cada dt */

            for (HardParticle p : planets) {
                integrationMap.get(p).calculate(p, dt);
            }

            time += dt;
            updateDays();
            missionComplete = hasSpaceshipArrived();
        }

        outputCalculations();

    }

    // a) El momento en el futuro (fecha y cuantos dias desde 06/04/2020) en el cual la nave debería partir para asegurar el arribo a marte. Para ello graficar distancia mínima a marte en función de la fecha de salida.

    private double simulateFutureArrival(double simulationTimeFuture, double simulationTime) throws IOException {
        double futureTime = 0;
        double currentTime = 0;
        double minDist = Double.MAX_VALUE;
        while (currentTime < simulationTime) {

            for (HardParticle p : planets) {
                integrationMap.get(p).calculate(p, dt);
                double dist = spaceship.distanceTo(mars);
                if (dist < minDist) {
                    minDist = dist;
                    futureTime = currentTime;
                }
                if (dist <= EPSILON) {
                    // arrived
                    break;
                }

                currentTime += dt;
            }
        }
        return futureTime;
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

        /* fijar un criterio */

        return false;
    }

    private void updateDays() {

        int simulationDays = (int) time / 3600;
        days += simulationDays - days;
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
