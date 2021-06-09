package dataStructures;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import operator.Operator;

public class Relation {

    private ArrayList<Tuple> arr;

    public Relation(String[][] rows) {
        arr = new ArrayList<>();
        for (String[] row : rows) {
            Tuple tuple = new Tuple();
            for (String val : row) tuple.add(val);
            arr.add(tuple);
        }
    }
    
    public Relation(List<List<String>> rows){
        arr = new ArrayList<>();
        for (List<String> row : rows) {
            Tuple tuple = new Tuple();
            for (String val : row) tuple.add(val);
            arr.add(tuple);
        }
    }

    public Iterator<Tuple> iterator() {
        return arr.iterator();
    }

    public Operator operator() {
        return new RelationOperator(this);
    }

    private class RelationOperator extends Operator {

        private Iterator<Tuple> it;
        private final Relation relation;

        public RelationOperator(Relation relation) {
            this.relation = relation;
            it = relation.iterator();
        }

        @Override
        public Tuple next() {
            return it.hasNext() ? it.next() : null;
        }

        @Override
        public void reset() {
            it = relation.iterator();
        }

        @Override
        public Operator instance() {
            return new RelationOperator(relation);
        }

    }
}
