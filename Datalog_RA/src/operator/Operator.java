package operator;

import dataStructures.Tuple;

public abstract class Operator {

    public int i, c;

    abstract public Tuple next();

    abstract public void reset();

    abstract public Operator instance();

}
