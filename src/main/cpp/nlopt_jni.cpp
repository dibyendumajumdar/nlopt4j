
// nlopt4j
// Provides a JNI wrapper for NLopt (http://ab-initio.mit.edu/wiki/index.php/NLopt)
// LICENSE: See LICENSE file

#include <nlopt.h>
#include <nlopt_jni.h>

#include <algorithm>

#include <assert.h>
#include <stdint.h>
#include <stdlib.h>

#ifdef __cpluplus
extern "C" {
#endif

    // Cache Java class and method handles for performance reasons
    static JavaVM *jvm;
    static jclass string_class;
    static jclass nlopt_func_class;
    static jmethodID nlopt_func_execute_method;
    static jclass illegal_argument_exception_class;
    static jclass out_of_memory_class;
    static jclass illegal_state_exception_class;

    // Must have same structure as NLopt_data
    struct Constraint_function {
        jobject func;
        nlopt_opt handle;
        Constraint_function *next;
        Constraint_function *unused;
    };

    struct NLopt_data {
        jobject func;
        nlopt_opt handle;
        Constraint_function *first_constraint;
        Constraint_function *last_constraint;
    };

    // We store a reference to the JVM so that we can use it
    // when invoking the callback
    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
    {
        jvm = vm;
        return JNI_VERSION_1_6;
    }

    // Wrapper for the Java implementation of the NLopt
    // function callback
    double nlopt_minfunc(unsigned n, const double *x,
        double *gradient, /* NULL if not needed */
        void *func_data)
    {
        NLopt_data *data = (NLopt_data *)func_data;
        // Get JNI environment
        JNIEnv *jni = NULL;
        if (jvm->GetEnv(reinterpret_cast<void **>(&jni), JNI_VERSION_1_6) != JNI_OK) {
            fprintf(stderr, "Error: Unable to obtain JNI environment\n");
            nlopt_force_stop(data->handle);
            return 0.0; // ignored
        }

        if (data->func) {
            jdoubleArray xarray = NULL;
            jdoubleArray gradientarray = NULL;

            // Create Java arrays
            xarray = jni->NewDoubleArray(n);
            if (!xarray || jni->ExceptionCheck()) {
                nlopt_force_stop(data->handle);
                return 0.0; // ignored
            }
            unsigned gsize = gradient ? n : 0;
            gradientarray = jni->NewDoubleArray(gsize);
            if (!gradientarray || jni->ExceptionCheck()) {
                // avoid holding on to memory - see issue #1
                jni->DeleteLocalRef(xarray);
                nlopt_force_stop(data->handle);
                return 0.0; // ignored
            }
            // Copy data into Java arrays
            jni->SetDoubleArrayRegion(xarray, 0, n, x);
            if (gradient) {
                jni->SetDoubleArrayRegion(gradientarray, 0, gsize, gradient);
            }
            // Call Java function
            jdouble result = jni->CallDoubleMethod(data->func, nlopt_func_execute_method, xarray, gradientarray);
            if (jni->ExceptionCheck()) {
                // avoid holding on to memory - see issue #1
                jni->DeleteLocalRef(xarray);
                jni->DeleteLocalRef(gradientarray);
                nlopt_force_stop(data->handle);
                return 0.0; // ignored
            }
            if (gradient) {
                // We need to copy data back to C++ land
                jboolean is_copy = false;
                double *xcopy = jni->GetDoubleArrayElements(gradientarray, &is_copy);
                if (!xcopy || jni->ExceptionCheck()) {
                    // avoid holding on to memory - see issue #1
                    jni->DeleteLocalRef(xarray);
                    jni->DeleteLocalRef(gradientarray);
                    nlopt_force_stop(data->handle);
                    return 0.0; // ignored
                }
                // Copy data
                std::copy(xcopy, xcopy+gsize, gradient);
                // Release the managed copy
                jni->ReleaseDoubleArrayElements(gradientarray, xcopy, 0);
            }
            // avoid holding on to memory - see issue #1
            jni->DeleteLocalRef(xarray);
            jni->DeleteLocalRef(gradientarray);
            return result;
        }
        jni->ThrowNew(illegal_argument_exception_class, "No function set");
        nlopt_force_stop(data->handle);
        return 0.0; // ignored
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_init(JNIEnv *jni, jclass myclass)
    {
        // We cache references to objects and fields we need to reference 
        jclass localclass = jni->FindClass("java/lang/String");
        if (jni->ExceptionCheck()) return;
        string_class = (jclass)(jni->NewGlobalRef(localclass));
        jni->DeleteLocalRef(localclass);

        localclass = jni->FindClass("org/nlopt4j/optimizer/NLopt$NLopt_func");
        if (jni->ExceptionCheck()) return;
        nlopt_func_class = (jclass)(jni->NewGlobalRef(localclass));
        jni->DeleteLocalRef(localclass);

        localclass = jni->FindClass("java/lang/IllegalArgumentException");
        if (jni->ExceptionCheck()) return;
        illegal_argument_exception_class = (jclass)(jni->NewGlobalRef(localclass));
        jni->DeleteLocalRef(localclass);

        localclass = jni->FindClass("java/lang/IllegalStateException");
        if (jni->ExceptionCheck()) return;
        illegal_state_exception_class = (jclass)(jni->NewGlobalRef(localclass));
        jni->DeleteLocalRef(localclass);

        localclass = jni->FindClass("java/lang/OutOfMemoryError");
        if (jni->ExceptionCheck()) return;
        out_of_memory_class = (jclass)(jni->NewGlobalRef(localclass));
        jni->DeleteLocalRef(localclass);

        nlopt_func_execute_method = jni->GetMethodID(nlopt_func_class, "execute", "([D[D)D");
        if (!nlopt_func_execute_method || jni->ExceptionCheck()) return;
    }

    JNIEXPORT jlong JNICALL Java_org_nlopt4j_optimizer_NLopt_create(JNIEnv *jni, jclass self, jint algo, jint dimensions)
    {
        if (algo < 0 || algo >= NLOPT_NUM_ALGORITHMS) {
            jni->ThrowNew(illegal_argument_exception_class, "Unknown NLopt.algorithm");
            return 0;
        }
        if (dimensions < 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Dimensions cannot be <= 0");
            return 0;
        }
        nlopt_opt h = nlopt_create((nlopt_algorithm)algo, dimensions);
        if (h == NULL) {
            jni->ThrowNew(out_of_memory_class, "NLopt failed to initialize: not enough memory");
            return 0;
        }
        NLopt_data *data = (NLopt_data *) malloc(sizeof(NLopt_data));
        data->handle = h;
        data->func = 0;
        data->first_constraint = NULL;
        data->last_constraint = NULL;

        // As we have no way to return a pointer to Java
        // We have to convert the pointer to a long (64 bit) value
        jlong ret = (jlong)data;

        //fprintf(stderr, "Created nlopt_opt pointer %p\n", data);
        assert(((void *)ret) == ((void *)data));

        return ret;
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_destroy(JNIEnv *jni, jclass self, jlong handle)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        //fprintf(stderr, "Destroying nlopt_opt object at %p\n", data);
        nlopt_opt h = data->handle;
        if (data->func) {
            jni->DeleteGlobalRef(data->func);
        }
        while (data->first_constraint) {
            Constraint_function *tmp = data->first_constraint;
            data->first_constraint = tmp->next;
            if (tmp->func) {
                jni->DeleteGlobalRef(tmp->func);
            }
            free(tmp);
        }
        nlopt_destroy(h);
        free(data);
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1min_1objective
        (JNIEnv *jni, jclass myclass, jlong handle, jobject func)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        if (data->func) {
            jni->ThrowNew(illegal_argument_exception_class, "Function already set");
            return;
        }
        data->func = jni->NewGlobalRef(func);
        nlopt_set_min_objective(data->handle, nlopt_minfunc, data);
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1max_1objective
        (JNIEnv *jni, jclass myclass, jlong handle, jobject func)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        if (data->func) {
            jni->ThrowNew(illegal_argument_exception_class, "Function already set");
            return;
        }
        data->func = jni->NewGlobalRef(func);
        nlopt_set_max_objective(data->handle, nlopt_minfunc, data);
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1stop_1value
        (JNIEnv *jni, jclass cls, jlong handle, jdouble value)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        nlopt_set_stopval(data->handle, value);
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1lower_1bounds
        (JNIEnv *jni, jclass cls, jlong handle, jdoubleArray lb)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        jsize len = jni->GetArrayLength(lb);
        if (len != nlopt_get_dimension(data->handle)) {
            jni->ThrowNew(illegal_argument_exception_class, "Array length of bounds does not match dimensions");
            return;
        }
        jboolean is_copy = true;
        jdouble *bounds = jni->GetDoubleArrayElements(lb, &is_copy);
        if (!bounds || jni->ExceptionCheck()) {
            return;
        }
        nlopt_set_lower_bounds(data->handle, bounds);
        jni->ReleaseDoubleArrayElements(lb, bounds, 0);
    }

    /*
    * Class:     optimizer_NLopt
    * Method:    set_upper_bounds
    * Signature: (J[D)V
    */
    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1upper_1bounds
        (JNIEnv *jni, jclass cls, jlong handle, jdoubleArray ub)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        jsize len = jni->GetArrayLength(ub);
        if (len != nlopt_get_dimension(data->handle)) {
            jni->ThrowNew(illegal_argument_exception_class, "Array length of bounds does not match dimensions");
            return;
        }
        jboolean is_copy = true;
        jdouble *bounds = jni->GetDoubleArrayElements(ub, &is_copy);
        if (!bounds || jni->ExceptionCheck()) {
            return;
        }
        nlopt_set_upper_bounds(data->handle, bounds);
        jni->ReleaseDoubleArrayElements(ub, bounds, 0);
    }

    /*
    * Class:     optimizer_NLopt
    * Method:    set_maxeval
    * Signature: (JI)V
    */
    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1maxeval
        (JNIEnv *jni, jclass cls, jlong handle, jint value)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        nlopt_set_maxeval(data->handle, value);
    }

    /*
    * Class:     optimizer_NLopt
    * Method:    set_maxtime
    * Signature: (JD)V
    */
    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1maxtime
        (JNIEnv *jni, jclass cls, jlong handle, jdouble value)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        nlopt_set_maxtime(data->handle, value);
    }

    JNIEXPORT jint JNICALL Java_org_nlopt4j_optimizer_NLopt_optimize
        (JNIEnv *jni, jclass cls, jlong handle, jdoubleArray values, jdoubleArray result)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return -10;
        }
        NLopt_data *data = (NLopt_data *)handle;
        jsize len = jni->GetArrayLength(values);
        jsize resultlen = jni->GetArrayLength(result);
        if (len != nlopt_get_dimension(data->handle)) {
            jni->ThrowNew(illegal_argument_exception_class, "Array length of values does not match dimensions");
            return -10;
        }
        if (resultlen < 1) {
            jni->ThrowNew(illegal_argument_exception_class, "Result array must be at least 1 element long");
            return -10;
        }
        jboolean is_copy = false;
        jdouble *c_values = jni->GetDoubleArrayElements(values, &is_copy);
        if (!c_values || jni->ExceptionCheck()) {
            return -10;
        }
        jdouble *c_result = jni->GetDoubleArrayElements(result, &is_copy);
        if (jni->ExceptionCheck()) {
            jni->ReleaseDoubleArrayElements(values, c_values, 0);
            return -10;
        }

        int rc = nlopt_optimize(data->handle, c_values, c_result);
        jni->ReleaseDoubleArrayElements(values, c_values, 0);
        jni->ReleaseDoubleArrayElements(result, c_result, 0);

        return rc;
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1local_1optimizer
        (JNIEnv *jni, jclass cls, jlong handle, jlong local_handle)
    {
        if (handle == 0 || local_handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *global_nlopt = (NLopt_data *)handle;
        NLopt_data *local_nlopt = (NLopt_data *)local_handle;

        nlopt_set_local_optimizer(global_nlopt->handle, local_nlopt->handle);
    }

    static void add_constraint(NLopt_data *data, Constraint_function *constraint)
    {
        constraint->next = NULL;
        if (data->last_constraint)
            data->last_constraint->next = constraint;
        else
            data->last_constraint = constraint;
        if (!data->first_constraint)
            data->first_constraint = constraint;
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_add_1inequality_1constraint
        (JNIEnv *jni, jclass cls, jlong handle, jobject func, jdouble tol)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        Constraint_function *constraint = (Constraint_function *)malloc(sizeof(Constraint_function));
        if (constraint == NULL) {
            jni->ThrowNew(out_of_memory_class, "Cannot allocate a constraint function");
            return;
        }
        constraint->handle = data->handle;
        constraint->next = NULL;
        constraint->unused = NULL;
        constraint->func = jni->NewGlobalRef(func);
        add_constraint(data, constraint);
        nlopt_add_inequality_constraint(data->handle, nlopt_minfunc, constraint, tol);
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_add_1equality_1constraint
        (JNIEnv *jni, jclass cls, jlong handle, jobject func, jdouble tol)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        Constraint_function *constraint = (Constraint_function *)malloc(sizeof(Constraint_function));
        if (constraint == NULL) {
            jni->ThrowNew(out_of_memory_class, "Cannot allocate a constraint function");
            return;
        }
        constraint->handle = data->handle;
        constraint->next = NULL;
        constraint->unused = NULL;
        constraint->func = jni->NewGlobalRef(func);
        add_constraint(data, constraint);
        nlopt_add_equality_constraint(data->handle, nlopt_minfunc, constraint, tol);
    }

    JNIEXPORT void JNICALL Java_org_nlopt4j_optimizer_NLopt_set_1xtol_1rel
        (JNIEnv *jni, jclass cls, jlong handle, jdouble tol)
    {
        if (handle == 0) {
            jni->ThrowNew(illegal_argument_exception_class, "Null handle");
            return;
        }
        NLopt_data *data = (NLopt_data *)handle;
        nlopt_set_xtol_rel(data->handle, tol);
    }

#ifdef __cpluplus
}
#endif
