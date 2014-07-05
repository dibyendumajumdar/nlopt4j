nlopt4j - NLopt for Java
========================

JNI wrapper for NLopt (http://ab-initio.mit.edu/wiki/index.php/NLopt).

Example
=======

The following example is a port of the sample used in NLopt tutorial.

```Java
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
```

Build Instructions
==================

Currently build has been tested on Windows and Mac OS X (Snow Leopard).

32-bit Windows
--------------
Requires following:
+ Visual C++ 2013 (should work with older versions but not tested)
+ CMake 2.8.12 or above.
+ Java 1.6 or above.
+ Maven 2.1 or above.

Currently the NLopt libraries and headers must be installed to c:/nlopt.
The NLopt include files must be under c:/nlopt/include.
The NLopt library files must be under c:/nlopt/lib.

To build follow these steps from a VS 2013 command shell:
```
cmake -G "Visual Studio 12"
```
Above creates VS solution files which can be opened in the IDE. Build the
solution inside the IDE.

To build and test the Java bits, run following from the command shell.
```
mvn test
```

Note that on Windows the required DLLs must be on the PATH.

Mac OS X
--------
Requires following:
+ CMake 2.8.12 or above.
+ GCC 4.2 or above.
+ Java 1.6 or above.
+ Maven 2.1 or above.

Currently the NLopt libraries and headers must be installed to ~/nlopt.
The NLopt include files must be under ~/nlopt/include.
The NLopt library files must be under ~/nlopt/lib.

To build follow these steps:
```
cmake -G "Unix Makefiles"
make
mvn test
```

Reference
=========

The main Java class is `org.nlopt4j.optimizer.NLopt`.

An instance of `NLopt` encapsulates the `NLopt` handle of the native library.

The NLopt constructor is defined as:
```Java
NLopt(int algorithm, int dim)
```
The `algorithm` is an integer constants such as `NLopt.NLOPT_GN_DIRECT`.
The `dim` argument specifies the number of variables in the problem.

The NLopt instance can be destroyed by calling:
```Java
void release()
```
This destroys the underlying native object. I recommend calling this in the
finally block. Note that `release()` will be invoked by the finalizer as well,
calling `release()` multiple times has no effect.


