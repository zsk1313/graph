package org.zsk13.graph.domain.po;

import lombok.*;

/**
 * @author zsk13
 * @date 10/1/22
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
public class WeightEdge extends Edge {
    private int weight;

    public WeightEdge(String id, int weight) {
        super(id);
        this.weight = weight;
    }
}
