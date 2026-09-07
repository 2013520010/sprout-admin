<template>
  <el-container class="layout-container">
    <el-aside width="210px" class="aside">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header class="header" height="56px">
        <Navbar />
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
</script>

<style scoped>
.layout-container {
  height: 100%;
}
.aside {
  background-color: #001529;
  overflow-x: hidden;
}
.header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 16px;
  z-index: 10;
}
.main {
  background-color: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}
</style>
