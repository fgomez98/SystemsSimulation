package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.utils.IOUtils;
import ar.itba.edu.ss.model.AutonomusParticle;
import ar.itba.edu.ss.model.Particle;
import ar.itba.edu.ss.utils.Rand;

import java.io.IOException;
import java.util.*;

import static ar.itba.edu.ss.utils.IOUtils.*;

public class OffLattice {

    private List<AutonomusParticle> particles;
    private int M; // dim matriz
    private double L; // ancho del cell
    private int N; // numero de particulas
    private double appNoise;

    private OffLattice() {
    }

    public OffLattice(List<AutonomusParticle> particles, double l, int n, double appNoise) {
        this.particles = particles;
        this.N = n;
        this.appNoise = appNoise;
        this.L = l;
        this.M = (int) Math.ceil(l / 1); // utilizamos el M optimo
//        try {
//            IOUtils.CSVWriteParticles(particles, OFF_LATICE_STATIC_FILENAME, OFF_LATICE_DINAMIC_FILENAME, particles.size(), l);
//        } catch (IOException e) {
//            System.err.println("An error has been encountered while writing output file");
//            System.exit(1);
//        }
    }

    public OffLattice(int n, double l, double appNoise) {
        this();
        this.N = n;
        this.appNoise = appNoise;
        this.L = l;
        this.M = (int) Math.ceil(l / 1); // utilizamos el M optimo
        this.particles = AutonomusParticle.generate(n, l, 0, 2 * Math.PI, 0.03);
//        try {
//            IOUtils.CSVWriteParticles(this.particles, OFF_LATICE_STATIC_FILENAME, OFF_LATICE_DINAMIC_FILENAME, this.particles.size(), l);
//        } catch (IOException e) {
//            System.err.println("An error has been encountered while writing output file");
//            System.exit(1);
//        }
    }

    public void simulate(int time) {
        Map<Particle, Double> nextAngles = new HashMap<>(); // evitar boxing y autoboxing
        CellIndexMethod cim = new CellIndexMethod(M, L, N, 1,true);
        List<Double> velocityAverages = new LinkedList<>();

        outputParticles(particles, 0, false);

        // cada segundo (intervalo de tiempo) el automata evoluciona a un estado nuevo
        for (int i = 1; i < time + 1; i++) {

            // cargamos las particulas con su estado en el tiempo i
            cim.populate(particles);
            Map<Particle, Set<Particle>> nearby = cim.cellIndexMethod();

            double accVx = 0.0;
            double accVy = 0.0;

            for (AutonomusParticle particle : particles) {
                nextAngles.put(particle, calculateNextAngle(particle, nearby.get(particle)));
            }

            for (AutonomusParticle particle : particles) {
                updateParticle(particle, nextAngles.get(particle));
                accVx += particle.getXVelocity();
                accVy += particle.getYVelocity();
            }

            double modSum = Math.sqrt(Math.pow(accVx, 2) + Math.pow(accVy, 2)); // sqrt((vx0 + vx1 + ...)^2 + (vy0 + vy1 + ...)^2) = | sumatoria,i..N (Vi)|
            double va = modSum / (particles.size() * 0.03);

            velocityAverages.add(va);

            outputParticles(particles, i, true);
        }

        try {
            CSVWrite("off-latice-va-"+ N + "-" + appNoise + ".txt", velocityAverages, "", aDouble -> Double.toString(aDouble) + "\n", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private double calculateNextAngle(AutonomusParticle particle, Set<Particle> neighbours) {
         /*
                el automata define como evolucionan las particulas en cada instante
                X(t + 1) = X(t) + V(t) * ∆t
                O(t + 1) = ⟨θ(t)⟩r + ∆θ es el promedio de los ángulos de todas las particulas dentro de rinteraccion incluyendo a la propia particula
                arctg[⟨sin(θ(t))⟩r/⟨cos(θ(t))⟩r]
                Δθ es un ruido uniforme entre [-η/2, η/2].
        */
        double cosSum = neighbours.stream().reduce(Math.cos(particle.getAngle()), (aDouble, particle1) -> aDouble + Math.cos(((AutonomusParticle) particle1).getAngle()), Double::sum);
        double sinSum = neighbours.stream().reduce(Math.sin(particle.getAngle()), (aDouble, particle1) -> aDouble + Math.sin(((AutonomusParticle) particle1).getAngle()), Double::sum);
        double n = neighbours.size() + 1;
        double randomNoise = (Rand.getInstance().nextDouble() * appNoise) - (appNoise / 2); // uniforme(min, max) = random.nextInt(max - min) + min;
        return Math.atan2(sinSum / n, cosSum / n) + randomNoise;
    }

    private void updateParticle(AutonomusParticle particle, Double nextAngle) {
        particle.setAngle(nextAngle);
        double x = particle.getX() + particle.getXVelocity();
        double y = particle.getY() + particle.getYVelocity();
        /*
            Bordes periodicos
         */
        particle.setX(floorMod(x, L));
        particle.setY(floorMod(y, L));
    }

    /*
        Igual que Math.floorMod pero para valores de tipo double
     */
    private double floorMod(double n, double m) {
        while (n >= m) {
            n -= m;
        }
        while (n <= 0) {
            n += m;
        }
        return n;
    }

    private void outputParticles(List<AutonomusParticle> autonomusParticles, int timeInterval, boolean append) {
        try {
            IOUtils.CSVWrite(OFF_LATICE_SIMULATION_FILENAME, autonomusParticles, Integer.toString(N) + "\n" + timeInterval + "\n", Particle::toString, append);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }

    public static void main(String args[]) {
        OffLattice ol = new OffLattice(300, 5, 0.1);

        System.out.println("Generating particles...");
        double start = System.currentTimeMillis();
        List<AutonomusParticle> autonomusParticles = AutonomusParticle.generate(ol.N, ol.L, 0, 2 * Math.PI, 0.03);
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));

         /*
            nose si esto viene en un archivo o las tengo que generar yo, por ahora las imprimo y dsp voy apendeando en dinamic
         */
        try {
            IOUtils.CSVWriteParticles(autonomusParticles, OFF_LATICE_STATIC_FILENAME, OFF_LATICE_DINAMIC_FILENAME, autonomusParticles.size(), ol.L);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }

        System.out.println("Starting simulation...");
        start = System.currentTimeMillis();
        ol.simulate(20);
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));
    }

}
