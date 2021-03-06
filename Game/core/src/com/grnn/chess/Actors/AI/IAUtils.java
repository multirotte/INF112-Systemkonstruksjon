package com.grnn.chess.Actors.AI;

import com.grnn.chess.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class IAUtils {

    private IAUtils() {}

    public static <M extends Move> List<M> iterableToSortedList(Iterable<M> iterable) {
        if (iterable instanceof Collection) {
            return new ArrayList<M>((Collection<M>) iterable);
        }
        List<M> list = new ArrayList<M>();
        for (M m : iterable) {
            list.add(m);
        }
        return list;
    }

}