package pset3;

import java.util.*;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class CFG {

    Set<Node> nodes = new HashSet<Node>();
    Map<Node, Set<Node>> edges = new HashMap<Node, Set<Node>>();

    public static class Node {
        int position;
        Method method;
        JavaClass clazz;
        Node(int p, Method m, JavaClass c) {
            position = p;
            method = m;
            clazz = c;
        }
        public Method getMethod() {return method;}
        public JavaClass getClazz() {return clazz;}

        public boolean equals(Object o) {
            if (!(o instanceof Node)) return false;
            Node n = (Node)o;
            return (position == n.position) &&
                method.equals(n.method) && clazz.equals(n.clazz);
        }
        public int hashCode() {
            return position + method.hashCode() + clazz.hashCode();
        }
        public String toString() {
            return String.format("%s.%s%s: %s", clazz.getClassName(), 
                                 method.getName(), method.getSignature(), 
                                 position);
        }
    }

    public void addNode(int p, Method m, JavaClass c) {
        addNode(new Node(p, m, c));
    }

    private void addNode(Node n) {
        nodes.add(n);
        Set<Node> neighbors = edges.get(n);
        if (neighbors == null) {
            neighbors = new HashSet<Node>();
            edges.put(n, neighbors);
        }
    }

    public void addEdge(int p1, Method m1, JavaClass c1, int p2, Method m2, JavaClass c2) {
        Node n1 = new Node(p1, m1, c1);
        Node n2 = new Node(p2, m2, c2);
        addNode(n1);
        addNode(n2);
        Set<Node> neighbors = edges.get(n1);
        neighbors.add(n2);
        edges.put(n1, neighbors);
    }
    public void addEdge(int p1, int p2, Method m, JavaClass c) {
        addEdge(p1, m, c, p2, m, c);
    }
    public String toString() {
        
        return String.format("%d nodes\nnodes: %s\nedges: %s", nodes.size(), 
                             nodes, edges);
    }

    public boolean isReachable(String methodFrom, String clazzFrom,
                               String methodTo, String clazzTo) {
        if (clazzFrom.equals(clazzTo) && methodFrom.equals(methodTo)) { return true; }

        Queue<Node> que = new LinkedList<Node>();
        for (Node n : nodes) {
            if (n.getMethod().getName().equals(methodFrom) &&
                n.getClazz().getClassName().equals(clazzFrom)) {
                
                /* From all matching methodFrom nodes */
                que.add(n);
            }
        }
        while(!que.isEmpty()) {
            Node n = que.remove();           
            if (n.getMethod().getName().equals(methodTo) &&
                n.getClazz().getClassName().equals(clazzTo)) {
                return true;
            }
            for (Node neighbor : edges.get(n)) {
                que.add(neighbor);
            }
        }
        return false;
    }
}

class PrettyPrintingMap<K, V> {
    private Map<K, V> map;

    public PrettyPrintingMap(Map<K, V> map) {
        this.map = map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            sb.append(entry.getKey());
            sb.append('=').append('"');
            sb.append(entry.getValue());
            sb.append('"');
            if (iter.hasNext()) {
                sb.append(',').append('\n');
            }
        }
        return sb.toString();

    }
}