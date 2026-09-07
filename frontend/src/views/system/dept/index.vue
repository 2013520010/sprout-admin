<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-button type="primary" icon="Plus" @click="handleAdd(null)">新增部门</el-button>
        <el-button icon="Refresh" @click="getList">刷新</el-button>
      </div>

      <el-table :data="tree" v-loading="loading" row-key="deptId" default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="deptName" label="部门名称" width="220" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
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

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="80px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="tree"
            :props="{ label: 'deptName', value: 'deptId', children: 'children' }"
            check-strictly
            :render-after-expand="false"
            placeholder="选择上级部门（空为根）"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listDeptTree, addDept, updateDept, delDept } from '@/api/dept'

const loading = ref(false)
const tree = ref([])

const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false
})

const formRef = ref()
const form = reactive({
  deptId: null,
  parentId: null,
  deptName: '',
  sort: 0,
  leader: '',
  phone: '',
  email: '',
  status: '0'
})

const formRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
}

function getList() {
  loading.value = true
  listDeptTree()
    .then((data) => (tree.value = data))
    .finally(() => (loading.value = false))
}

function handleAdd(parent) {
  dialog.title = '新增部门'
  dialog.isEdit = false
  resetForm()
  form.parentId = parent ? parent.deptId : null
  dialog.visible = true
}

function handleEdit(row) {
  dialog.title = '编辑部门'
  dialog.isEdit = true
  Object.assign(form, row)
  dialog.visible = true
}

function resetForm() {
  Object.assign(form, {
    deptId: null, parentId: null, deptName: '', sort: 0,
    leader: '', phone: '', email: '', status: '0'
  })
}

async function submitForm() {
  await formRef.value.validate()
  if (dialog.isEdit) {
    await updateDept(form)
    ElMessage.success('修改成功')
  } else {
    await addDept(form)
    ElMessage.success('新增成功')
  }
  dialog.visible = false
  getList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除部门「${row.deptName}」吗？`, '提示', { type: 'warning' })
  await delDept(row.deptId)
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
