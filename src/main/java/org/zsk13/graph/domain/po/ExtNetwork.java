package org.zsk13.graph.domain.po;

import com.google.common.graph.EndpointPair;

/**
 * @author zsk13
 * @date 10/1/22
 */
public interface ExtNetwork<N, E> {
    boolean addNode(N var1);

    boolean addEdge(N var1, N var2, E var3);

    boolean addEdge(EndpointPair<N> var1, E var2);

    boolean removeNode(N var1);

    boolean removeEdge(E var1);
}
