package ar.itba.edu.ss;

import ar.itba.edu.ss.systems.MissionToMars;
import ar.itba.edu.ss.systems.SpringOscillator;
import ar.itba.edu.ss.utils.CmdParserUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class MolecularDinamicGravitatory {

    @Option(name = "-Ds", usage = "Paso temporal", required = true)
    private double dt;

    @Option(name = "-Dv", usage = "Velocidad inicial de la nave", required = true)
    private double initialSpeed;

    @Option(name = "-DT", usage = "Tiempo a simular", required = true)
    private int time;

    @Option(name = "-Dd", usage = "Paso temporal en días para buscar el día óptimo de despegue", required = true)
    private int dayStep;

    @Option(name = "-Df", usage = "Cantidad de días después del 06/04 para iniciar la búsqueda del día óptimo de despegue", required = true)
    private int dayFrom;

    @Option(name = "-Dt", usage = "Cantidad de días después del 06/04 para terminar la búsqueda del día óptimo de despegue", required = true)
    private int dayTo;

    @Option(name = "-Dj", usage = "0 (false) o 1 (true) para agregar a Júpiter y Venus al sistema", required = true)
    private int addPlanets;

    public double getDt() {
        return dt;
    }

    public double getInitialSpeed () { return initialSpeed; }

    public double getTime () { return time; }

    public int getDayFrom() {
        return dayFrom;
    }

    public int getDayStep() {
        return dayStep;
    }

    public int getDayTo() {
        return dayTo;
    }

    public int getAddPlanets() {
        return addPlanets;
    }

    public MolecularDinamicGravitatory() {
    }

    public static void main(String args[]) {
        MolecularDinamicGravitatory gravitatory = new MolecularDinamicGravitatory();
        try {
            CmdParserUtils.init(args, gravitatory);
        } catch (IOException e) {
            System.out.println("There was a problem reading the arguments");
            System.exit(1);
        }

        gravitatory.start();
    }

    public void start() {

        MissionToMars mission = new MissionToMars(getDt(), getInitialSpeed(), getAddPlanets() == 1);

        System.out.println("Starting simulation...");

        long startTime = System.currentTimeMillis();

        try {
            mission.simulateFutureArrivalD(getTime(), getDayFrom(), getDayTo(), getDayStep());
        } catch (IOException e) {
            e.printStackTrace();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Time elapsed: " + ((double) elapsedTime / 1000));
    }
}
