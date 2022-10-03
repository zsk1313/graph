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
public class Node {
    @EqualsAndHashCode.Include
    private String id;

    public static Node deepCopy(Node src){
        return new Node(src.id);
    }
}
