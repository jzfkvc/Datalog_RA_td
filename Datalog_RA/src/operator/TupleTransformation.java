package operator;

import dataStructures.Tuple;

@FunctionalInterface
public interface TupleTransformation {

    public Tuple transform(Tuple t1, Tuple t2);

}
