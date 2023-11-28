package com.ebremer.halcyon.hilbert;

import java.util.Collections;
import java.util.LinkedList;
import org.davidmoten.hilbert.Range;
import org.davidmoten.hilbert.Ranges;

/**
 *
 * @author erich
 */
public class hTools {
    
    public static Ranges Tran(LinkedList<Range> src) {
        LinkedList<eRange> tmp = new LinkedList<>();
        for (Range r : src) {
            //System.out.println("SEQ Range : "+r.low()+"-->"+r.high());
            tmp.add(new eRange(r.low(),r.high()));
        }
        Collections.sort(tmp);
        Ranges rr = new Ranges(tmp.size());
        for (eRange ee : tmp) {
            //System.out.println("Add Range : "+ee.low()+"-->"+ee.high());
            rr.add(ee.low(),ee.high());
        }
        return rr;
    }
}
