package org.zsk13.graph.domain.po;

import lombok.*;

/**
 * @author zsk13
 * @date 10/4/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class PrimAttr {
    @EqualsAndHashCode.Include
    private String id;
    private int key;
    private String prefixId;
}
