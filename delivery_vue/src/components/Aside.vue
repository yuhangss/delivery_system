<!--左侧边栏-->
<template>
    <nav>
        <el-row class="tac" >
            <!--加入outer实现导航栏点击跳转，index项值为对应的路由-->
            <el-menu
                router
                style="width:200px; min-height: 90vh;"
                active-text-color="#ffd04b"
                class="el-menu"
                default-active="home"
                text-color="black"
                >
                <el-menu-item index="home">
                  <template #title>
                      <el-icon><HomeFilled /></el-icon>
                    <span>
                        主页
                    </span>
                  </template>
                </el-menu-item>
                <el-sub-menu index="系统管理">
                    <template #title>
                      <el-icon><Menu /></el-icon>
                        <span>
                            系统管理
                        </span>
                    </template>
                    <el-menu-item index="user">
                      <template #title>
                        <el-icon><Avatar /></el-icon>
                        <span>用户管理</span>
                      </template>
                    </el-menu-item>
                    <el-menu-item index="robot">
                      <template #title>
                        <el-icon><UserFilled/></el-icon>
                        <span>机器人管理</span>
                      </template>
                    </el-menu-item>
                    <el-menu-item index="order">
                      <template #title>
                        <el-icon><List /></el-icon>
                        <span>订单管理</span>
                      </template>
                     </el-menu-item>
                </el-sub-menu>
                <el-menu-item index="login">
                  <template #title>
                    <el-icon><HelpFilled /></el-icon>
                    <span>
                      登录
                    </span>
                  </template>
                </el-menu-item>
            </el-menu>
        </el-row>
    </nav>
</template>

<script>
import { Location, Grid, Document, Setting, User } from '@element-plus/icons-vue'
import Home from "@/views/Home";

export default {
    name: "Aside",
    components: {
      Home,
        Location,
        Grid,
        Document,
        Setting,
        User
    },
}
</script>

<style scoped>
.el-menu{
  background: linear-gradient();
}
</style>
