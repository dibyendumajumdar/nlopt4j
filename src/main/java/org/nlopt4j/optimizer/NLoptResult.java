package org.nlopt4j.optimizer;

public final class NLoptResult {
    final int nlopt_result;
    final double min_value;

    public NLoptResult(int nlopt_result, double min_value) {
        this.nlopt_result = nlopt_result;
        this.min_value = min_value;
    }
    public final int result() { return nlopt_result; }
    public final double minValue() { return min_value; }
}
