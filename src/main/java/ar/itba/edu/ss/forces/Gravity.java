package ar.itba.edu.ss.forces;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.model.HardParticle;

import java.util.Set;

public class Gravity implements Force {

    private final double G = 6.603E-11;

    private Set<HardParticle> planets;

    public Gravity(Set<HardParticle> planets) {
        this.planets = planets;
    }

    @Override
    public double getX(HardParticle planet) {
        double totalFx = 0.0;
        for (HardParticle p : planets) {
            totalFx += getF(planet, p) * getEX(planet, p);
        }
        return totalFx;
    }

    @Override
    public double getY(HardParticle planet) {
        double totalFy = 0.0;
        for (HardParticle p : planets) {
            if (!planet.equals(p)) {
                totalFy += getF(planet, p) * getEY(planet, p);
            }
        }
        return totalFy;
    }

    @Override
    public double getX(double[] r, double[] v, double mass) {
        return 0;
    }

    @Override
    public double getY(double[] r, double[] v, double mass) {
        return 0;
    }

    @Override
    public boolean isVelocityDependant() {
        return true;
    }

    private double getF(HardParticle planetA, HardParticle planetB) {
        return (G * planetA.getMass() * planetB.getMass()) / Math.pow(planetA.distanceTo(planetB), 2);
    }

    private double getEX(HardParticle planetA, HardParticle planetB) {
        return (planetB.getX() - planetA.getX()) / planetA.distanceTo(planetB);
    }

    private double getEY(HardParticle planetA, HardParticle planetB) {
        return (planetB.getX() - planetA.getX()) / planetA.distanceTo(planetB);
    }

}
