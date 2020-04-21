package ar.itba.edu.ss.systems;

import ar.itba.edu.ss.interfaces.Integration;
import ar.itba.edu.ss.model.HardParticle;
import ar.itba.edu.ss.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class SpringOscillator {

    /*
    simulacion de una particula
        m = 70 kg; k = 104 N/m; 𝛾 = 100 kg/s; tf = 5 s
        r (t=0) = 1 m;
        v (t=0) = - A 𝛾/(2m) m/s;
     */
    private final double FPS = 10.0; // frames por segundo a usar par guardar el estado
    private int k = 10 ^ 4; // constante del resorte
    private int gamma = 100; // Kg/s
    private double dt; // differencial de tiempo
    private double mass = 70; // Kgh
    private double r = 1; // m
    private double amplitud = r; // m

    private HardParticle particle = (HardParticle) new HardParticle.Builder()
            .withMass(mass)
            .withRandomVelocity((-amplitud * gamma) / (2 * mass))
            .withAngle(0) // solo eje x
            .withCoordinates(1, 0).build(); // solo eje x


    public double analiticalSolution(double time) {
        return amplitud * Math.exp((-gamma / (2 * mass)) * time) * Math.cos(Math.sqrt((k / mass) - (Math.pow(gamma, 2) / 4 * Math.pow(mass, 2))) * time);
    }

    public void simulate(double simulationTime, Integration integrationMethod) {
        double time = 0;
        while (time < simulationTime) {
            if ((time / FPS) - Math.round(time / FPS) <= 0) {
                ovitoOutputParticle((int) Math.round(time / FPS));
            }
            integrationMethod.calculate(particle, dt);
            time += dt;
        }
    }

    private void ovitoOutputParticle(int frame) {
        List<HardParticle> frameParticles = new ArrayList<>();
        frameParticles.add(particle);
        IOUtils.ovitoOutputParticles("spring.xyz",
                frameParticles,
                frame,
                true);
    }
}
