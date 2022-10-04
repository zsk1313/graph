package org.zsk13.graph.domain.po;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zsk13
 * @date 10/3/22
 */
public class DisjointSet<E> {
    private Map<E, Set<E>> data;

    DisjointSet() {
        this.data = new HashMap<>();
    }

    public void makeSet(E e) {
        if (!this.data.containsKey(e)) {
            Set<E> val = new HashSet<>();
            val.add(e);
            this.data.put(e, val);
        }
    }

    public Set<E> findSet(E e) {
        return this.data.get(e);
    }

    public void union(E u, E v) {
        if (this.data.containsKey(u) && this.data.containsKey(v)) {
            Set<E> set1 = this.data.get(u);
            Set<E> set2 = this.data.get(v);
            set1.addAll(set2);
            set1.forEach(e -> this.data.put(e, set1));
        }
    }

    public boolean sameComponent(E u, E v) {
        Set<E> su = this.data.get(u);
        Set<E> sv = this.data.get(v);
        if (su == sv) {
            return true;
        } else {
            if (su.size() != sv.size()) {
                return false;
            }
            AtomicBoolean same = new AtomicBoolean(true);
            su.forEach(e -> {
                if (!sv.contains(e)) {
                    same.set(false);
                }
            });
            return same.get();
        }
    }
}
