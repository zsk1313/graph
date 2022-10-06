package org.zsk13.graph.domain.po;

import com.google.common.graph.ElementOrder;
import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("增强可变网络图测试")
class ExtMutableNetworkTest {

    @Test
    void printPath() {
        MutableNetwork<Node, Edge> bfsNetwork = createBfs01();
        ExtMutableNetwork<Node, Edge> extBfsNetwork=new ExtMutableNetwork<>(bfsNetwork);
        LinkedList<String> linkedList = extBfsNetwork.printPath("s", "v");
        Assertions.assertArrayEquals(Arrays.asList("s","r","v").toArray(),linkedList.toArray());
    }

    @Test
    void dfs() {
        MutableNetwork<Node, Edge> dfsNetwork = createDfs01();
        ExtMutableNetwork<Node, Edge> extDfsNetwork = new ExtMutableNetwork<>(dfsNetwork);
        Map<String, DFSAttr> dfsAttrMap = extDfsNetwork.dfs();
        System.out.println(dfsAttrMap);
    }

    @Test
    void topologicalSort() {
        MutableNetwork<Node, Edge> dfsNetwork = createDfs02();
        ExtMutableNetwork<Node, Edge> extDfsNetwork = new ExtMutableNetwork<>(dfsNetwork);
        Map<String, DFSAttr> dfsAttrMap = extDfsNetwork.dfs();
        List<DFSAttr> topologicalSort = extDfsNetwork.topologicalSort(dfsAttrMap);
        System.out.println(topologicalSort);
    }

    @Test
    void stronglyConnectedComponents() {
        MutableNetwork<Node, Edge> dfsNetwork = createDfs03();
        ExtMutableNetwork<Node, Edge> extDfsNetwork = new ExtMutableNetwork<>(dfsNetwork);
        MutableNetwork<Nodes, Edge> stronglyConnectedComponents = extDfsNetwork.stronglyConnectedComponents();
        System.out.println(stronglyConnectedComponents);
    }

    @Test
    void mstKruskal() {
        MutableNetwork<Node, WeightEdge> mstNetwork = createMst01();
        List<WeightEdge> weightEdges = ExtMutableNetwork.mstKruskal(mstNetwork);
        System.out.println(weightEdges);
    }

    @Test
    void mstPrim() {
        MutableNetwork<Node, WeightEdge> mstNetwork = createMst01();
        Map<String, PrimAttr> primAttrMap = ExtMutableNetwork.mstPrim(mstNetwork, "a");
        System.out.println(primAttrMap);
    }

    @Test
    void bellmanFord() {
        MutableNetwork<Node, WeightEdge> fordNetwork = createFord01();
        BellmanFordResult bellmanFordResult = ExtMutableNetwork.bellmanFord(fordNetwork, "s");
        System.out.println(bellmanFordResult);
    }

