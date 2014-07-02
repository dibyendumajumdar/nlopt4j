package org.nlopt4j.optimizer;

import java.util.ArrayList;

public class NLopt {

    public static final int NLOPT_GN_DIRECT = 0;
    public static final int NLOPT_GN_DIRECT_L = 1;
    public static final int NLOPT_GN_DIRECT_L_RAND = 2;
    public static final int NLOPT_GN_DIRECT_NOSCAL = 3;
    public static final int NLOPT_GN_DIRECT_L_NOSCAL = 4;
    public static final int NLOPT_GN_DIRECT_L_RAND_NOSCAL = 5;

    public static final int NLOPT_GN_ORIG_DIRECT = 6;
    public static final int NLOPT_GN_ORIG_DIRECT_L = 7;

    public static final int NLOPT_GD_STOGO = 8;
    public static final int NLOPT_GD_STOGO_RAND = 9;

    public static final int NLOPT_LD_LBFGS_NOCEDAL = 10;

    public static final int NLOPT_LD_LBFGS = 11;

    public static final int NLOPT_LN_PRAXIS = 12;

    public static final int NLOPT_LD_VAR1 = 13;
    public static final int NLOPT_LD_VAR2 = 14;

    public static final int NLOPT_LD_TNEWTON = 15;
    public static final int NLOPT_LD_TNEWTON_RESTART = 16;
    public static final int NLOPT_LD_TNEWTON_PRECOND = 17;
    public static final int NLOPT_LD_TNEWTON_PRECOND_RESTART = 18;

    public static final int NLOPT_GN_CRS2_LM = 19;

    public static final int NLOPT_GN_MLSL = 20;
    public static final int NLOPT_GD_MLSL = 21;
    public static final int NLOPT_GN_MLSL_LDS = 22;
    public static final int NLOPT_GD_MLSL_LDS = 23;

    public static final int NLOPT_LD_MMA = 24;

    public static final int NLOPT_LN_COBYLA = 25;

    public static final int NLOPT_LN_NEWUOA = 26;
    public static final int NLOPT_LN_NEWUOA_BOUND = 27;

    public static final int NLOPT_LN_NELDERMEAD = 28;
    public static final int NLOPT_LN_SBPLX = 29;

    public static final int NLOPT_LN_AUGLAG = 30;
    public static final int NLOPT_LD_AUGLAG = 31;
    public static final int NLOPT_LN_AUGLAG_EQ = 32;
    public static final int NLOPT_LD_AUGLAG_EQ = 33;

    public static final int NLOPT_LN_BOBYQA = 34;

    public static final int NLOPT_GN_ISRES = 35;

    /* new variants that require local_optimizer to be set,
   not with older constants for backwards compatibility */
    public static final int NLOPT_AUGLAG = 36;
    public static final int NLOPT_AUGLAG_EQ = 37;
    public static final int NLOPT_G_MLSL = 38;
    public static final int NLOPT_G_MLSL_LDS = 39;

    public static final int NLOPT_LD_SLSQP = 40;

    public static final int NLOPT_LD_CCSAQ = 41;

    public static final int NLOPT_GN_ESCH = 42;

    public static final int NLOPT_NUM_ALGORITHMS = 43;/* not an algorithm, just the number of them */

    public static final int NLOPT_FAILURE = -1; /* generic failure code */
    public static final int NLOPT_INVALID_ARGS = -2;
    public static final int NLOPT_OUT_OF_MEMORY = -3;
    public static final int NLOPT_ROUNDOFF_LIMITED = -4;
    public static final int NLOPT_FORCED_STOP = -5;
    public static final int NLOPT_SUCCESS = 1; /* generic success code */
    public static final int NLOPT_STOPVAL_REACHED = 2;
    public static final int NLOPT_FTOL_REACHED = 3;
    public static final int NLOPT_XTOL_REACHED = 4;
    public static final int NLOPT_MAXEVAL_REACHED = 5;
    public static final int NLOPT_MAXTIME_REACHED = 6;

    static final class Param {
        final String name;
        final int value;

