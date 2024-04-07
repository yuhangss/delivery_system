<!--布局框架文件-->
<template>
    <div>
        <el-config-provider: locale="locale">
            <Header>

            </Header>
            <!--body-->
            <div style="display:flex">
                <!--侧边栏-->
                <Aside>

                </Aside>
                <!--主体内容区域-->
                <router-view style="flex:1">
                </router-view>
            </div>
        </el-config-provider:>
    </div>
</template>

<script>
import Header from "../components/Header.vue";
import Aside from "../components/Aside.vue"
import locale from "element-plus/lib/locale/lang/zh-cn"

export default {
    name: "Layout",
    components: {
        Header,
        Aside
    },
    setup() {
        return {locale};
    },
}
</script>

<style scoped>

</style>
