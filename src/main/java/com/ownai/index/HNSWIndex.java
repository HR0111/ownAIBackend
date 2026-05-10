package com.ownai.index;
//
//
//import java.util.*;
//
public class HNSWIndex {
//
//    private static class Node {
//        int id;
//        float[] emb;
//        String metadata, category;
//        int maxLyr;
//        List<List<Integer>> nbrs;
//
//        Node(int id, float[] emb, String metadata, String category, int maxLyr) {
//            this.id = id; this.emb = emb;
//            this.metadata = metadata; this.category = category;
//            this.maxLyr = maxLyr;
//            this.nbrs = new ArrayList<>();
//            for (int i = 0; i <= maxLyr; i++) nbrs.add(new ArrayList<>());
//        }
//    }
//
//    private final Map<Integer, Node> G = new HashMap<>();
//    private final int M, M0, efBuild;
//    private final float mL;
//    private int topLayer = -1, entryPt = -1;
//    private final Random rng = new Random(42);
//
//    public HNSWIndex(int M, int efBuild) {
//        this.M = M; this.M0 = 2 * M; this.efBuild = efBuild;
//        this.mL = 1.0f / (float) Math.log(M);
//    }
//
//    private int randLevel() {
//        return (int) Math.floor(-Math.log(rng.nextFloat()) * mL);
//    }
//
//    private List<int[]> searchLayer(float[] q, int ep, int ef, int lyr) {
//        Map<Integer, Boolean> vis = new HashMap<>();
//        PriorityQueue<int[]> cands = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
//        PriorityQueue<int[]> found = new PriorityQueue<>((a, b) -> Float.compare(b[1], a[1]));
//
//        float d0 = cosine(q, G.get(ep).emb);
//        vis.put(ep, true);
//        cands.add(new int[]{ep, Float.floatToIntBits(d0)});
//        found.add(new int[]{ep, Float.floatToIntBits(d0)});
//
//        while (!cands.isEmpty()) {
//            int[] c = cands.poll();
//            int cid = c[0]; float cd = Float.intBitsToFloat(c[1]);
//            if (found.size() >= ef && cd > Float.intBitsToFloat(found.peek()[1])) break;
//            Node cn = G.get(cid);
//            if (cn == null || lyr >= cn.nbrs.size()) continue;
//            for (int nid : cn.nbrs.get(lyr)) {
//                if (vis.containsKey(nid) || !G.containsKey(nid)) continue;
//                vis.put(nid, true);
//                float nd = cosine(q, G.get(nid).emb);
//                if (found.size() < ef || nd < Float.intBitsToFloat(found.peek()[1])) {
//                    cands.add(new int[]{nid, Float.floatToIntBits(nd)});
//                    found.add(new int[]{nid, Float.floatToIntBits(nd)});
//                    if (found.size() > ef) found.poll();
//                }
//            }
//        }
//
//        List<int[]> res = new ArrayList<>(found);
//        res.sort(Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1])));
//        return res;
//    }
//
//    public synchronized void insert(int id, float[] emb, String metadata, String category) {
//        int lvl = randLevel();
//        G.put(id, new Node(id, emb, metadata, category, lvl));
//
//        if (entryPt == -1) { entryPt = id; topLayer = lvl; return; }
//
//        int ep = entryPt;
//        for (int lc = topLayer; lc > lvl; lc--) {
//            if (G.containsKey(ep) && lc < G.get(ep).nbrs.size()) {
//                List<int[]> W = searchLayer(emb, ep, 1, lc);
//                if (!W.isEmpty()) ep = W.get(0)[0];
//            }
//        }
//
//        for (int lc = Math.min(topLayer, lvl); lc >= 0; lc--) {
//            List<int[]> W = searchLayer(emb, ep, efBuild, lc);
//            int maxM = (lc == 0) ? M0 : M;
//            List<Integer> sel = new ArrayList<>();
//            for (int i = 0; i < Math.min(W.size(), maxM); i++) sel.add(W.get(i)[0]);
//
//            G.get(id).nbrs.set(lc, sel);
//
//            for (int nid : sel) {
//                if (!G.containsKey(nid)) continue;
//                Node nn = G.get(nid);
//                while (nn.nbrs.size() <= lc) nn.nbrs.add(new ArrayList<>());
//                nn.nbrs.get(lc).add(id);
//                if (nn.nbrs.get(lc).size() > maxM) {
//                    List<int[]> ds = new ArrayList<>();
//                    for (int c : nn.nbrs.get(lc))
//                        if (G.containsKey(c))
//                            ds.add(new int[]{c, Float.floatToIntBits(cosine(nn.emb, G.get(c).emb))});
//                    ds.sort(Comparator.comparingDouble(a -> Float.intBitsToFloat(a[1])));
//                    List<Integer> newConn = new ArrayList<>();
//                    for (int i = 0; i < Math.min(maxM, ds.size()); i++) newConn.add(ds.get(i)[0]);
//                    nn.nbrs.set(lc, newConn);
//                }
//            }
//            if (!W.isEmpty()) ep = W.get(0)[0];
//        }
//        if (lvl > topLayer) { topLayer = lvl; entryPt = id; }
//    }
//
//    public synchronized List<int[]> knn(float[] q, int k, int ef) {
//        if (entryPt == -1) return new ArrayList<>();
//        int ep = entryPt;
//        for (int lc = topLayer; lc > 0; lc--) {
//            if (G.containsKey(ep) && lc < G.get(ep).nbrs.size()) {
//                List<int[]> W = searchLayer(q, ep, 1, lc);
//                if (!W.isEmpty()) ep = W.get(0)[0];
//            }
//        }
//        List<int[]> W = searchLayer(q, ep, Math.max(ef, k), 0);
//        return W.subList(0, Math.min(k, W.size()));
//    }
//
//    public synchronized void remove(int id) {
//        if (!G.containsKey(id)) return;
//        for (Node n : G.values())
//            for (List<Integer> layer : n.nbrs)
//                layer.remove((Integer) id);
//        if (entryPt == id) {
//            entryPt = -1;
//            for (int nid : G.keySet()) if (nid != id) { entryPt = nid; break; }
//        }
//        G.remove(id);
//    }
//
//    public synchronized Map<String, Object> getInfo() {
//        int maxL = Math.max(topLayer + 1, 1);
//        int[] nodesPerLayer = new int[maxL];
//        int[] edgesPerLayer = new int[maxL];
//        for (Node n : G.values()) {
//            for (int lc = 0; lc <= n.maxLyr && lc < maxL; lc++) {
//                nodesPerLayer[lc]++;
//                if (lc < n.nbrs.size()) edgesPerLayer[lc] += n.nbrs.get(lc).size() / 2;
//            }
//        }
//        Map<String, Object> info = new HashMap<>();
//        info.put("topLayer",      topLayer);
//        info.put("nodeCount",     G.size());
//        info.put("nodesPerLayer", nodesPerLayer);
//        info.put("edgesPerLayer", edgesPerLayer);
//        return info;
//    }
//
//    public synchronized int size() { return G.size(); }
//
//    private float cosine(float[] a, float[] b) {
//        float dot = 0, na = 0, nb = 0;
//        for (int i = 0; i < a.length; i++) { dot += a[i]*b[i]; na += a[i]*a[i]; nb += b[i]*b[i]; }
//        if (na < 1e-9f || nb < 1e-9f) return 1.0f;
//        return 1.0f - dot / (float)(Math.sqrt(na) * Math.sqrt(nb));
//    }
}