        Param(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    static final Param[] parameters =
            {
                    new Param("NLOPT_GN_DIRECT", NLOPT_GN_DIRECT),
                    new Param("NLOPT_GN_DIRECT_L", NLOPT_GN_DIRECT_L),
                    new Param("NLOPT_GN_DIRECT_L_RAND", NLOPT_GN_DIRECT_L_RAND),
                    new Param("NLOPT_GN_DIRECT_NOSCAL", NLOPT_GN_DIRECT_NOSCAL),
                    new Param("NLOPT_GN_DIRECT_L_NOSCAL", NLOPT_GN_DIRECT_L_NOSCAL),
                    new Param("NLOPT_GN_DIRECT_L_RAND_NOSCAL", NLOPT_GN_DIRECT_L_RAND_NOSCAL),

                    new Param("NLOPT_GN_ORIG_DIRECT", NLOPT_GN_ORIG_DIRECT),
                    new Param("NLOPT_GN_ORIG_DIRECT_L", NLOPT_GN_ORIG_DIRECT_L),

                    new Param("NLOPT_GD_STOGO", NLOPT_GD_STOGO),
                    new Param("NLOPT_GD_STOGO_RAND", NLOPT_GD_STOGO_RAND),

                    new Param("NLOPT_LD_LBFGS_NOCEDAL", NLOPT_LD_LBFGS_NOCEDAL),

                    new Param("NLOPT_LD_LBFGS", NLOPT_LD_LBFGS),

                    new Param("NLOPT_LN_PRAXIS", NLOPT_LN_PRAXIS),

                    new Param("NLOPT_LD_VAR1", NLOPT_LD_VAR1),
                    new Param("NLOPT_LD_VAR2", NLOPT_LD_VAR2),

                    new Param("NLOPT_LD_TNEWTON", NLOPT_LD_TNEWTON),
                    new Param("NLOPT_LD_TNEWTON_RESTART", NLOPT_LD_TNEWTON_RESTART),
                    new Param("NLOPT_LD_TNEWTON_PRECOND", NLOPT_LD_TNEWTON_PRECOND),
                    new Param("NLOPT_LD_TNEWTON_PRECOND_RESTART", NLOPT_LD_TNEWTON_PRECOND_RESTART),

                    new Param("NLOPT_GN_CRS2_LM", NLOPT_GN_CRS2_LM),

                    new Param("NLOPT_GN_MLSL", NLOPT_GN_MLSL),
                    new Param("NLOPT_GD_MLSL", NLOPT_GD_MLSL),
                    new Param("NLOPT_GN_MLSL_LDS", NLOPT_GN_MLSL_LDS),
                    new Param("NLOPT_GD_MLSL_LDS", NLOPT_GD_MLSL_LDS),

                    new Param("NLOPT_LD_MMA", NLOPT_LD_MMA),

                    new Param("NLOPT_LN_COBYLA", NLOPT_LN_COBYLA),

                    new Param("NLOPT_LN_NEWUOA", NLOPT_LN_NEWUOA),
                    new Param("NLOPT_LN_NEWUOA_BOUND", NLOPT_LN_NEWUOA_BOUND),

                    new Param("NLOPT_LN_NELDERMEAD", NLOPT_LN_NELDERMEAD),
                    new Param("NLOPT_LN_SBPLX", NLOPT_LN_SBPLX),

                    new Param("NLOPT_LN_AUGLAG", NLOPT_LN_AUGLAG),
                    new Param("NLOPT_LD_AUGLAG", NLOPT_LD_AUGLAG),
                    new Param("NLOPT_LN_AUGLAG_EQ", NLOPT_LN_AUGLAG_EQ),
                    new Param("NLOPT_LD_AUGLAG_EQ", NLOPT_LD_AUGLAG_EQ),

                    new Param("NLOPT_LN_BOBYQA", NLOPT_LN_BOBYQA),

                    new Param("NLOPT_GN_ISRES", NLOPT_GN_ISRES),

        /* new variants that require local_optimizer to be set,
       not with older constants for backwards compatibility */
                    new Param("NLOPT_AUGLAG", NLOPT_AUGLAG),
                    new Param("NLOPT_AUGLAG_EQ", NLOPT_AUGLAG_EQ),
                    new Param("NLOPT_G_MLSL", NLOPT_G_MLSL),
                    new Param("NLOPT_G_MLSL_LDS", NLOPT_G_MLSL_LDS),

                    new Param("NLOPT_LD_SLSQP", NLOPT_LD_SLSQP),

                    new Param("NLOPT_LD_CCSAQ", NLOPT_LD_CCSAQ),

                    new Param("NLOPT_GN_ESCH", NLOPT_GN_ESCH),
            };
    static final Param[] errors =

            {
                    new Param("NLOPT_FAILURE", NLOPT_FAILURE), /* generic failure code */
                    new Param("NLOPT_INVALID_ARGS", NLOPT_INVALID_ARGS),
                    new Param("NLOPT_OUT_OF_MEMORY", NLOPT_OUT_OF_MEMORY),
                    new Param("NLOPT_ROUNDOFF_LIMITED", NLOPT_ROUNDOFF_LIMITED),
                    new Param("NLOPT_FORCED_STOP", NLOPT_FORCED_STOP),
            };
    static final Param[] successes =

            {
                    new Param("NLOPT_SUCCESS", NLOPT_SUCCESS), /* generic success code */
                    new Param("NLOPT_STOPVAL_REACHED", NLOPT_STOPVAL_REACHED),
                    new Param("NLOPT_FTOL_REACHED", NLOPT_FTOL_REACHED),
                    new Param("NLOPT_XTOL_REACHED", NLOPT_XTOL_REACHED),
                    new Param("NLOPT_MAXEVAL_REACHED", NLOPT_MAXEVAL_REACHED),
                    new Param("NLOPT_MAXTIME_REACHED", NLOPT_MAXTIME_REACHED),
            };

    /**
     * The interface that must be implemented by functions
     * that are to be minimised
     */
    public static interface NLopt_func {
        double execute(double[] x, double[] gradient);
    }

    /**
     * Returns a newly allocated nlopt_opt handle.
     */
    static native long create(int algorithm, int dimensions);

    /**
     * Deallocates the nlopt_opt handle
     */
    static native void destroy(long handle);

    /**
     * Set objective function
     */
    static native void set_min_objective(long handle, NLopt_func f);

    static native void set_max_objective(long handle, NLopt_func f);

    static native void add_inequality_constraint(long handle, NLopt_func f, double tol);

    static native void add_equality_constraint(long handle, NLopt_func f, double tol);

    /**
     * Set bounds
     */
    static native void set_lower_bounds(long handle, double[] lb);

    static native void set_upper_bounds(long handle, double[] ub);

    /**
     * Stop condition
     */
    static native void set_maxeval(long handle, int maxeval);

    static native void set_maxtime(long handle, double maxtime);

    static native void set_stop_value(long handle, double stop_value);

    /**
     * Run the org.nlopt4j.optimizer
     */
    static native int optimize(long handle, double[] x, double[] result);

    static native void set_local_optimizer(long global_handle, long local_handle);

    static native void set_xtol_rel(long handle, double tol);

    private static native void init();

    static {
        System.loadLibrary("nlopt4j");
        init();
    }

    long handle;

    public NLopt(int algorithm, int dim) {
        handle = create(algorithm, dim);
    }

    public void release() {
        if (handle != 0)
            destroy(handle);
        handle = 0;
    }

    public void setUpperBounds(double[] ub) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_upper_bounds(handle, ub);
    }

