package ar.itba.edu.ss;

import ar.itba.edu.ss.model.Particle;
import ar.itba.edu.ss.utils.CmdParserUtils;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.*;

import static ar.itba.edu.ss.utils.IOUtils.CSVWriteParticles;
import static ar.itba.edu.ss.utils.IOUtils.STATIC_FILENAME;
import static ar.itba.edu.ss.utils.IOUtils.DINAMIC_FILENAME;

public class Generate {

    @Option(name = "-Dsoutput", usage = "Filename del output")
    private String staticOutputFilename = "./" + STATIC_FILENAME;

    @Option(name = "-Ddoutput", usage = "Filename del output")
    private String dinamicOutputFilename = "./" + DINAMIC_FILENAME;

    @Option(name = "-Dl", usage = "Longitud del lado del área cuadrada de simulación", required = true)
    private double l; // ancho del cell

    @Option(name = "-Dn", usage = "Numero de particulas", required = true)
    private int n; // numero de particulas

    @Option(name = "-Drmax", usage = "Radio maximo", required = true)
    private double radio_max;

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

    public double getRadio_max() {
        return radio_max;
    }

    public void setRadio_max(double radio_max) {
        this.radio_max = radio_max;
    }

    public static void main(String[] args) {
        Generate app = new Generate();

        try {
            CmdParserUtils.init(args, app);
        } catch (IOException e) {
            System.out.println("There was a problem reading the arguments");
            System.exit(1);
        }

        app.generate();
    }

    public void generate() {
        List<Particle> particleList = Particle.generate(n, l, radio_max);
        try {
            CSVWriteParticles(particleList, staticOutputFilename, dinamicOutputFilename, n, l);
        } catch (IOException e) {
            System.out.println("There was a problem writing content to output file");
            System.exit(1);
        }
    }
}
