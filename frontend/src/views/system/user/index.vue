<template>
  <div>
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="请输入用户名" clearable @clear="handleQuery" />
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
      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" icon="Delete" :disabled="!selection.length" @click="handleDelete(selection.map(r => r.userId))">删除</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" icon="Key" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button link type="primary" icon="UserFilled" @click="handleAssignRole(row)">分配角色</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete([row.userId])">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="dialog.isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!dialog.isEdit" label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="留空默认 123456" show-password />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="form.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'deptId' }"
            check-strictly
            :render-after-expand="false"
            placeholder="请选择部门"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdDialog.visible" title="重置密码" width="400px">
      <el-form label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="pwdDialog.password" type="password" placeholder="留空默认 123456" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleDialog.visible" title="分配角色" width="400px">
      <el-checkbox-group v-model="roleDialog.checkedRoles">
        <el-checkbox v-for="role in allRoles" :key="role.roleId" :value="role.roleId">
          {{ role.roleName }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitAssignRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listUser, addUser, updateUser, delUser, resetUserPwd, getUserRoles, assignUserRoles
} from '@/api/user'
import { listAllRole } from '@/api/role'
import { listDeptTree } from '@/api/dept'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const selection = ref([])
const deptTree = ref([])
const allRoles = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  status: ''
})

const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false
})

const pwdDialog = reactive({
  visible: false,
  userId: null,
  password: ''
})

const roleDialog = reactive({
  visible: false,
  userId: null,
  checkedRoles: []
})

const formRef = ref()
const form = reactive({
  userId: null,
  username: '',
  password: '',
  nickname: '',
  deptId: null,
  phone: '',
  email: '',
  status: '0'
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listUser(query)
    .then((data) => {
      list.value = data.list
      total.value = data.total
    })
    .finally(() => {
      loading.value = false
    })
}

function handleQuery() {
  query.pageNum = 1
  getList()
}

function resetQuery() {
  query.username = ''
  query.status = ''
  query.pageNum = 1
  getList()
}

function handleSelectionChange(rows) {
  selection.value = rows
}

function handleAdd() {
  dialog.title = '新增用户'
  dialog.isEdit = false
  resetForm()
  dialog.visible = true
}

function handleEdit(row) {
  dialog.title = '编辑用户'
  dialog.isEdit = true
  Object.assign(form, row)
  form.password = ''
  dialog.visible = true
}

function resetForm() {
  Object.assign(form, {
    userId: null, username: '', password: '', nickname: '', deptId: null,
    phone: '', email: '', status: '0'
  })
}

async function submitForm() {
  await formRef.value.validate()
  if (dialog.isEdit) {
    await updateUser(form)
    ElMessage.success('修改成功')
  } else {
    await addUser(form)
    ElMessage.success('新增成功')
  }
  dialog.visible = false
  getList()
}

async function handleDelete(userIds) {
  await ElMessageBox.confirm('确定删除选中的用户吗？', '提示', { type: 'warning' })
  await delUser(userIds.join(','))
  ElMessage.success('删除成功')
  getList()
}

function handleResetPwd(row) {
  pwdDialog.userId = row.userId
  pwdDialog.password = ''
  pwdDialog.visible = true
}

async function submitResetPwd() {
  await resetUserPwd(pwdDialog.userId, pwdDialog.password)
  ElMessage.success('密码已重置')
  pwdDialog.visible = false
}

async function handleAssignRole(row) {
  roleDialog.userId = row.userId
  roleDialog.checkedRoles = await getUserRoles(row.userId)
  roleDialog.visible = true
}

async function submitAssignRole() {
  await assignUserRoles(roleDialog.userId, roleDialog.checkedRoles)
  ElMessage.success('角色分配成功')
  roleDialog.visible = false
}

onMounted(() => {
  getList()
  listDeptTree().then((data) => (deptTree.value = data))
  listAllRole().then((data) => (allRoles.value = data))
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
