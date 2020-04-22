package ar.itba.edu.ss;

import ar.itba.edu.ss.systems.SpringOscillator;
import ar.itba.edu.ss.utils.CmdParserUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class MolecularDinamicOscillator {

    @Option(name = "-Dt", usage = "Paso temporal", required = true)
    private double dt;

    public double getDt() {
        return dt;
    }

    public MolecularDinamicOscillator() {
    }

    public static void main(String args[]) {
        MolecularDinamicOscillator oscillator = new MolecularDinamicOscillator();
        try {
            CmdParserUtils.init(args, oscillator);
        } catch (IOException e) {
            System.out.println("There was a problem reading the arguments");
            System.exit(1);
        }

        oscillator.start();
    }

    public void start() {
        SpringOscillator springOscillator = new SpringOscillator(getDt());

        System.out.println("Starting simulation...");

        long startTime = System.currentTimeMillis();

        try {
            springOscillator.simulateAll(5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Time elapsed: " + ((double) elapsedTime / 1000));
    }
}
