package ar.itba.edu.ss.forces;

import ar.itba.edu.ss.interfaces.Force;
import ar.itba.edu.ss.model.HardParticle;

import java.awt.*;
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
            totalFy += getF(planet, p) * getEY(planet, p);
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
        return (G * planetA.getMass() * planetB.getMass()) / Math.pow(getDistance(planetA, planetB), 2);
    }

    private double getEX(HardParticle planetA, HardParticle planetB) {
        return (planetB.getX() - planetA.getX()) / getDistance(planetA, planetB);
    }

    private double getEY(HardParticle planetA, HardParticle planetB) {
        return (planetB.getY() - planetA.getY()) / getDistance(planetA, planetB);
    }

    private double getDistance(HardParticle planetA, HardParticle planetB) {
        return Point.distance(planetA.getX(), planetA.getY(), planetB.getX(), planetB.getY());
    }

}
