package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.utils.CSVUtils;
import ar.itba.edu.ss.model.AutonomusParticle;
import ar.itba.edu.ss.model.Particle;
import ar.itba.edu.ss.utils.Rand;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ar.itba.edu.ss.utils.CSVUtils.*;

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
        try {
            CSVUtils.CSVWriteParticles(particles, OFF_LATICE_STATIC_FILENAME, OFF_LATICE_DINAMIC_FILENAME, particles.size(), l);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }

    public OffLattice(int n, double l, double appNoise) {
        this();
        this.N = n;
        this.appNoise = appNoise;
        this.L = l;
        this.M = (int) Math.ceil(l / 1); // utilizamos el M optimo
        this.particles = AutonomusParticle.generate(n, l, 0, 2 * Math.PI, 0.03);
        try {
            CSVUtils.CSVWriteParticles(this.particles, OFF_LATICE_STATIC_FILENAME, OFF_LATICE_DINAMIC_FILENAME, this.particles.size(), l);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked") // casteo de Particle a AutonomusParticle
    public void simulate(int time) {
        CellIndexMethod cim = new CellIndexMethod(M, L, N, 1, false);

        // cada segundo (intervalo de tiempo) el automata evoluciona a un estado nuevo
        for (int i = 1; i < time + 1; i++) {

            // cargamos las particulas con su estado en el tiempo i
            cim.populate(particles);
            Map<Particle, Set<Particle>> nearby = cim.cellIndexMethod();

            double accVx = 0.0;
            double accVy = 0.0;

            for (AutonomusParticle particle : (List<AutonomusParticle>) cim.getParticles()) {
                updateParticle(particle, nearby.get(particle));
                accVx += particle.getXVelocity();
                accVy += particle.getYVelocity();
            }

            double modSum = Math.sqrt(Math.pow(accVx, 2) + Math.pow(accVy, 2)); // sqrt((vx0 + vx1 + ...)^2 + (vy0 + vy1 + ...)^2) = |sumatoria,i..N (Vi)|
            double velocityAverage = modSum / (N * 0.03); // 0.03 = velocidad

            /*
            TODO: VA nose bien que onda como es esto
             */
//          System.out.println(velocityAverage);

            outputParticles(particles, i);
        }
    }

    private void outputParticles(List<AutonomusParticle> autonomusParticles, int timeInterval) {
        try {
            CSVUtils.CSVWrite(OFF_LATICE_DINAMIC_FILENAME, autonomusParticles, "\n" + Integer.toString(timeInterval) + "\n\n", particle -> particle.dinamicData().append("\n").toString(), true);
        } catch (IOException e) {
            System.err.println("An error has been encountered while writing output file");
            System.exit(1);
        }
    }

    private void updateParticle(AutonomusParticle particle, Set<Particle> nearby) {
    /*
                el automata define como evolucionan las particulas en cada instante
                X(t + 1) = X(t) + V(t) * ∆t
                O(t + 1) = ⟨θ(t)⟩r + ∆θ es el promedio de los ángulos de todas las particulas dentro de rinteraccion incluyendo a la propia particula
                arctg[⟨sin(θ(t))⟩r/⟨cos(θ(t))⟩r]
                Δθ es un ruido uniforme entre [-η/2, η/2].
    */
        double sinSum = nearby.stream().reduce(Math.sin(particle.getRadius()), (aDouble, particle1) -> aDouble + Math.sin(((AutonomusParticle) particle1).getAngle()), Double::sum);
        double cosSum = nearby.stream().reduce(Math.cos(particle.getRadius()), (aDouble, particle1) -> aDouble + Math.cos(((AutonomusParticle) particle1).getAngle()), Double::sum);
        double n = nearby.size() + 1;
        double randomNoise = (Rand.getInstance().nextDouble() * appNoise) - (appNoise / 2); // uniforme(min, max) = random.nextInt(max - min) + min;
        particle.setAngle(Math.atan2(sinSum / n, cosSum / n) + randomNoise);
        particle.move(1);
        particle.move(1);
        double x = particle.getX() + Math.cos(particle.getAngle()) * particle.getVelocity();
        double y = particle.getX() + Math.sin(particle.getAngle()) * particle.getVelocity();
        /*
            Bordes periodicos
         */
        particle.setX(x % L);
        particle.setY(y % L);
    }

    public static void main(String args[]) {
        OffLattice ol = new OffLattice(300, 5, 0.1);

        System.out.println("Generating particles...");
        double start = System.currentTimeMillis();
        List<AutonomusParticle> autonomusParticles = AutonomusParticle.generate(ol.N, ol.L, 0, 2 * Math.PI, 0.03);
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));

         /*
        Todo: nose si esto viene en un archivo o las tengo que generar yo, por ahora las imprimo y dsp voy apendeando en dinamic
         */
        try {
            CSVUtils.CSVWriteParticles(autonomusParticles, OFF_LATICE_STATIC_FILENAME, OFF_LATICE_DINAMIC_FILENAME, autonomusParticles.size(), ol.L);
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
