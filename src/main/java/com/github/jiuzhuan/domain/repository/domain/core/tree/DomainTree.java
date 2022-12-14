package com.github.jiuzhuan.domain.repository.domain.core.tree;

import java.util.*;

/**
 * 领域聚合结构化树 -> 实体关系树
 *
 * 领域聚合结构化树有以下特点:
 * 1. 所有中间节点(包括顶点)都是子聚合(非实体), 所有叶子节点都是实体(对应一个表)
 * 2. 结构可以使用intelij-idea的图显示, 以便轻松查询领域内所有实体的关系: 将聚合所有相关类拖到图里, 右键-布局-正交-分层, 右键-方向-从上到下
 *
 * 两者不完全对应, 实体关系树由领域聚合结构化树转化而来, 要做稍微变化: 子聚合内的顶点实体(表)上浮到父节点那一层, 子聚合本身不当节点被忽略
 *
 * 实体关系树有以下特点:
 * 1. 每个节点都是一个实体(对应一个表)
 * 2. 节点的入箭头代表实体包含某个字段用来关联别的实体
 * 3. 节点的出箭头代表实体中某个字段被其它实体保存用以关联
 * 4. 根节点只有出箭头, 叶子节点只有入箭头, 中间节点有入箭头也有出箭头, 他是特殊的中间实体(表)
 *
 * todo 对于类似于无限自我递归的表, 目前无法适用, 对于单个表有多个向外关联的字段也暂时没想到好办法, 但是不能因此全盘否定, 一个工具或框架解决了大部分的问题那也是很有价值的
 * @author pengfwang@trip.com
 * @date 2022/9/18 15:13
 */
public class DomainTree {

    /**
     * 聚合的根节点
     */
    public DomainTreeNode rootNode;

    /**
     * 索引-记录聚合内每个实体在哪一层
     */
    public Map<Class<?>, DomainTreeNode> entityNodeMap = new HashMap<>();

    public DomainTreeNode getNodeByEntity(Class<?> entity) {
        return entityNodeMap.get(entity);
    }

    /**
     * 寻找目标节点最近的已知的约束节点到目标节点路径
     * 方法一(最小公顶点):
     * 1.从根节点出发, 查询到目标节点和到所有已知节点的路径
     * 2.找到根节点-已经节点路径和根节点-目标节点 重复的那一段路径 这段路径的最低节点就是已知节点-目标节点的 [最小公顶点]
     * 3.目标节点到已知节点的路径长度 = 最小公顶点到已知节点的路径长度 + 最小公顶点到目标节点的路径长度
     * 方法二(动态规划改):
     * 1.找目标节点到最近已知节点的路径 = min(目标节点父节点到已知节点的最小路径+1, min(目标节点所有子节点到已知节点的最小路径))
     * 2.每次递归保证所有解只执行一步, 找到已知节点时直接退出方法不再继续
     */
    public List<DomainTreeNode> recentKnownNode(DomainTreeNode targetNode, Set<DomainTreeNode> clearNodes) {
        // 目标节点到根节点的路径
        List<DomainTreeNode> targetPath2root = new ArrayList<>();
        targetPath2root.add(targetNode);
        path2root(targetNode, targetPath2root);
        // 已知节点到根节点的路径 已知节点必然是互相连接的  所以可以用第一个节点往上找直到未知节点
        DomainTreeNode clearTopNode = clearNodes.stream().findFirst().orElseThrow();
        for (DomainTreeNode clearNode : clearNodes) {
            if (!clearNodes.contains(clearNode.parentNode)) clearTopNode = clearNode;
        }
        List<DomainTreeNode> clearPath2root = new ArrayList<>();
        clearPath2root.add(clearTopNode);
        path2root(clearTopNode, clearPath2root);
        // 找到最小公顶点, 得到经过最小公顶点的路径
        return minPublicNode(targetPath2root, clearPath2root);
    }

    private List<DomainTreeNode> minPublicNode(List<DomainTreeNode> targetPath2root, List<DomainTreeNode> clearPath2root) {
        int index = 0;
        for (int i = 0; i < targetPath2root.size() && i < clearPath2root.size(); i++) {
            if (!Objects.equals(targetPath2root.get(i).hashCode(), clearPath2root.get(i).hashCode())) {
                break;
            }
            index++;
        }
        List<DomainTreeNode> prePath = clearPath2root.subList(index, clearPath2root.size());
        List<DomainTreeNode> postPath = targetPath2root.subList(index - 1, targetPath2root.size());
        prePath.addAll(postPath);
        return prePath;
    }

    private List<DomainTreeNode> path2root(DomainTreeNode targetNode, List<DomainTreeNode> path) {
        if (targetNode.parentNode != null) {
            path.add(0, targetNode.parentNode);
            path2root(targetNode.parentNode, path);
        }
        return path;
    }

}
