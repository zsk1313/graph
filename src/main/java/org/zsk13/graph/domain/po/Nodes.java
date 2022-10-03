package org.zsk13.graph.domain.po;

import lombok.*;

import java.util.HashSet;

/**
 * @author zsk13
 * @date 10/2/22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = true)
@ToString
public class Nodes extends Node{
    private HashSet<String> ids;
}
