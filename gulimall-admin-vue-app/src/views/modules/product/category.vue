<template>
  <div>
    <div class="c-head">
      <el-switch
          v-model="draggable"
          active-text="开启拖拽"
          inactive-text="关闭拖拽">
      </el-switch>
      <div>
        <el-button
            class="b-button"
            v-show="draggable"
            type="primary"
            plain
            size="mini"
            @click="batchSave"
        >
          批量保存
        </el-button>
        <el-button
            class="b-button"
            type="danger"
            plain
            size="mini"
            @click="batchRemove"
        >
          批量删除
        </el-button>
      </div>
    </div>

    <el-tree
        :data="menu"
        :props="defaultProps"
        show-checkbox
        node-key="catId"
        :expand-on-click-node="false"
        :default-expanded-keys="defaultKey"
        :draggable="draggable"
        :allow-drop="allowDrop"
        @node-drop="handleDrop"
        ref="menuTree"
    >
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span class="btns-product">
          <el-button
              v-if="data.catLevel<3||data.children"
              plain
              class="btn-product"
              type="primary"
              size="mini"
              @click="() => append(data)">
            添加子分类
          </el-button>
          <el-button
              plain
              class="btn-product"
              type="warning"
              size="mini"
              @click="() => editMenu(data)">
            编辑
          </el-button>
          <el-button
              v-if="data.catLevel===3||!data.children"
              plain
              class="btn-product"
              type="danger"
              size="mini"
              @click="() => remove(node, data)">
            删除
          </el-button>
        </span>
      </span>
    </el-tree>
    <el-dialog
        :title="title"
        :visible.sync="dialogVisible"
        width="30%"
        :close-on-click-modal="false"
    >
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="category.icon" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="category.productUnit" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
    <el-button @click="dialogVisible = false">取 消</el-button>
    <el-button type="primary" @click="submitData()">确 定</el-button>
  </span>
    </el-dialog>
  </div>
</template>

<script>
import draggable from 'element-ui/packages/color-picker/src/draggable'