    private static MutableNetwork<Node, Edge> createBfs01() {
        MutableNetwork<Node, Edge> bfs = NetworkBuilder.undirected().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).build();
        Node v = new Node("v");
        Node r = new Node("r");
        Node s = new Node("s");
        Node w = new Node("w");
        Node t = new Node("t");
        Node x = new Node("x");
        Node u = new Node("u");
        Node y = new Node("y");
        bfs.addEdge(v, r, new Edge("v-r"));
        bfs.addEdge(r, s, new Edge("r-s"));
        bfs.addEdge(s, w, new Edge("s-w"));
        bfs.addEdge(w, t, new Edge("w-t"));
        bfs.addEdge(w, x, new Edge("w-x"));
        bfs.addEdge(t, u, new Edge("t-u"));
        bfs.addEdge(t, x, new Edge("t-x"));
        bfs.addEdge(x, u, new Edge("x-u"));
        bfs.addEdge(x, y, new Edge("x-y"));
        bfs.addEdge(u, y, new Edge("u-y"));
        return bfs;
    }

    private static MutableNetwork<Node, Edge> createDfs01() {
        MutableNetwork<Node, Edge> dfs = NetworkBuilder.directed().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).allowsSelfLoops(true).build();
        Node x = new Node("x");
        Node u = new Node("u");
        Node y = new Node("y");
        Node v = new Node("v");
        Node w = new Node("w");
        Node z = new Node("z");
        dfs.addEdge(x, v, new Edge("x-v"));
        dfs.addEdge(u, x, new Edge("u-x"));
        dfs.addEdge(u, v, new Edge("u-v"));
        dfs.addEdge(y, x, new Edge("y-x"));
        dfs.addEdge(v, y, new Edge("v-y"));
        dfs.addEdge(w, y, new Edge("w-y"));
        dfs.addEdge(w, z, new Edge("w-z"));
        dfs.addEdge(z, z, new Edge("z-z"));
        return dfs;
    }

    private static MutableNetwork<Node, Edge> createDfs02() {
        MutableNetwork<Node, Edge> dfs = NetworkBuilder.directed().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).build();
        Node n1 = new Node("内裤");
        Node n2 = new Node("裤子");
        Node n3 = new Node("腰带");
        Node n4 = new Node("衬衣");
        Node n5 = new Node("领带");
        Node n6 = new Node("夹克");
        Node n7 = new Node("袜子");
        Node n8 = new Node("鞋");
        Node n9 = new Node("手表");

        dfs.addEdge(n1, n2, new Edge("内裤-裤子"));
        dfs.addEdge(n1, n8, new Edge("内裤-鞋"));
        dfs.addEdge(n2, n8, new Edge("裤子-鞋"));
        dfs.addEdge(n3, n6, new Edge("腰带-夹克"));
        dfs.addEdge(n4, n3, new Edge("衬衣-腰带"));
        dfs.addEdge(n4, n5, new Edge("衬衣-领带"));
        dfs.addEdge(n5, n6, new Edge("领带-夹克"));
        dfs.addEdge(n7, n8, new Edge("袜子-鞋"));
        dfs.addNode(n9);
        return dfs;
    }

    private static MutableNetwork<Node, Edge> createDfs03() {
        MutableNetwork<Node, Edge> dfs = NetworkBuilder.directed().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).allowsSelfLoops(true).build();
        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        Node d = new Node("d");
        Node e = new Node("e");
        Node f = new Node("f");
        Node g = new Node("g");
        Node h = new Node("h");

        dfs.addEdge(a, b, new Edge("a-b"));
        dfs.addEdge(b, e, new Edge("b-e"));
        dfs.addEdge(e, a, new Edge("e-a"));
        dfs.addEdge(b, c, new Edge("b-c"));
        dfs.addEdge(b, f, new Edge("b-f"));
        dfs.addEdge(e, f, new Edge("e-f"));
        dfs.addEdge(f, g, new Edge("f-g"));
        dfs.addEdge(g, f, new Edge("g-f"));
        dfs.addEdge(c, g, new Edge("c-g"));
        dfs.addEdge(g, h, new Edge("g-h"));
        dfs.addEdge(h, h, new Edge("h-h"));
        dfs.addEdge(c, d, new Edge("c-d"));
        dfs.addEdge(d, c, new Edge("d-c"));
        dfs.addEdge(d, h, new Edge("d-h"));
        return dfs;
    }

    private static MutableNetwork<Node, WeightEdge> createMst01() {
        MutableNetwork<Node, WeightEdge> mst = NetworkBuilder.undirected().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).build();
        Node a = new Node("a");
        Node b = new Node("b");
        Node c = new Node("c");
        Node d = new Node("d");
        Node e = new Node("e");
        Node f = new Node("f");
        Node g = new Node("g");
        Node h = new Node("h");
        Node i = new Node("i");

        mst.addEdge(a, b, new WeightEdge("a-b",4));
        mst.addEdge(a, h, new WeightEdge("a-h",8));
        mst.addEdge(b, h, new WeightEdge("b-h",11));
        mst.addEdge(b, c, new WeightEdge("b-c",8));
        mst.addEdge(h, i, new WeightEdge("h-i",7));
        mst.addEdge(h, g, new WeightEdge("h-g",1));
        mst.addEdge(i, c, new WeightEdge("i-c",2));
        mst.addEdge(i, g, new WeightEdge("i-g",6));
        mst.addEdge(c, d, new WeightEdge("c-d",7));
        mst.addEdge(c, f, new WeightEdge("c-f",4));
        mst.addEdge(g, f, new WeightEdge("g-f",2));
        mst.addEdge(d, e, new WeightEdge("d-e",9));
        mst.addEdge(d, f, new WeightEdge("d-f",14));
        mst.addEdge(f, e, new WeightEdge("f-e",10));
        return mst;
    }

    private static MutableNetwork<Node, WeightEdge> createFord01() {
        MutableNetwork<Node, WeightEdge> ford = NetworkBuilder.directed().allowsParallelEdges(false).nodeOrder(ElementOrder.insertion()).build();
        Node s = new Node("s");
        Node t = new Node("t");
        Node y = new Node("y");
        Node x = new Node("x");
        Node z = new Node("z");

        ford.addEdge(s, y, new WeightEdge("s-y",7));
        ford.addEdge(s, t, new WeightEdge("s-t",6));
        ford.addEdge(t, y, new WeightEdge("t-y",8));
        ford.addEdge(t, x, new WeightEdge("t-x",5));
        ford.addEdge(t, z, new WeightEdge("t-z",-4));
        ford.addEdge(y, z, new WeightEdge("y-z",9));
        ford.addEdge(y, x, new WeightEdge("y-x",-3));
        ford.addEdge(x, t, new WeightEdge("x-t",-2));
        ford.addEdge(z, x, new WeightEdge("z-x",7));
        ford.addEdge(z, s, new WeightEdge("z-s",2));
        return ford;
    }
}