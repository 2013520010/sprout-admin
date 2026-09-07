<template>
  <div>
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="操作人员">
          <el-input v-model="query.operName" placeholder="请输入操作人员" clearable @clear="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <div class="toolbar">
        <el-button type="danger" icon="Delete" :disabled="!selection.length" @click="handleDelete">删除</el-button>
        <el-button type="warning" icon="DeleteFilled" @click="handleClean">清空</el-button>
      </div>

      <el-table :data="list" v-loading="loading" @selection-change="(rows) => (selection = rows)">
        <el-table-column type="selection" width="50" />
        <el-table-column prop="operId" label="日志ID" width="80" />
        <el-table-column prop="title" label="操作模块" width="110" />
        <el-table-column prop="operName" label="操作人员" width="100" />
        <el-table-column prop="requestMethod" label="请求方式" width="90" />
        <el-table-column prop="operUrl" label="请求地址" show-overflow-tooltip />
        <el-table-column prop="operIp" label="操作IP" width="140" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="costTime" label="耗时(ms)" width="90" />
        <el-table-column prop="operTime" label="操作时间" width="170" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" icon="View" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 详情弹窗 -->
    <el-dialog v-model="detail.visible" title="操作日志详情" width="640px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作模块">{{ detail.row.title }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ detail.row.method }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">{{ detail.row.operParam }}</el-descriptions-item>
        <el-descriptions-item label="返回结果">{{ detail.row.jsonResult }}</el-descriptions-item>
        <el-descriptions-item label="错误消息">{{ detail.row.errorMsg }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOperLog, delOperLog, cleanOperLog } from '@/api/log'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const selection = ref([])

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  operName: ''
})

const detail = reactive({
  visible: false,
  row: {}
})

function getList() {
  loading.value = true
  listOperLog(query)
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
  query.operName = ''
  query.pageNum = 1
  getList()
}

async function handleDelete() {
  await ElMessageBox.confirm('确定删除选中的日志吗？', '提示', { type: 'warning' })
  await delOperLog(selection.value.map((r) => r.operId).join(','))
  ElMessage.success('删除成功')
  getList()
}

async function handleClean() {
  await ElMessageBox.confirm('确定清空所有操作日志吗？', '提示', { type: 'warning' })
  await cleanOperLog()
  ElMessage.success('清空成功')
  getList()
}

function handleDetail(row) {
  detail.row = row
  detail.visible = true
}

onMounted(getList)
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
