package ar.itba.edu.ss.utils;

import ar.itba.edu.ss.model.MovingParticle;
import ar.itba.edu.ss.model.Particle;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class IOUtils {

    public static String STATIC_FILENAME = "static.csv";
    public static String DINAMIC_FILENAME = "dinamic.csv";
    public static String NEAR_BY_FILENAME = "nearby.csv";
    public static String OFF_LATICE_STATIC_FILENAME = "off-latice-static.txt";
    public static String OFF_LATICE_DINAMIC_FILENAME = "off-latice-dinamic.txt";
    public static String OFF_LATICE_SIMULATION_FILENAME = "off-latice-simulation.xyz";
    public static String BROWNIAN_MOTION_STATIC_FILENAME = "brownian-motion-static.txt";
    public static String BROWNIAN_MOTION_DINAMIC_FILENAME = "brownian-motion-dinamic.txt";
    public static String BROWNIAN_MOTION_SIMULATION_FILENAME = "brownian-motion-simulation.xyz";

    public static List<Particle> CSVReadParticles(String staticPath, String dinamicPath) throws IOException {
        CSVReader staticReader = new CSVReader(new FileReader(staticPath), ' ');
        CSVReader dinamicReader = new CSVReader(new FileReader(dinamicPath), ' ');
        List<Particle> resp = new ArrayList<>();
        String[] staticNextLine;
        String[] dinamicNextLine;
        staticReader.readNext();
        staticReader.readNext(); // leemos N y L
        dinamicReader.readNext(); // leemos el tiempo To
        long id = 0;
        while ((staticNextLine = staticReader.readNext()) != null && (dinamicNextLine = dinamicReader.readNext()) != null) {
            resp.add(new Particle.Builder(id++).
                    withCoordinates(Double.parseDouble(dinamicNextLine[0]), Double.parseDouble(dinamicNextLine[1]))
                    .withRadius(Double.parseDouble(staticNextLine[0]))
                    .build());
        }
        return resp;
    }

    public static List<MovingParticle> CSVReadMovingParticles(String staticPath, String dinamicPath) throws IOException {
        CSVReader staticReader = new CSVReader(new FileReader(staticPath), ' ');
        CSVReader dinamicReader = new CSVReader(new FileReader(dinamicPath), ' ');
        List<MovingParticle> resp = new ArrayList<>();
        String[] staticNextLine;
        String[] dinamicNextLine;
        staticReader.readNext();
        staticReader.readNext(); // leemos N y L
        dinamicReader.readNext(); // leemos el tiempo To
        long id = 0;
        while ((staticNextLine = staticReader.readNext()) != null && (dinamicNextLine = dinamicReader.readNext()) != null) {
            double x = Double.parseDouble(dinamicNextLine[0]);
            double y = Double.parseDouble(dinamicNextLine[1]);
            double radius = Double.parseDouble(staticNextLine[0]);
            double vx = Double.parseDouble(dinamicNextLine[2]);
            double vy = Double.parseDouble(dinamicNextLine[3]);
            double angle = Math.atan2(vy, vx);
            double velocity = Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
            resp.add((MovingParticle) new MovingParticle.Builder(id++)
                    .withVelocity(velocity)
                    .withAngle(angle)
                    .withRadius(radius)
                    .withCoordinates(x, y).build());
        }
        return resp;
    }

    public static void CSVWriteParticles(Collection<? extends Particle> particles, String staticPath, String dinamicPath, int n, double l) throws IOException {
        Writer staticWriter = new FileWriter(Paths.get(staticPath).toString());
        Writer dinamicWriter = new FileWriter(Paths.get(dinamicPath).toString());

        staticWriter.write(n + "\n");
        staticWriter.write(l + "\n");
        dinamicWriter.write(0 + "\n");

        for (Particle particle : particles) {
            staticWriter.write(particle.staticData().append("\n").toString());
            dinamicWriter.write(particle.dinamicData().append("\n").toString());
        }

        staticWriter.close();
        dinamicWriter.close();
    }

    public static <K> void CSVWrite(Path path, Collection<K> data, String headers, Function<K, String> toCsvRow, boolean append) throws IOException {
        try (Writer writer = new FileWriter(path.toString(), append)) {
            writer.write(headers);
            for (K row : data) {
                writer.write(toCsvRow.apply(row));
            }
            writer.close();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public static <K> void CSVWrite(String path, Collection<K> data, String headers, Function<K, String> toCsvRow, boolean append) throws IOException {
        CSVWrite(Paths.get(path), data, headers, toCsvRow, append);
    }

    public static void ovitoOutputParticles(String filename, List<? extends Particle> particles, int timeInterval, boolean append) {
        try {
            IOUtils.CSVWrite(filename, particles, Integer.toString(particles.size()) + "\n" + timeInterval + "\n", Particle::toString, append);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }

}
