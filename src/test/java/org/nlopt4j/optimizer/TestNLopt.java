package org.nlopt4j.optimizer;

import org.nlopt4j.optimizer.NLopt;
import org.nlopt4j.optimizer.NLoptResult;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Dibyendu Majumdar on 22/06/2014.
 */

public class TestNLopt {

    static void dump(double[] arry, String label)
    {
        for (int i = 0; i < arry.length; i++) {
            System.err.println(label + "[" + i + "] = " + arry[i]);
        }
    }

    static final class Constraint implements NLopt.NLopt_func {
        double a, b;
        Constraint(double a, double b) {
            this.a = a;
            this.b = b;
        }
        @Override
        public double execute(double[] x, double[] gradient) {
            double a = this.a, b = this.b;
            if (gradient.length == x.length) {
                gradient[0] = 3.0 * a * (a*x[0] + b) * (a*x[0] + b);
                gradient[1] = -1.0;
            }
            return ((a*x[0] + b) * (a*x[0] + b) * (a*x[0] + b) - x[1]);
        }
    }

    @Test
    public void testTutorialSample() {
        NLopt.NLopt_func func = new NLopt.NLopt_func() {
            @Override
            public double execute(double[] x, double[] gradient) {
                if (gradient.length == x.length) {
                    gradient[0] = 0.0;
                    gradient[1] = 0.5 / Math.sqrt(x[1]);
                }
                return Math.sqrt(x[1]);
            }
        };

        double[] lb = {Double.NEGATIVE_INFINITY, 0}; /* lower bounds */
        NLopt optimizer = new NLopt(NLopt.NLOPT_LD_MMA, 2);
        optimizer.setLowerBounds(lb);
        optimizer.setMinObjective(func);
        optimizer.addInequalityConstraint(new Constraint(2.0, 0.0), 1e-8);
        optimizer.addInequalityConstraint(new Constraint(-1.0, 1.0), 1e-8);
        optimizer.setRelativeToleranceOnX(1e-4);
        double[] x = {1.234, 5.678};  /* some initial guess */
        NLoptResult minf = optimizer.optimize(x);
        System.out.println(String.format("found minimum at f(%f,%f) = %f\n", x[0], x[1], minf.minValue()));
        assertEquals(0.544331, minf.minValue(), 1e-4);
    }
}
