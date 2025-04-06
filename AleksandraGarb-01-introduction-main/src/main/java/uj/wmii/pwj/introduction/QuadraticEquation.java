package uj.wmii.pwj.introduction;

public class QuadraticEquation {

    public double[] findRoots(double a, double b, double c) {
        double delta = b * b - 4.0 * a * c;

        if (delta < 0) {
            return new double[0];
        } else if (delta > 0) {
            double[] tab = new double[2];
            tab[0] = (-b + Math.sqrt(delta)) / (2.0 * a);
            tab[1] = (-b - Math.sqrt(delta)) / (2.0 * a);
            return tab;
        } else {
            double[] tab = new double[1];
            tab[0] = (-b) / (2.0 * a);
            return tab;
        }
    }

}

