package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.forces.SpringOscillatorForce;
import ar.itba.edu.ss.integrators.AnaliticalSpring;
import ar.itba.edu.ss.integrators.Beeman;
import ar.itba.edu.ss.integrators.GearOrder5;
import ar.itba.edu.ss.integrators.Verlet;
import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SpringOscillator {

    public static String SPRING_OSCILLATOR_ANALITICAL_FILENAME = "spring-oscillator-analitical-simulation.txt";
    public static String SPRING_OSCILLATOR_VERLET_FILENAME = "spring-oscillator-verlet-simulation.txt";
    public static String SPRING_OSCILLATOR_BEEMAN_FILENAME = "spring-oscillator-beeman-simulation.txt";
    public static String SPRING_OSCILLATOR_GEAR_FILENAME = "spring-oscillator-gear-simulation.txt";
    public static double SAVE_STATE_K = 5;

    /*
    simulacion de una particula
        m = 70 kg; k = 104 N/m; 𝛾 = 100 kg/s; tf = 5 s
        r (t=0) = 1 m;
        v (t=0) = - A 𝛾/(2m) m/s;
     */
    private final double k = 10000; // constante del resorte
    private final double gamma = 100; // Kg/s
    private double dt; // differencial de tiempo
    private double dt2; // differencial de tiempo para guardar el estado
    private final double mass = 70; // Kgh
    private final double r = 1; // m
    private final double amplitud = r; // m

    public SpringOscillator(double dt) {
        this.dt = dt;
        this.dt2 = dt * SAVE_STATE_K;
    }

    private HardParticle particle = (HardParticle) new HardParticle.Builder()
            .withMass(mass)
            .withVelocity(-(amplitud * gamma) / (2 * mass), 0.0)
            .withCoordinates(1, 0).build(); // solo eje x


    public double analiticalSolution(double time) {
        return amplitud * Math.exp(-1 * ((gamma) / (2 * mass)) * time) * Math.cos(Math.sqrt((k / mass) - ((gamma * gamma) / (4 * mass * mass))) * time);
    }

    public void simulate(double simulationTime, Integration integrationMethod, String outFilename) throws IOException {
        createFiles(outFilename);
        double time = 0;
        while (time <= simulationTime) {
//            if ((time / dt2) - Math.round(time / dt2) == 0) {
            outputData(time, outFilename);
//            }
            integrationMethod.calculate(particle, dt);
            time += dt;
        }
    }

    public void simulateAll(double simulationTime) throws IOException {
        simulate(simulationTime, new AnaliticalSpring(k, gamma, mass, amplitud), SPRING_OSCILLATOR_ANALITICAL_FILENAME);
        particle.setVelocity(-(amplitud * gamma) / (2 * mass), 0.0);
        particle.setCoordinates(1, 0);
        simulate(simulationTime, new Verlet(new SpringOscillatorForce(k, gamma)), SPRING_OSCILLATOR_VERLET_FILENAME);
        particle.setVelocity(-(amplitud * gamma) / (2 * mass), 0.0);
        particle.setCoordinates(1, 0);
        simulate(simulationTime, new Beeman(new SpringOscillatorForce(k, gamma)), SPRING_OSCILLATOR_BEEMAN_FILENAME);
        particle.setVelocity(-(amplitud * gamma) / (2 * mass), 0.0);
        particle.setCoordinates(1, 0);
        simulate(simulationTime, new GearOrder5(new SpringOscillatorForce(k, gamma)), SPRING_OSCILLATOR_GEAR_FILENAME);
    }

    private void createFiles(String outFilename) throws IOException {
        IOUtils.CSVWrite(outFilename,
                new LinkedList<>(),
                "Time,Position\n",
                null, // no hay problema ya que no hay datos a aplicar la funcion
                false);
    }

    private void outputData(double time, String outFilename) throws IOException {
        List<HardParticle> data = new LinkedList<>();
        data.add(particle);
        IOUtils.CSVWrite(outFilename,
                data,
                "",
                p -> time + ", " + p.getX() + "\n",
                true);
    }

    public static void main(String[] args) {
        SpringOscillator so = new SpringOscillator(0.01);

        System.out.println("Starting simulation...");
        long start = System.currentTimeMillis();

        try {
            so.simulateAll(5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }
}
