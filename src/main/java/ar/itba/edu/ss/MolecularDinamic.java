package ar.itba.edu.ss;

import ar.itba.edu.ss.systems.BrownianMotion;
import ar.itba.edu.ss.utils.CmdParserUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class MolecularDinamic {

    public static enum DCM {
        BIGG, SMALL
    }

    @Option(name = "-Dn", usage = "Numero de particulas", forbids = {"-Ddinput", "Dsinput"})
    private int n;

    @Option(name = "-Dt", usage = "Tiempo a simular", required = true)
    private int time;


    @Option(name = "-Ddcm", usage = "Coeficiente de difusión de la partícula")
    private DCM dcm;

    @Option(name = "-Dsinput", usage = "Archivo estatico", forbids = {"-Dn"})
    private String staticInputFilename;

    @Option(name = "-Ddinput", usage = "Archivo dinamico", forbids = {"-Dn"})
    private String dinamicInputFilename;

    public MolecularDinamic() {
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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

    public static void main(String args[]) {
        MolecularDinamic md = new MolecularDinamic();
        try {
            CmdParserUtils.init(args, md);
        } catch (IOException e) {
            System.out.println("There was a problem reading the arguments");
            System.exit(1);
        }

        md.start();
    }

    public void start() {
        BrownianMotion brownianMotion = null;

        if (staticInputFilename != null && dinamicInputFilename != null) {
//
////            TODO: read de particulas autonomas (hacer le metodo read generico para ambas)
//
//            List<MovingParticle> data = null;
//            try {
//                data = IOUtils.CSVReadMovingParticles(staticInputFilename, dinamicInputFilename);
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
//                params = new Scanner(new File(staticInputFilename));
//            } catch (FileNotFoundException e) {
//                System.err.println("An error has been encountered while reading input file");
//                System.exit(1);
//            }
//
//            int n = params.nextInt(); // leemos el N pasado por el archivo estatico
//            double l = params.nextDouble(); // leemos el L pasado por el archivo estatico
//
//            brownianMotion = new BrownianMotion(10);
            return;
        } else {
            brownianMotion = new BrownianMotion(n);
        }

        System.out.println("Starting simulation...");

        long startTime = System.currentTimeMillis();

        try {
            if (dcm != null) {
                brownianMotion.simulateDCM(time, dcm);
            } else {
                brownianMotion.simulate(time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Time elapsed: " + ((double) elapsedTime / 1000));
    }
}
