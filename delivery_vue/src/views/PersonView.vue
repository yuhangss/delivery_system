<template >
    <div style="width:500px; padding:20px; border:1px solid #ccc; ">
    <el-card style="width:500px">
        <el-form label-width="80px" size="small">
            <!-- <el-upload
                class="avatar-uploader"
                action="http://localhost:9090/file/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess">
                <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload> -->

            <el-form-item label="用户名">
                <el-input v-model="form.admin" disabled autocomplete="off" style="width:300px"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="save">确定</el-button>
            </el-form-item>
        </el-form>
    </el-card>
</div>
</template>

<script>

export default {
    name: "Person",
    data() {
        return {
            form: {},
            user: localStorage.getItem("admin") ? JSON.parse(localStorage.getItem("admin") || '') : {}
        }
    },

    created() {
        this.request.get("/admin/" + this.user.admin).then(res => {
            console.log(res);
            this.form = res
        })
    },

    methods: {
        async getAdmin() {
            return (await this.request.get("/admin/" + this.user.admin))
        },
        save() {
            this.request.post("/admin", this.form).then(res => {
                if (res.data){
                    this.$message({
                        message: '保存成功o(*￣▽￣*)ブ',
                        type: 'success'
                    });
                    this.getAdmin().then(res => {
                        res.token = JSON.parse(localStorage.getItem("admin")).token
                        localStorage.setItem("admin", JSON.stringify(res))
                    })
                    
                }else {
                    this.$message({
                        message: '保存失败,,ԾㅂԾ,,',
                        type: 'error'
                    });
                    this.dialogVisible = false;
                }
            })
        },

    }
}
</script>
