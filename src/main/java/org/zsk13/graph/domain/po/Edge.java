package org.zsk13.graph.domain.po;

import lombok.*;

/**
 * @author zsk13
 * @date 10/1/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Edge {
    @EqualsAndHashCode.Include
    private String id;

    public static Edge deepCopy(Edge src){
        return new Edge(src.id);
    }
}