export default {
  data () {
    return {
      //父id
      pCid: [],
      //是否可拖拽
      draggable: false,
      //待更新节点
      updateNodes: [],
      title: '',//
      maxLevel: 0,
      dialogType: '',//点击添加和编辑
      category: {
        name: '',
        icon: '',
        productUnit: '',
        parentCid: 0,
        catLevel: 0,
        showStatus: 1,
        sort: 0,
        catId: null,
      },
      dialogVisible: false,
      menu: [],
      defaultKey: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  methods: {
    append (data) {
      this.dialogType = 'add'
      this.title = '添加分类'
      this.dialogVisible = true//打开对话框
      this.category.name = data.name//新添加的菜单名
      this.category.parentCid = data.catId//新添加的菜单父id
      this.category.catLevel = data.catLevel * 1 + 1//新添加的菜单级别
      this.category.showStatus = 1//是否展示
      //待添加的菜单名
      this.category.name = ''
      //待添加的菜单图标
      this.category.icon = ''
      //待添加的计量单位
      this.category.productUnit = ''
      this.sort = 0
    },
    //批量删除
    batchRemove () {
      let checkedNodes = this.$refs.menuTree.getCheckedNodes()
      let ids = []
      let names = []
      for (let i = 0; i < checkedNodes.length; i++) {
        ids.push(checkedNodes[i].catId)
        names.push(checkedNodes[i].name)
      }
      this.$confirm(`是否删除【${names}】?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(() => {
          this.$message({
            type: 'warning',
            message: `成功删除【${names}】`
          })
          this.getMenus()
          // this.defaultKey = [node.parent.data.catId]
        })
        // console.log('被删除的编号为', data.catId)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    batchSave () {
      this.$http({
        url: this.$http.adornUrl('/product/category/update/sort'),
        method: 'post',
        data: this.$http.adornData(this.updateNodes, false)
      }).then(({data}) => {
        this.$message({
          message: '菜单顺序等修改成功',
          type: 'success'
        })
        //刷新出新的菜单
        this.getMenus()
        //设置默认展开的菜单
        this.defaultKey = this.pCid
        //清空更新节点和最大层级的数据
        this.updateNodes = []
        this.maxLevel = 0
        this.pCid = 0
      })
    },
    //提交数据
    submitData () {
      // if (this.dialogType === "add") {
      //   this.addCategory()
      // }
      // if (this.dialogType === "edit") {
      //   this.editCategory()
      // }
      this.dialogType === 'add' ? this.addCategory() :
          this.dialogType === 'edit' ? this.editCategory() : this.addCategory()
    },
    //修改三级分类数据
    editCategory () {
      let {catId, name, icon, productUnit} = this.category
      this.$http({
        url: this.$http.adornUrl('/product/category/update'),
        method: 'post',
        data: this.$http.adornData({catId, name, icon, productUnit}, false)
      }).then(({data}) => {
        this.$message({
          message: '菜单修改成功',
          type: 'success'
        })
        //关闭对话框
        this.dialogVisible = false
        //刷新出新的菜单
        this.getMenus()
        //设置需要默认展开的菜单
        this.defaultKey = [this.category.parentCid]
      })

    },
    //添加三级分类
    addCategory () {
      console.log('提交的三级分类数据', this.category)
      this.$http({
        url: this.$http.adornUrl('/product/category/save'),
        method: 'post',
        data: this.$http.adornData(this.category, false)
      }).then(({data}) => {
        this.$message({
          message: '保存成功',
          type: 'success'
        })
        //关闭对话框
        this.dialogVisible = false
        //刷新菜单
        this.getMenus()
        //设置默认展开的菜单
        this.defaultKey = [this.category.parentCid]
      })
    },

    /**
     *
     * @param draggingNode 被拖拽节点
     * @param dropNode 拖拽目标节点
     * @param type 往哪里拖 (inner, pre, next)
     * @returns {boolean}
     */
    allowDrop (draggingNode, dropNode, type) {
      //1、被拖动的当前节点以及所在的父节点总层数不能大于3
      //1.1、被拖动的当前节点总层数
      console.log('allowDrop', draggingNode, dropNode, type)

      this.countNodeLevel(draggingNode)
      //当前正在拖动的节点+父节点所在深度<=3即可
      let depth = this.maxLevel - draggingNode.level + 1//当前拖拽节点的层级深度
      console.log('深度', depth)
      if (type === 'inner') {
        console.log('深度+拖拽节点的层级', depth + dropNode.level)
        return (depth + dropNode.level) <= 3
      } else {
        console.log('深度+拖拽节点的层级', depth + dropNode.level)
        return (depth + dropNode.parent.level) <= 3
      }
    },
    /**
     *
     * @param draggingNode 拖拽的元素
     * @param dropNode 拖拽目标元素
     * @param dropType 拖拽类型
     * @param ev
     */
    handleDrop (draggingNode, dropNode, dropType, ev) {
      console.log('handleDrop', draggingNode, dropNode, dropType)
      //1. 当前节点最新的父节点id
      let pCid = 0
      //2.当前节点的兄弟节点
      let siblings = null
      //拖拽的类型是相邻的时候
      if (dropType == 'before' || dropType == 'after') {
        //若无父id,则为一级菜单父id设置为0
        pCid = dropNode.parent.data.catId == undefined ? 0 : dropNode.parent.data.catId
        //兄弟节点就是拖拽目标节点的父亲的子节点们
        siblings = dropNode.parent.childNodes
      } else {
        //拖拽类型是往里面的时候
        //拖拽目标节点的菜单id就是改拖拽节点的父id
        pCid = dropNode.data.catId
        //兄弟节点就是拖拽的目标节点的子节点们
        siblings = dropNode.childNodes
      }
      this.pCid.push(pCid)

      //当前拖拽节点的最新顺序
      for (let i = 0; i < siblings.length; i++) {
        if (siblings[i].data.catId == draggingNode.data.catId) {
          //如果遍历的是当前正在拖拽的节点
          let catLevel = draggingNode.level
          if (siblings[i].level != draggingNode.level) {
            //当前节点的层级发生变化
            catLevel = siblings[i].level
            //修改其子节点的层级
            this.updateChildNodeLevel(siblings[i])
          }
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i,
            parentCid: pCid
          })
        } else {
          this.updateNodes.push({
            catId: siblings[i].data.catId,
            sort: i
          })
        }
      }
      //3、当前拖拽节点的最新层级
      console.log('updateNodes', this.updateNodes)

    },

    //递归更新子节点的层级
    updateChildNodeLevel (node) {
      if (node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          let currentNode = node.childNodes[i].data
          this.updateNodes.push({
            catId: currentNode.catId,
            catLevel: node.childNodes[i].level
          })
          this.updateChildNodeLevel(node.childNodes[i])
        }
      }
    },
    //计算节点的层级
    countNodeLevel (node) {
      //找到所有子节点,求出最大深度
      if (node.childNodes != null && node.childNodes.length > 0) {
        for (let i = 0; i < node.childNodes.length; i++) {
          if (node.childNodes[i].level > this.maxLevel) {
            this.maxLevel = node.childNodes[i].level
          }
          this.countNodeLevel(node.childNodes[i])
        }
      } else {
        this.maxLevel = 3
      }
    },

    //删除菜单
    remove (node, data) {
      let ids = [data.catId]
      this.$confirm(`是否删除【${data.name}】?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(() => {
          this.$message({
            type: 'warning',
            message: `成功删除【${data.name}】`
          })
          this.getMenus()
          this.defaultKey = [node.parent.data.catId]
        })
        // console.log('被删除的编号为', data.catId)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })

      // console.log(node, data)
    },
    //编辑菜单
    editMenu (data) {
      console.log('要修改的数据是', data)
      this.dialogType = 'edit'
      this.title = '修改分类'
      this.dialogVisible = true
      //发送请求获取当前节点最新的数据
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
        method: 'get'
      }).then(({data}) => {
        console.log('要回显的数据', data)
        this.category.name = data.data.name//待编辑的菜单名
        this.category.catId = data.data.catId
        this.category.icon = data.data.icon
        this.category.productUnit = data.data.productUnit
        this.category.parentCid = data.data.parentCid
      })

    },
    getMenus () {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({data}) => {
        this.menu = data.data
      })
    }
  },
  created () {
    this.getMenus()
  }
}
</script>
<style scoped>
.btn-product {
  margin-top: -7px;
  padding-top: 3px;
  padding-bottom: 3px;
}

.btns-product {
  position: absolute;
  left: 300px;
}

.c-head {
  height: 30px;
  display: flex;
  justify-content: space-between;
}

.b-button {
  margin-top: -10px;
}
</style>
