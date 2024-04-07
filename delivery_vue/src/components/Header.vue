<!--头部导航-->
<template>
    <div class="topNavigation" color="dark">
        <div class="headerMenu" color="dark">
            机器人送物后台管理系统
        </div>
        <div style="flex: 1"></div>
        <div style="width: 100px">
            <el-dropdown>
                <el-button>
                    {{ user.admin }}
                    <el-icon>
                        <arrow-down />
                    </el-icon>
                </el-button>

                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item>
                            <router-link to="/person" style="text-decoration:none">个人信息</router-link>
                        </el-dropdown-item>
                        <el-dropdown-item @click="quit">
                            退出系统
                        </el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
    import { ArrowDown } from '@element-plus/icons-vue'
    import router from "../router";
    export default {
        name: "Header",
        components: {
            ArrowDown
        },
        data() {
            return {
                user:{}
            }
        },
        created() {
            this.user = JSON.parse(localStorage.getItem("admin") || "{}");
        },
        methods: {
            quit() {
                localStorage.removeItem("admin")
                this.$router.push("/login");
            },
        }
    }
</script>

<style>
    .topNavigation {
        height: 50px;
        line-height: 50px;
        border-bottom: 1px solid #f6eaff;
        display: flex;
    }

    .headerMenu {
        width: 200px;
        padding-left: 30px;
        font-weight: bold;
        color: dodgerblue;
    }
</style>