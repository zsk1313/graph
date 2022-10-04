package org.zsk13.graph.domain.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zsk13
 * @date 10/1/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BFSAttr {
    private String id;
    private Color color;
    private int depth;
    private String prefixId;
}
