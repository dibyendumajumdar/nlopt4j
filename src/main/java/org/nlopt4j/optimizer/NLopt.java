package org.nlopt4j.optimizer;

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

    public void setUpperBounds(double[] ub)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_upper_bounds(handle, ub);
    }

    public void setLowerBounds(double[] lb)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_lower_bounds(handle, lb);
    }

    public void setMaxEval(int n)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_maxeval(handle, n);
    }

    public void setMinObjective(NLopt_func func)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_min_objective(handle, func);
    }

    public void setMaxObjective(NLopt_func func)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_max_objective(handle, func);
    }

    public void addInequalityConstraint(NLopt_func func, double tol)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        add_inequality_constraint(handle, func, tol);
    }

    public void addEqualityConstraint(NLopt_func func, double tol)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        add_equality_constraint(handle, func, tol);
    }

    public void setLocalOptimizer(NLopt localOptimizer)
    {
        set_local_optimizer(handle, localOptimizer.handle);
    }

    public void setStopValue(double v)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_stop_value(handle, v);
    }

    public void setMaxTime(double t)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_maxtime(handle, t);
    }

    public void setRelativeToleranceOnX(double tol)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        set_xtol_rel(handle, tol);
    }

    public NLoptResult optimize(double[] values)
    {
        if (handle == 0)
            throw new IllegalStateException("Not initialized");
        double[] result = new double[1];
        int rc = optimize(handle, values, result);
        if (rc < 0)
            throw new RuntimeException(String.format("Optimization failed with value %d", rc));
        return new NLoptResult(rc, result[0]);
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }
}
