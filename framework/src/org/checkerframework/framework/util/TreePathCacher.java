package org.checkerframework.framework.util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * TreePathCacher is a TreeScanner that creates and caches a TreePath for a target
 * Tree.
 *
 * This class replicates some logic from TreePath.getPath but also adds
 * caching to all intermediate TreePaths that are generated. The intermediate TreePaths
 * are reused when other targets have overlapping paths.
 *
 * @author mcarthur
 */
public class TreePathCacher  extends TreeScanner<TreePath, Tree> {

    private Map<Tree, TreePath> foundPaths = new HashMap<>();
    // The TreePath of the previous tree scanned.
    // It is always set back to null after a scan has completed.
    private TreePath path;

    /**
     * Return the TreePath for a Tree.
     *
     * This method uses try/catch and the Result Error for control flow to
     * stop the superclass from scanning other subtrees when target is found.
     *
     * @param root The compilation unit to search in
     * @param target The target tree to look for
     * @return The TreePath corresponding to target,
     *  or null if target is not found in the compilation root
     */
    public TreePath getPath(CompilationUnitTree root, Tree target) {
        TreePath path = new TreePath(root);
        if (path.getLeaf() == target) {
            return path;
        }
        if (foundPaths.containsKey(target)) {
            return foundPaths.get(target);
        }

        try {
            this.scan(path, target);
        } catch (Result result) {
            return result.path;
        }
        return null;
    }

    private class Result extends Error {
        private static final long serialVersionUID = 4948452207518392627L;
        TreePath path;
        Result(TreePath path) {
            this.path = path;
        }
    }

    public void clear() {
        foundPaths.clear();
    }

    /**
     * Scan a single node.
     * The current path is updated for the duration of the scan.
     */
    @Override
    public TreePath scan(Tree tree, Tree target) {
        TreePath prev = path;
        if (tree != null && foundPaths.get(tree) == null) {
            TreePath current = new TreePath(path, tree);
            foundPaths.put(tree, current);
            path = current;
        } else {
            this.path = foundPaths.get(tree);
        }

        if (tree == target) {
            throw new Result(path);
        }
        try {
            return super.scan(tree, target);
        } finally {
            this.path = prev;
        }
    }
}