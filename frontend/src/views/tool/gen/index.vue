<template>
  <div>
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="表名称">
          <el-input v-model="query.tableName" placeholder="请输入表名称" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Download" @click="openImport">导入表</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="list" v-loading="loading">
        <el-table-column prop="tableId" label="编号" width="70" />
        <el-table-column prop="tableName" label="表名称" width="180" />
        <el-table-column prop="tableComment" label="表描述" show-overflow-tooltip />
        <el-table-column prop="className" label="实体类" width="130" />
        <el-table-column prop="moduleName" label="模块名" width="100" />
        <el-table-column prop="functionAuthor" label="作者" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" icon="Download" @click="handleDownload(row)">生成代码</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <!-- 导入表弹窗 -->
    <el-dialog v-model="importDialog.visible" title="导入数据库表" width="640px">
      <el-table :data="importDialog.dbTables" v-loading="importDialog.loading" height="400" @selection-change="(rows) => (importDialog.selected = rows)">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="tableName" label="表名称" width="200" />
        <el-table-column prop="tableComment" label="表描述" show-overflow-tooltip />
      </el-table>
      <template #footer>
        <el-button @click="importDialog.visible = false">取消</el-button>
        <el-button type="primary" :disabled="!importDialog.selected.length" @click="submitImport">导入</el-button>
      </template>
    </el-dialog>

    <!-- 编辑配置弹窗 -->
    <el-dialog v-model="editDialog.visible" title="编辑生成配置" width="720px">
      <el-form :model="editDialog.form" label-width="90px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="实体类名称">
              <el-input v-model="editDialog.form.className" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="包路径">
              <el-input v-model="editDialog.form.packageName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模块名">
              <el-input v-model="editDialog.form.moduleName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务名">
              <el-input v-model="editDialog.form.businessName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="功能名称">
              <el-input v-model="editDialog.form.functionName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="editDialog.form.functionAuthor" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">字段配置</el-divider>
      <el-table :data="editDialog.form.columns" max-height="320" size="small">
        <el-table-column prop="columnName" label="列名" width="140" />
        <el-table-column prop="columnComment" label="描述" width="140" />
        <el-table-column label="Java类型" width="130">
          <template #default="{ row }">
            <el-select v-model="row.javaType" size="small">
              <el-option v-for="t in javaTypes" :key="t" :label="t" :value="t" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="字段名" width="130">
          <template #default="{ row }">
            <el-input v-model="row.javaField" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="列表" width="60">
          <template #default="{ row }">
            <el-switch v-model="row.isList" active-value="1" inactive-value="0" />
          </template>
        </el-table-column>
        <el-table-column label="查询" width="60">
          <template #default="{ row }">
            <el-switch v-model="row.isQuery" active-value="1" inactive-value="0" />
          </template>
        </el-table-column>
        <el-table-column label="插入" width="60">
          <template #default="{ row }">
            <el-switch v-model="row.isInsert" active-value="1" inactive-value="0" />
          </template>
        </el-table-column>
        <el-table-column label="编辑" width="60">
          <template #default="{ row }">
            <el-switch v-model="row.isEdit" active-value="1" inactive-value="0" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="editDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewDialog.visible" title="代码预览" width="760px" top="5vh">
      <el-tabs v-model="previewDialog.activeTab">
        <el-tab-pane v-for="(code, name) in previewDialog.codes" :key="name" :label="name" :name="name">
          <pre class="code-pre">{{ code }}</pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import {
  listDbTable, importTable, listGenTable, getGenTable, updateGenTable, delGenTable, previewCode
} from '@/api/gen'
import { getToken } from '@/utils/auth'

const loading = ref(false)
const list = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  tableName: ''
})

const javaTypes = ['Long', 'Integer', 'String', 'BigDecimal', 'Double', 'Float', 'LocalDateTime', 'LocalDate', 'Boolean']

const importDialog = reactive({
  visible: false,
  loading: false,
  dbTables: [],
  selected: []
})

const editDialog = reactive({
  visible: false,
  form: {}
})

const previewDialog = reactive({
  visible: false,
  activeTab: '',
  codes: {}
})

function getList() {
  loading.value = true
  listGenTable(query)
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
  query.tableName = ''
  query.pageNum = 1
  getList()
}

async function openImport() {
  importDialog.visible = true
  importDialog.loading = true
  importDialog.dbTables = await listDbTable()
  importDialog.loading = false
}

async function submitImport() {
  const names = importDialog.selected.map((t) => t.tableName)
  await importTable(names)
  ElMessage.success('导入成功')
  importDialog.visible = false
  getList()
}

async function handleEdit(row) {
  editDialog.form = await getGenTable(row.tableId)
  editDialog.visible = true
}

async function submitEdit() {
  await updateGenTable(editDialog.form)
  ElMessage.success('保存成功')
  editDialog.visible = false
  getList()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除表「${row.tableName}」的生成配置吗？`, '提示', { type: 'warning' })
  await delGenTable([row.tableId])
  ElMessage.success('删除成功')
  getList()
}

async function handlePreview(row) {
  const codes = await previewCode(row.tableId)
  previewDialog.codes = codes
  previewDialog.activeTab = Object.keys(codes)[0]
  previewDialog.visible = true
}

async function handleDownload(row) {
  const res = await axios({
    url: `/api/tool/gen/download/${row.tableId}`,
    method: 'get',
    responseType: 'blob',
    headers: { Authorization: 'Bearer ' + getToken() }
  })
  const blob = new Blob([res.data], { type: 'application/zip' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'sprout-code.zip'
  a.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('代码生成成功，已开始下载')
}

onMounted(getList)
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.code-pre {
  background-color: #f6f8fa;
  padding: 12px;
  border-radius: 4px;
  overflow: auto;
  max-height: 60vh;
  font-size: 13px;
  line-height: 1.5;
}
</style>