    public void setLowerBounds(double[] lb) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_lower_bounds(handle, lb);
    }

    public void setMaxEval(int n) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_maxeval(handle, n);
    }

    public void setMinObjective(NLopt_func func) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_min_objective(handle, func);
    }

    public void setMaxObjective(NLopt_func func) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_max_objective(handle, func);
    }

    public void addInequalityConstraint(NLopt_func func, double tol) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        add_inequality_constraint(handle, func, tol);
    }

    public void addEqualityConstraint(NLopt_func func, double tol) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        add_equality_constraint(handle, func, tol);
    }

    public void setLocalOptimizer(NLopt localOptimizer) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_local_optimizer(handle, localOptimizer.handle);
    }

    public void setStopValue(double v) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_stop_value(handle, v);
    }

    public void setMaxTime(double t) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_maxtime(handle, t);
    }

    public void setRelativeToleranceOnX(double tol) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_xtol_rel(handle, tol);
    }

    public NLoptResult optimize(double[] values) {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        double[] result = new double[1];
        int rc = optimize(handle, values, result);
        if (rc < 0)
            throw new RuntimeException(String.format("Optimization failed with value %d", rc));
        return new NLoptResult(rc, result[0]);
    }

    public static String[] getGlobalAlgorithms() {
        ArrayList<String> values = new ArrayList<String>();
        for (Param p : parameters) {
            if (p.name.contains("_GN_") || p.name.contains("_GD_")) {
                values.add(p.name);
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public static String[] getLocalAlgorithms() {
        ArrayList<String> values = new ArrayList<String>();
        for (Param p : parameters) {
            if (p.name.contains("_LN_") || p.name.contains("_LD_")) {
                values.add(p.name);
            }
        }
        return values.toArray(new String[values.size()]);
    }

    public static int getAlgorithmCode(String name)
    {
        for (Param p: parameters) {
            if (name.equals(p.name))
                return p.value;
        }
        return -1;
    }

    public static String getSuccessDesc(int code)
    {
        for (Param p: successes) {
            if (p.value == code)
                return p.name;
        }
        return "UNKNOWN SUCCESS CODE";
    }

    public static String getErrorDesc(int code)
    {
        for (Param p: errors) {
            if (p.value == code)
                return p.name;
        }
        return "UNEXPECTED ERROR";
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
