<template>
  <div>
    <el-card>
      <div class="welcome">
        <h2>欢迎使用新芽 Sprout Admin 👋</h2>
        <p class="subtitle">现代化权限管理系统脚手架 · Spring Boot 3 + Vue 3 全栈项目</p>
      </div>

      <el-row :gutter="16" class="stats">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">RBAC</div>
            <div class="stat-label">权限模型</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">JWT</div>
            <div class="stat-label">无状态认证</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">AOP</div>
            <div class="stat-label">操作日志</div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-value">Velocity</div>
            <div class="stat-label">代码生成器</div>
          </el-card>
        </el-col>
      </el-row>

      <el-descriptions title="当前登录信息" :column="2" border class="info">
        <el-descriptions-item label="用户名">{{ userStore.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userStore.nickname }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-for="r in userStore.roles" :key="r" style="margin-right: 6px">{{ r }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="权限数量">{{ userStore.permissions.length }}</el-descriptions-item>
      </el-descriptions>

      <el-alert
        title="技术栈：Spring Boot 3 + MyBatis-Plus + Spring Security 6 + Redis + Vue 3 + Element Plus"
        type="success"
        :closable="false"
        style="margin-top: 16px"
      />
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>

<style scoped>
.welcome h2 {
  color: #303133;
  margin-bottom: 8px;
}
.subtitle {
  color: #909399;
  margin-bottom: 24px;
}
.stats {
  margin-bottom: 16px;
}
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #2a5298;
}
.stat-label {
  color: #909399;
  font-size: 13px;
  margin-top: 4px;
}
</style>
