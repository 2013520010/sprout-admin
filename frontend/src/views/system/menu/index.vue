<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-button type="primary" icon="Plus" @click="handleAdd(null)">新增菜单</el-button>
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <el-table :data="tree" v-loading="loading" row-key="menuId" default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="typeTag(row.menuType)" size="small">{{ typeName(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80" />
        <el-table-column prop="sort" label="排序" width="70" />
        <el-table-column prop="perms" label="权限标识" show-overflow-tooltip />
        <el-table-column prop="path" label="路由地址" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="Plus" @click="handleAdd(row)">新增</el-button>
            <el-button link type="primary" icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="560px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptionsTree"
            :props="{ label: 'menuName', value: 'menuId', children: 'children' }"
            check-strictly
            :render-after-expand="false"
            placeholder="选择上级菜单（空为根）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio-button value="M">目录</el-radio-button>
            <el-radio-button value="C">菜单</el-radio-button>
            <el-radio-button value="F">按钮</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'F'" label="路由地址">
          <el-input v-model="form.path" placeholder="如 /system/user" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 'C'" label="组件路径">
          <el-input v-model="form.component" placeholder="如 system/user/index" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 'M'" label="权限标识">
          <el-input v-model="form.perms" placeholder="如 system:user:list" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="Element Plus 图标名" />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMenuTree, addMenu, updateMenu, delMenu } from '@/api/menu'

const loading = ref(false)
const tree = ref([])

const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false
})

const formRef = ref()
const form = reactive({
  menuId: null,
  parentId: null,
  menuName: '',
  menuType: 'C',
  path: '',
  component: '',
  perms: '',
  icon: '',
  sort: 0,
  status: '0'
})

const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

function typeName(type) {
  return { M: '目录', C: '菜单', F: '按钮' }[type] || type
}

function typeTag(type) {
  return { M: 'warning', C: 'primary', F: 'info' }[type] || 'info'
}

// 父级选项：仅目录和菜单可作为父级
const parentOptionsTree = computed(() => buildParentOptions(tree.value))

function buildParentOptions(list) {
  return list
    .filter((m) => m.menuType !== 'F')
    .map((m) => ({ ...m, children: buildParentOptions(m.children || []) }))
}

function getList() {
  loading.value = true
  listMenuTree()
    .then((data) => {
      tree.value = data
    })
    .finally(() => (loading.value = false))
}

function handleAdd(parent) {
  dialog.title = '新增菜单'
  dialog.isEdit = false
  resetForm()
  form.parentId = parent ? parent.menuId : null
  dialog.visible = true
}

function handleEdit(row) {
  dialog.title = '编辑菜单'
  dialog.isEdit = true
  Object.assign(form, row)
  dialog.visible = true
}

function resetForm() {
  Object.assign(form, {
    menuId: null, parentId: null, menuName: '', menuType: 'C',
    path: '', component: '', perms: '', icon: '', sort: 0, status: '0'
  })
}

async function submitForm() {
  await formRef.value.validate()
  if (dialog.isEdit) {
    await updateMenu(form)
    ElMessage.success('修改成功')
  } else {
    await addMenu(form)
    ElMessage.success('新增成功')
  }
  dialog.visible = false
  getList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除菜单「${row.menuName}」吗？`, '提示', { type: 'warning' })
  await delMenu(row.menuId)
  ElMessage.success('删除成功')
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
