package ar.itba.edu.ss;

import ar.itba.edu.ss.model.Particle;
import ar.itba.edu.ss.systems.CellIndexMethod;
import ar.itba.edu.ss.utils.CSVUtils;
import ar.itba.edu.ss.utils.CmdParserUtils;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.*;

import static ar.itba.edu.ss.utils.CSVUtils.STATIC_FILENAME;
import static ar.itba.edu.ss.utils.CSVUtils.DINAMIC_FILENAME;
import static ar.itba.edu.ss.utils.CSVUtils.NEAR_BY_FILENAME;

public class ComputeNeighbors {

    private enum Method {CIM, BRUTE}

    @Option(name = "-Dsinput", usage = "Filename del input")
    private String staticInputFilename = "./" + STATIC_FILENAME;

    @Option(name = "-Ddinput", usage = "Filename del input")
    private String dinamicInputFilename = "./" + DINAMIC_FILENAME;

    @Option(name = "-Dmethod", usage = "Metodo a utilizar")
    private Method method = Method.CIM;

    @Option(name = "-Dm", usage = "Dimensión matriz cuadrada", required = true)
    private int m; // dim matriz

    @Option(name = "-Drc", usage = "Radio de interaccion", required = true)
    private double radio_interaccion;

    @Option(name = "-Dc", usage = "condiciones periódicas de contorno")
    private boolean contorno;

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

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public double getRadio_interaccion() {
        return radio_interaccion;
    }

    public void setRadio_interaccion(double radio_interaccion) {
        this.radio_interaccion = radio_interaccion;
    }

    public boolean isContorno() {
        return contorno;
    }

    public void setContorno(boolean contorno) {
        this.contorno = contorno;
    }

    public static void main(String[] args) {
        ComputeNeighbors app = new ComputeNeighbors();

        try {
            CmdParserUtils.init(args, app);
        } catch (IOException e) {
            System.out.println("There was a problem reading the arguments");
            System.exit(1);
        }

        app.compute();
    }

    public void compute() {
        List<Particle> data = null;
        try {
            data = CSVUtils.CSVReadParticles(staticInputFilename, dinamicInputFilename);
        } catch (Exception e) {
            System.err.println("An error has been encountered while reading input file");
            System.exit(1);
        }

        /*
            Necesitamos conocer el N y L pasado en el archivo estatico
         */

        Scanner params = null;
        try {
            params = new Scanner(new File(STATIC_FILENAME));
        } catch (FileNotFoundException e) {
            System.err.println("An error has been encountered while reading input file");
            System.exit(1);
        }

        int n = params.nextInt(); // leemos el N pasado por el archivo estatico
        double l = params.nextDouble(); // leemos el L pasado por el archivo estatico

        double maxRadius = data.stream().max(Comparator.comparingDouble(Particle::getRadius)).orElse(new Particle(0)).getRadius();

        // criterio: L/M > rc + 2 * radioMax
        if (l / m < radio_interaccion + 2 * maxRadius) {
            // corregimos para ir de acuerdo con el criterio
            m = (int) Math.ceil(l / radio_interaccion + 2 * maxRadius);
        }

        CellIndexMethod cim = new CellIndexMethod(m, l, n, radio_interaccion, contorno);
        cim.populate(data);

        Map<Particle, Set<Particle>> nearbyParticlesMap = null;

        long startTime = System.currentTimeMillis();

        switch (method) {
            case BRUTE:
                nearbyParticlesMap = cim.forceBruteMethod();
                break;
            case CIM:
                nearbyParticlesMap = cim.cellIndexMethod();
                break;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.print(Double.toString((double) elapsedTime / 1000));

        try {
            CSVUtils.CSVWrite(NEAR_BY_FILENAME, nearbyParticlesMap.entrySet(), "", e -> "[" + e.getKey().getId() + " " + e.getValue().stream().reduce("", (s, particle) -> s + " " + particle.getId(), String::concat) + "]\n", false);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }
}
