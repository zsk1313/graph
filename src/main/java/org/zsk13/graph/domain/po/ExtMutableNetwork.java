package org.zsk13.graph.domain.po;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zsk13
 * @date 10/1/22
 */
@Data
@Slf4j
public class ExtMutableNetwork<N extends Node, E extends Edge> implements ExtNetwork<N, E> {
    private MutableNetwork<N, E> network;
    private Map<String, N> allNodeMap;
    private Cache<String, Object> cache = CacheBuilder.newBuilder().maximumSize(2).build();
    private static final String CACHE_PREFIX_BFS = "bfs_";
    private static final String CACHE_PREFIX_DFS = "dfs_";


    public ExtMutableNetwork() {
        this.network = NetworkBuilder.undirected().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).build();
        this.allNodeMap = new HashMap<>();
    }

    public ExtMutableNetwork(MutableNetwork<N, E> network) {
        this.network = network;
        this.allNodeMap = this.network.nodes().stream()
                .collect(HashMap::new, (hashMap, n) -> hashMap.put(n.getId(), n), HashMap::putAll);
    }

    @SuppressWarnings("unchecked")
    public Map<String, BFSAttr> bfs(String begin) {
        try {
            return (Map<String, BFSAttr>) cache.get(CACHE_PREFIX_BFS + begin, () -> {
                Map<String, BFSAttr> bfsAttrMap = this.network.nodes().stream()
                        .collect(HashMap::new, (hashMap, node) -> hashMap.put(node.getId(), new BFSAttr(node.getId(), Color.WHITE, Integer.MAX_VALUE, null)), HashMap::putAll);
                Node s = getNodeById(begin);
                BFSAttr sBfsAttr = bfsAttrMap.get(s.getId());
                sBfsAttr.setColor(Color.GRAY);
                sBfsAttr.setDepth(0);
                sBfsAttr.setPrefix(null);
                LinkedList<Node> q = new LinkedList<>();
                q.addFirst(s);
                while (!q.isEmpty()) {
                    Node u = q.removeFirst();
                    BFSAttr uBfsAttr = bfsAttrMap.get(u.getId());
                    this.network.adjacentNodes((N) u).forEach(v -> {
                        BFSAttr vBfsAttr = bfsAttrMap.get(v.getId());
                        if (vBfsAttr.getColor().equals(Color.WHITE)) {
                            vBfsAttr.setColor(Color.GRAY);
                            vBfsAttr.setDepth(uBfsAttr.getDepth() + 1);
                            vBfsAttr.setPrefix(u);
                            q.addLast(v);
                        }
                    });
                    uBfsAttr.setColor(Color.BLACK);
                }
                return bfsAttrMap;
            });
        } catch (ExecutionException e) {
            log.error("{0}", e);
            return null;
        }
    }

    public LinkedList<String> printPath(String s, String v) {
        Map<String, BFSAttr> bfsAttrMap = bfs(s);
        LinkedList<String> path = new LinkedList<>();
        printPath(bfsAttrMap, path, s, v);
        return path;
    }

    private void printPath(Map<String, BFSAttr> bfsAttrMap, LinkedList<String> path, String s, String v) {
        if (Objects.equals(s, v)) {
            path.addLast(s);
        } else if (Objects.isNull(bfsAttrMap.get(v).getPrefix())) {
            log.error("no path from {} to {} exists", s, v);
        } else {
            printPath(bfsAttrMap, path, s, bfsAttrMap.get(v).getPrefix().getId());
            path.addLast(v);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, DFSAttr> dfs() {
        try {
            return (Map<String, DFSAttr>) cache.get(CACHE_PREFIX_DFS, () -> dfs(this.network));
        } catch (ExecutionException e) {
            log.error("{0}", e);
            return null;
        }
    }

    public Map<String, DFSAttr> dfs(MutableNetwork<N, E> network) {
        Map<String, DFSAttr> dfsAttrMap = network.nodes().stream()
                .collect(HashMap::new, (hashMap, node) -> hashMap.put(node.getId(), new DFSAttr(node.getId(), Color.WHITE, Integer.MAX_VALUE, Integer.MAX_VALUE, null)), HashMap::putAll);
        AtomicInteger time = new AtomicInteger(0);
        network.nodes().stream().sequential().forEach(u -> {
            if (Objects.equals(dfsAttrMap.get(u.getId()).getColor(), Color.WHITE)) {
                dfsVisit(network, dfsAttrMap, time, u);
            }
        });
        return dfsAttrMap;
    }

    private void dfsVisit(MutableNetwork<N, E> network, Map<String, DFSAttr> dfsAttrMap, AtomicInteger time, N u) {
        time.incrementAndGet();
        DFSAttr dfsAttr = dfsAttrMap.get(u.getId());
        dfsAttr.setDepth(time.get());
        dfsAttr.setColor(Color.GRAY);
        network.outEdges(u).stream().sequential().forEach(e -> {
            EndpointPair<N> ep = network.incidentNodes(e);
            N v = ep.target();
            DFSAttr vDfsAttr = dfsAttrMap.get(v.getId());
            if (Objects.equals(vDfsAttr.getColor(), Color.WHITE)) {
                vDfsAttr.setPrefix(u);
                dfsVisit(network, dfsAttrMap, time, v);
            }
        });
        dfsAttr.setColor(Color.BLACK);
        time.incrementAndGet();
        dfsAttr.setFinish(time.get());
    }

    public List<DFSAttr> topologicalSort(Map<String, DFSAttr> dfsAttrMap) {
        List<DFSAttr> dfsAttrs = dfsAttrMap.values().stream().sorted(Comparator.comparingInt(DFSAttr::getFinish)).collect(Collectors.toList());
        Collections.reverse(dfsAttrs);
        return dfsAttrs;
    }

    public <NS extends Nodes> MutableNetwork<NS, E> stronglyConnectedComponents() {
        MutableNetwork<NS, E> stronglyConnectedComponentsNetwork = NetworkBuilder.directed().allowsParallelEdges(true).nodeOrder(ElementOrder.insertion()).build();
        Map<String, DFSAttr> dfs = dfs();
        MutableNetwork<N, E> gt = computeGraphT(this.network);
        List<String> ids = topologicalSort(dfs).stream().map(DFSAttr::getId).collect(Collectors.toList());
        Map<String, DFSAttr> gtDfs = dfs(gt, ids);
        List<DFSAttr> dfsAttrs = new ArrayList<>(gtDfs.values());
        dfsAttrs.sort(Comparator.comparingInt(DFSAttr::getDepth));
        mergeNode(stronglyConnectedComponentsNetwork, dfsAttrs);
        mergeEdge(stronglyConnectedComponentsNetwork);
        return stronglyConnectedComponentsNetwork;
    }

    private <NS extends Nodes> void mergeEdge(MutableNetwork<NS, E> stronglyConnectedComponentsNetwork) {
        List<NS> nodesList = new ArrayList<>(stronglyConnectedComponentsNetwork.nodes());
        nodesList.stream().forEach(sourceNodes -> {
            final HashSet<String> sourceIds = sourceNodes.getIds();
            sourceIds.stream().forEach(sourceId -> {
                Node node = this.getNodeById(sourceId);
                this.network.outEdges((N) node).stream()
                        .filter(e -> !sourceIds.contains(this.network.incidentNodes(e).target().getId()))
                        .forEach(e -> {
                            String targetId = this.network.incidentNodes(e).target().getId();
                            NS targetNodes = nodesList.stream()
                                    .filter(curTargetNodes -> curTargetNodes.getIds().contains(targetId))
                                    .collect(Collectors.toList())
                                    .get(0);
                            stronglyConnectedComponentsNetwork.addEdge(sourceNodes, targetNodes, (E) Edge.deepCopy(e));
                        });
            });
        });
    }

    private <NS extends Nodes> void mergeNode(MutableNetwork<NS, E> stronglyConnectedComponentsNetwork, List<DFSAttr> dfsAttrs) {
        Nodes nodes = new Nodes();
        nodes.setIds(new HashSet<>());
        for (DFSAttr dfsAttr : dfsAttrs) {
            if (dfsAttr.getFinish() - dfsAttr.getDepth() == 1) {
                nodes.getIds().add(dfsAttr.getId());
                nodes.setId(nodes.getIds().toString());
                stronglyConnectedComponentsNetwork.addNode((NS) nodes);
                nodes = new Nodes();
                nodes.setIds(new HashSet<>());
                continue;
            }
            nodes.getIds().add(dfsAttr.getId());
        }
    }

    private MutableNetwork<N, E> computeGraphT(MutableNetwork<N, E> network) {
        MutableNetwork<N, E> gt = NetworkBuilder.directed().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).allowsSelfLoops(true).build();
        network.nodes().forEach(node -> {
            gt.addNode((N) Node.deepCopy(node));
        });
        ExtMutableNetwork<N, E> extGt = new ExtMutableNetwork<>(gt);
        network.edges().forEach(e -> {
            EndpointPair<N> ep = network.incidentNodes(e);
            String srcId = ep.source().getId();
            String targetId = ep.target().getId();
            extGt.addEdge((N) extGt.getNodeById(targetId), (N) extGt.getNodeById(srcId), (E) Edge.deepCopy(e));
        });
        return gt;
    }

    public Map<String, DFSAttr> dfs(MutableNetwork<N, E> network, List<String> ids) {
        Map<String, DFSAttr> dfsAttrMap = network.nodes().stream()
                .collect(HashMap::new, (hashMap, node) -> hashMap.put(node.getId(), new DFSAttr(node.getId(), Color.WHITE, Integer.MAX_VALUE, Integer.MAX_VALUE, null)), HashMap::putAll);
        AtomicInteger time = new AtomicInteger(0);
        ExtMutableNetwork<N, E> extMutableNetwork = new ExtMutableNetwork<>(network);
        ids.stream().sequential().forEach(id -> {
            if (Objects.equals(dfsAttrMap.get(id).getColor(), Color.WHITE)) {
                dfsVisit(network, dfsAttrMap, time, (N) extMutableNetwork.getNodeById(id));
            }
        });
        return dfsAttrMap;
    }

    private Node getNodeById(String id) {
        return this.allNodeMap.get(id);
    }

    @Override
    public boolean addNode(N n1) {
        boolean result = this.network.addNode(n1);
        if (result) {
            this.allNodeMap.put(n1.getId(), n1);
            this.cache.cleanUp();
        }
        return result;
    }

    @Override
    public boolean addEdge(N n1, N n2, E e) {
        boolean result = this.network.addEdge(n1, n2, e);
        if (result) {
            this.allNodeMap.put(n1.getId(), n1);
            this.allNodeMap.put(n2.getId(), n2);
            this.cache.cleanUp();
        }
        return result;
    }

    @Override
    public boolean addEdge(EndpointPair<N> ep, E e) {
        boolean result = this.network.addEdge(ep, e);
        if (result) {
            this.allNodeMap.put(ep.nodeU().getId(), ep.nodeU());
            this.allNodeMap.put(ep.nodeV().getId(), ep.nodeV());
            this.cache.cleanUp();
        }
        return false;
    }

    @Override
    public boolean removeNode(N n1) {
        boolean result = this.network.removeNode(n1);
        if (result) {
            this.allNodeMap.remove(n1.getId());
            this.cache.cleanUp();
        }
        return false;
    }

    @Override
    public boolean removeEdge(E e) {
        boolean result = this.network.removeEdge(e);
        if (result) {
            this.cache.cleanUp();
        }
        return result;
    }
}
