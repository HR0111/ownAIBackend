package com.ownai.index;
//
//import java.util.*;
//
public class KDTreeIndex {
//
//    private static class Node {
//        int id; float[] emb;
//        Node left, right;
//        Node(int id, float[] emb) { this.id = id; this.emb = emb; }
//    }
//
//    private Node root;
//    private final int dims;
//
//    public KDTreeIndex(int dims) { this.dims = dims; }
//
//    public synchronized void insert(int id, float[] emb) {
//        root = insert(root, id, emb, 0);
//    }
//
//    private Node insert(Node n, int id, float[] emb, int d) {
//        if (n == null) return new Node(id, emb);
//        int ax = d % dims;
//        if (emb[ax] < n.emb[ax]) n.left  = insert(n.left,  id, emb, d + 1);
//        else                      n.right = insert(n.right, id, emb, d + 1);
//        return n;
//    }
//
//    public synchronized List<int[]> knn(float[] q, int k, String metric) {
//        PriorityQueue<int[]> heap = new PriorityQueue<>(
//                (a, b) -> Float.compare(Float.intBitsToFloat(b[1]), Float.intBitsToFloat(a[1]))
//        );
//        knn(root, q, k, 0, metric, heap);
//        List<int[]> res = new ArrayList<>(heap);
//        res.sort(Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1])));
//        return res;
//    }
//
//    private void knn(Node n, float[] q, int k, int d, String metric,
//                     PriorityQueue<int[]> heap) {
//        if (n == null) return;
//        float dn = dist(q, n.emb, metric);
//        if (heap.size() < k || dn < Float.intBitsToFloat(heap.peek()[1])) {
//            heap.add(new int[]{n.id, Float.floatToIntBits(dn)});
//            if (heap.size() > k) heap.poll();
//        }
//        int ax = d % dims;
//        float diff = q[ax] - n.emb[ax];
//        Node closer  = diff < 0 ? n.left  : n.right;
//        Node farther = diff < 0 ? n.right : n.left;
//        knn(closer,  q, k, d + 1, metric, heap);
//        if (heap.size() < k || Math.abs(diff) < Float.intBitsToFloat(heap.peek()[1]))
//            knn(farther, q, k, d + 1, metric, heap);
//    }
//
//    public synchronized void rebuild(List<int[]> items, List<float[]> embs) {
//        root = null;
//        for (int i = 0; i < items.size(); i++) insert(items.get(i)[0], embs.get(i));
//    }
//
//    private float dist(float[] a, float[] b, String metric) {
//        return switch (metric) {
//            case "euclidean" -> euclidean(a, b);
//            case "manhattan" -> manhattan(a, b);
//            default          -> cosine(a, b);
//        };
//    }
//
//    private float cosine(float[] a, float[] b) {
//        float dot=0,na=0,nb=0;
//        for (int i=0;i<a.length;i++){dot+=a[i]*b[i];na+=a[i]*a[i];nb+=b[i]*b[i];}
//        if(na<1e-9f||nb<1e-9f) return 1f;
//        return 1f-(float)(dot/(Math.sqrt(na)*Math.sqrt(nb)));
//    }
//    private float euclidean(float[] a, float[] b) {
//        float s=0; for(int i=0;i<a.length;i++){float d=a[i]-b[i];s+=d*d;} return (float)Math.sqrt(s);
//    }
//    private float manhattan(float[] a, float[] b) {
//        float s=0; for(int i=0;i<a.length;i++) s+=Math.abs(a[i]-b[i]); return s;
//    }
}