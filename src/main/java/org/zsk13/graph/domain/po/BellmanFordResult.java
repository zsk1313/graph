package org.zsk13.graph.domain.po;

import lombok.*;

import java.util.Map;

/**
 * @author zsk13
 * @date 10/5/22
 */
@Data
@AllArgsConstructor
@ToString
public class BellmanFordResult {
    private Map<String, SingleSourceShortestPathAttr> singleSourceShortestPathAttrMap;
    private boolean existNegativeCyclePath;
}
