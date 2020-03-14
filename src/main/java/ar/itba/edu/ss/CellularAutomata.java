package ar.itba.edu.ss;

import ar.itba.edu.ss.systems.OffLattice;
import ar.itba.edu.ss.utils.CmdParserUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class CellularAutomata {

    @Option(name = "-Dsinput", usage = "Filename del input", forbids = {"-Dn"})
    private String staticInputFilename;

    @Option(name = "-Ddinput", usage = "Filename del input", forbids = {"-Dn"})
    private String dinamicInputFilename;

    @Option(name = "-Dl", usage = "Longitud del lado del área cuadrada de simulación", forbids = {"-Ddinput", "Dsinput"})
    private double l;

    @Option(name = "-Dn", usage = "Numero de particulas", forbids = {"-Ddinput", "Dsinput"})
    private int n;

    @Option(name = "-Dr", usage = "Amplitud del ruido", required = true)
    private double appNoise;

    @Option(name = "-Dt", usage = "Tiempo a simular", required = true)
    private int time;

    public CellularAutomata() {
    }

    public String getStaticInputFilename() {
        return staticInputFilename;
    }

    public void setStaticInputFilename(String staticInputFilename) {
        this.staticInputFilename = staticInputFilename;
    }

    public String getDinamicInputFilename() {
        return dinamicInputFilename;
    }

    public void setDinamicInputFilename(String dinamicInputFilename) {
        this.dinamicInputFilename = dinamicInputFilename;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getAppNoise() {
        return appNoise;
    }

    public void setAppNoise(double appNoise) {
        this.appNoise = appNoise;
    }

    public double getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public static void main(String args[]) {
        CellularAutomata automata = new CellularAutomata();
        try {
            CmdParserUtils.init(args, automata);
        } catch (IOException e) {
            System.out.println("There was a problem reading the arguments");
            System.exit(1);
        }

        automata.start();
    }

    public void start() {
        OffLattice offLattice = null;

        if (staticInputFilename != null && dinamicInputFilename != null) {
//            TODO: read de particulas autonomas (hacer le metodo read generico para ambas)
//            List<AutonomusParticle> data = null;
//            try {
//                data = CSVUtils.CSVReadParticles(staticInputFilename, dinamicInputFilename);
//            } catch (Exception e) {
//                System.err.println("An error has been encountered while reading input file");
//                System.exit(1);
//            }
//
//        /*
//            Necesitamos conocer el N y L pasado en el archivo estatico
//         */
//
//            Scanner params = null;
//            try {
//                params = new Scanner(new File(STATIC_FILENAME));
//            } catch (FileNotFoundException e) {
//                System.err.println("An error has been encountered while reading input file");
//                System.exit(1);
//            }
//
//            int n = params.nextInt(); // leemos el N pasado por el archivo estatico
//            double l = params.nextDouble(); // leemos el L pasado por el archivo estatico
//
//             offLattice = new OffLattice(data, l, n, appNoise);
        } else {
             offLattice = new OffLattice(n, l, appNoise);
        }
        System.out.println("Starting simulation...");
        long startTime = System.currentTimeMillis();
        offLattice.simulate(time);
        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time elapsed: " + ((double) elapsedTime / 1000));
    }
}
