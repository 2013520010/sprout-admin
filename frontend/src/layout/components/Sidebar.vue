<template>
  <div class="sidebar">
    <div class="logo">
      <span class="logo-text">🌱 新芽 Admin</span>
    </div>
    <el-menu
      :default-active="activeMenu"
      :collapse="false"
      background-color="#001529"
      text-color="rgba(255,255,255,0.65)"
      active-text-color="#fff"
      router
      unique-opened
    >
      <template v-for="item in menus" :key="item.path || item.title">
        <el-sub-menu v-if="item.children" :index="item.title">
          <template #title>
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </template>
          <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
            <el-icon><component :is="child.icon" /></el-icon>
            <span>{{ child.title }}</span>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item v-else :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menus = [
  { path: '/dashboard', title: '首页', icon: 'HomeFilled' },
  {
    title: '系统管理',
    icon: 'Setting',
    children: [
      { path: '/system/user', title: '用户管理', icon: 'User' },
      { path: '/system/role', title: '角色管理', icon: 'UserFilled' },
      { path: '/system/menu', title: '菜单管理', icon: 'Menu' },
      { path: '/system/dept', title: '部门管理', icon: 'OfficeBuilding' }
    ]
  },
  {
    title: '日志管理',
    icon: 'Document',
    children: [
      { path: '/monitor/loginlog', title: '登录日志', icon: 'Document' },
      { path: '/monitor/operlog', title: '操作日志', icon: 'Tickets' }
    ]
  },
  {
    title: '开发工具',
    icon: 'Cpu',
    children: [{ path: '/tool/gen', title: '代码生成', icon: 'Cpu' }]
  }
]

const activeMenu = computed(() => route.path)
</script>

<style scoped>
.sidebar {
  height: 100%;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}
.sidebar :deep(.el-menu) {
  border-right: none;
}
</style>
