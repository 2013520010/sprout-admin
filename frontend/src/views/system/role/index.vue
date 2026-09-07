<template>
  <div>
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="角色名称">
          <el-input v-model="query.roleName" placeholder="请输入角色名称" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
      </div>

      <el-table :data="list" v-loading="loading">
        <el-table-column prop="roleId" label="角色ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleKey" label="权限标识" width="140" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" icon="Menu" @click="handleAssignMenu(row)">分配权限</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete([row.roleId])">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="如 admin、common" />
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

    <!-- 分配菜单权限弹窗 -->
    <el-dialog v-model="menuDialog.visible" title="分配菜单权限" width="420px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        show-checkbox
        node-key="menuId"
        :props="{ label: 'menuName', children: 'children' }"
        :default-checked-keys="menuDialog.checkedKeys"
      />
      <template #footer>
        <el-button @click="menuDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignMenu">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listRole, addRole, updateRole, delRole, getRoleMenus, assignRoleMenus } from '@/api/role'
import { listMenuTree } from '@/api/menu'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const menuTree = ref([])
const menuTreeRef = ref()

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  status: ''
})

const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false
})

const menuDialog = reactive({
  visible: false,
  roleId: null,
  checkedKeys: []
})

const formRef = ref()
const form = reactive({
  roleId: null,
  roleName: '',
  roleKey: '',
  sort: 0,
  status: '0'
})

const formRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限标识', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listRole(query)
    .then((data) => {
      list.value = data.list
      total.value = data.total
    })
    .finally(() => (loading.value = false))
}

function handleQuery() {
  query.pageNum = 1
  getList()
}

function resetQuery() {
  query.roleName = ''
  query.status = ''
  query.pageNum = 1
  getList()
}

function handleAdd() {
  dialog.title = '新增角色'
  dialog.isEdit = false
  resetForm()
  dialog.visible = true
}

function handleEdit(row) {
  dialog.title = '编辑角色'
  dialog.isEdit = true
  Object.assign(form, row)
  dialog.visible = true
}

function resetForm() {
  Object.assign(form, { roleId: null, roleName: '', roleKey: '', sort: 0, status: '0' })
}

async function submitForm() {
  await formRef.value.validate()
  if (dialog.isEdit) {
    await updateRole(form)
    ElMessage.success('修改成功')
  } else {
    await addRole(form)
    ElMessage.success('新增成功')
  }
  dialog.visible = false
  getList()
}

async function handleDelete(roleIds) {
  await ElMessageBox.confirm('确定删除选中的角色吗？', '提示', { type: 'warning' })
  await delRole(roleIds.join(','))
  ElMessage.success('删除成功')
  getList()
}

async function handleAssignMenu(row) {
  menuDialog.roleId = row.roleId
  menuDialog.checkedKeys = await getRoleMenus(row.roleId)
  menuDialog.visible = true
  await nextTick()
  menuTreeRef.value?.setCheckedKeys(menuDialog.checkedKeys)
}

async function submitAssignMenu() {
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
  await assignRoleMenus(menuDialog.roleId, [...checkedKeys, ...halfCheckedKeys])
  ElMessage.success('权限分配成功')
  menuDialog.visible = false
}

onMounted(() => {
  getList()
  listMenuTree().then((data) => (menuTree.value = data))
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}
.toolbar {
  margin-bottom: 12px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
