package com.color.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.color.mall.product.dao.CategoryDao;
import com.color.mall.product.entity.CategoryEntity;
import com.color.mall.product.service.CategoryBrandRelationService;
import com.color.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.color.mall.common.utils.PageUtils;
import com.color.mall.common.utils.Query;

import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

//    @Override
//    public List<CategoryEntity> listWithTree() {
//        //1、查出所有分类
//        List<CategoryEntity> list = super.baseMapper.selectList(null);
//        //2、组装成父子的树形结构
//        //2.1）、找到所有的一级分类
//        List<CategoryEntity> level1Menus = list
//                .stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
//                .peek(menu -> menu.setChildren(getChildren(menu, list)))
//                .sorted(Comparator.comparingInt(menu -> menu.getSort() == null ? 0 : menu.getSort()))
//                .collect(Collectors.toList());
//        return level1Menus;
//    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> list = baseMapper.selectList(null);

        //2、找到所有的一级分类
        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        List<CategoryEntity> list1 = baseMapper.selectList(wrapper.eq(CategoryEntity::getParentCid, 0));
        //3、组装成父子的树形结构
//        List<CategoryEntity> collect = list1.stream()
//                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, list)))
//                .sorted(Comparator.comparingInt(c -> c.getSort() == null ? 0 : c.getSort()))
//                .collect(Collectors.toList());
        List<CategoryEntity> collect = new ArrayList<>();
        list1.forEach(categoryEntity -> {
            categoryEntity.setChildren(getChildren(categoryEntity, list));
            collect.add(categoryEntity);
        });
        return collect.stream().sorted(Comparator.comparingInt(v -> v.getSort() == null ? 0 : v.getSort())).collect(Collectors.toList());
    }

    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> list) {
        List<CategoryEntity> collect = new ArrayList<>();
        list.forEach(categoryEntity -> {
            if (root.getCatId().equals(categoryEntity.getParentCid())) {
                categoryEntity.setChildren(getChildren(categoryEntity, list));
                collect.add(categoryEntity);
            }
        });
        return collect.stream().sorted(Comparator.comparingInt(v -> v.getSort() == null ? 0 : v.getSort())).collect(Collectors.toList());
    }
//    递归查找所有菜单的子菜单
//    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
//        List<CategoryEntity> children = all
//                .stream().filter(categoryEntity -> categoryEntity.getParentCid() == root.getCatId())
//                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, all)))   //1、找到子菜单
//                .sorted(Comparator.comparingInt(menu -> menu.getSort() == null ? 0 : menu.getSort())) //排序
//                .collect(Collectors.toList());  //封装成list返回
//        return children;
//    }
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }



    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;

    }


}
