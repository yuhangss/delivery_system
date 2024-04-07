<template>
  <div style="width: 100%">
      <!--    操作区域-->
      <div style="margin: 10px 5px">
            <el-button type="primary" @click="addUser" >新增</el-button>
            <el-popconfirm title="确定删除这些数据?" @confirm="delBatch()" >
                <template #reference>
                    <el-button  type="danger" class="ml-5">批量删除</el-button>
                </template>
            </el-popconfirm>
            <el-button type="primary" @click="exp" class="ml-5">导出</el-button>
            <el-upload
                action="http://localhost:9090/user/import"
                style="display:inline-block; margin:auto 10px"
                :show-file-list="false"
                accept=".xlsx"
                :on-success="handleExcelImportSuccess"
            >   
                <el-button type="primary" class="ml-5" color="green">导入</el-button>
            </el-upload>
            
        
      </div>
      <!--    搜索区域-->
      <div style="margin: 10px 5px">
          <el-input v-model="username" clearable type="text" placeholder="请输入用户名" style="width: 200px"></el-input>
          <el-input v-model="studentid" clearable type="text" placeholder="请输入学号" style="width: 200px"></el-input>
          <el-button @click="load" type="primary" style="margin: 0 5px">
              搜索
          </el-button>
          <el-button type="warning" @click="reset">重置</el-button>
      </div>
      <el-table v-model:data="tableData"
                stripe
                border
                style="width: 100%"
                @selection-change="handleSelectionChange">
          
          <el-table-column type="selection" width="30">

          </el-table-column>
          <el-table-column prop="id" label="id"/>
          <el-table-column prop="username" label="用户名"/>
          <el-table-column prop="studentid" label="学号"></el-table-column>
          <el-table-column prop="sex" label="性别"></el-table-column>
          <el-table-column prop="phone" label="电话"></el-table-column>
          <el-table-column prop="location" label="位置"></el-table-column>
          <!-- <el-table-column prop="password" label="密码"></el-table-column> -->
          <el-table-column label="操作">
              <template #default="scope">
                  <el-button size="mini" @click="updateUser(scope.row)">编辑</el-button>
                  <el-popconfirm title="确定删除该数据?" @confirm="deleteUser(scope.row.id)">
                      <template #reference>
                          <el-button size="mini" type="danger">删除</el-button>
                      </template>
                  </el-popconfirm>
              </template>
          </el-table-column>
      </el-table>
      <div class="demo-pagination-block">
          <el-pagination
                  v-model:currentPage="pageNum"
                  v-model:page-size="pageSize"
                  :page-sizes="[5, 10, 15, 20]"
                  :small="small"
                  :disabled="disabled"
                  :background="background"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="totals"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
          />
      </div>
      <div>
          <el-dialog
                  v-model="dialogVisible"
                  title="用户信息"
                  width="30%"
                  ty
                  :close-on-click-modal="false"
                  :close-on-press-escape="false"
                  :show-close="false"
                  draggable = "true">
              <el-form :model="form" :rules="rules" ref="ruleForm" label-width="80px">
                <el-upload
                    class="avatar-uploader"
                    accept=".jpg, .jpeg, .png, .JPG, .JPEG"
                    action="http://localhost:9090/file/upload"
                    :show-file-list="false"
                    :on-success="handleAvatarSuccess">
                    <img v-if="form.faceurl" :src="form.faceurl" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
                  <el-form-item label="用户名:" prop="username">
                      <el-input v-model.trim="form.username" style="width: 70%"/>
                  </el-form-item>
                  
                  <el-form-item label="学号:" prop="studentid">
                    <el-input v-model.trim="form.studentid" style="width: 70%"/>
                  </el-form-item>
                  <el-form-item label="电话:" prop="phone">
                    <el-input v-model.trim="form.phone" style="width: 70%"/>
                  </el-form-item>
                  <el-form-item label="密码:" prop="password">
                    <el-input v-model.trim="form.password" style="width: 70%"/>
                  </el-form-item>
                  <el-form-item label="位置:" prop="location">
                    <el-input v-model.trim="form.location" style="width: 70%"/>
                  </el-form-item>
                  
                  <el-form-item label="性别:" prop="sex">
                    <el-radio-group v-model="radio" class="ml-4" @change="change">
                    <el-radio label="1" size="small" >男</el-radio>
                    <el-radio label="2" size="small" >女</el-radio>
                    </el-radio-group>
                  </el-form-item>
                
                 
              </el-form>
              <template #footer>
                <span class="dialog-footer">
                  <el-button @click="deleteFace()">取消</el-button>
                  <el-button type="primary" @click="addFormSave('ruleForm')">确定</el-button>
                </span>
              </template>
          </el-dialog>
      </div>
  </div>
</template>
<script>
import {Search} from '@element-plus/icons-vue'

export default {
    name: 'UserView',
    components: {
        Search
    },

    data() {
        return {
            radio: '0',
            search: '',
            totals: 0,
            currentPage: '',
            dialogVisible: false,
            tableData: [],
            pageNum: 1,
            pageSize: 10,
            username: "",
            studentid: "",
            mutipleSelection: [],
            //判断编辑信息时url是否变化，变化时在点击取消按钮后要删除没有使用的传入的图片，否则不删除原本的图片
            changeUrl: false,
            faceUrl_Origin: '',

            form: {
                faceurl: '',
                username: '',
                studentid: '',
                phone: '',
                location: '',
                sex: ''
            },
            // 表单校验
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
                ],
                studentid: [
                    { required: true, message: '请输入学号', trigger: 'blur' },
                    { min: 5, max: 8, message: '长度在 5 到 8 个字符', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '请输入电话', trigger: 'blur' },
                    { min: 0, max: 15, message: '长度在 0 到 15 个字符', trigger: 'blur' }
                ],
                
            }
        }
    },

      

    created() {
        //请求分页查询数据
        this.load()
    },

    // mounted() {
    //     this.timer = setInterval(() => {
    //         this.load()
    //     }, 1000)
    // },

    // beforeUnmount() {
    //     clearInterval(this.timer)
    // },
      
    methods: {
        // 加载初始数据
        load(){
        this.request.get("/user/page", {
            params: {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                username: this.username,
                studentid: this.studentid
            }
        }).then(res => {
            console.log(res);
            this.tableData = res.records
            this.totals = res.total
            })
        },

        reset() {
            this.username = ""
            this.studentid = ""
            this.load()
        },

        //新增数据按钮
        addUser() {
            this.dialogVisible = true; //打开弹窗
            this.form = {}; //form对象置空
        },
        // 点击新增按钮后在弹出的表单点击确定按钮
        addFormSave(formName) {
            /**
            * 配置axios请求,请求的url和请求参数
            * .then（）链式操作，前一步完成后，结果传入
            */
            //表单校验
            this.radio = '0'
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.request.post("/user",this.form).then(res=>{
                        if (res){
                            this.$message({
                                message: '保存成功o(*￣▽￣*)ブ',
                                type: 'success'
                            });
                            this.dialogVisible = false;
                            this.load();
                            this.changeUrl = false;
                        }else {
                            this.$message({
                                message: '保存失败,,ԾㅂԾ,,',
                                type: 'error'
                            });
                            this.dialogVisible = false;
                        }
                    });
                } else {
                    this.$message({
                        message: '请确认表单(ˉ▽ˉ；)...',
                        type: 'warning'
                    });
                    return false;
                }
            });
            if(this.changeUrl) {
                this.request.delete("/file/del" , {
                    params: {
                        url: this.faceUrl_Origin
                    }
                }).then(res => {
                    console.log(res)
                })
                this.changeUrl = false
            }
        },

        //点击取消按钮时，删除磁盘上添加的图片
        deleteFace() {
            this.dialogVisible = false //关闭弹窗
            this.radio = '0'
            if(this.changeUrl) {
                this.request.delete("/file/del" , {
                    params: {
                        url: this.form.faceurl
                    }
                }).then(res => {
                    console.log(res)
                })
                this.changeUrl = false
            }
        },

        //删除数据按钮
        deleteUser(userId) {
            this.request.delete("user/"+userId,).then(res =>{
                if (res){
                    this.$message({
                        message: '删除成功o(*￣▽￣*)ブ',
                        type: 'success'
                    });
                    this.load();
                }else {
                    this.$message({
                        message: '删除失败,,ԾㅂԾ,,',
                        type: 'error'
                    });
                }
            });
        },

        handleSelectionChange(val) {
            console.log(val)
            this.mutipleSelection = val

        },

        delBatch() {
            let ids = this.mutipleSelection.map(v => v.id)
            this.request.delete("user/del/batch", {data: ids}).then(res =>{
                if (res){
                    this.$message({
                        message: '批量删除成功o(*￣▽￣*)ブ',
                        type: 'success'
                    });
                    this.load();
                }else {
                    this.$message({
                        message: '批量删除失败,,ԾㅂԾ,,',
                        type: 'error'
                    });
                }
            });
        },

        //编辑信息按钮
        updateUser(row) {
            this.form = JSON.parse(JSON.stringify(row));
            console.log(this.form.faceurl)
            if(this.form.sex == '男'){
                this.radio = '1'
            }
            else if(this.form.sex == '女'){
                this.radio = '2'
            }
            this.dialogVisible = true;
            this.load();
        },
        //导出数据按钮
        exp() {
            window.open("http://localhost:9090/user/export")
        },
        //导入数据按钮
        handleExcelImportSuccess() {
            this.$message.success("导入成功")
            this.load()
        },
        // 改变当前每页数据个数触发
        handleSizeChange() {
            this.load();
        },
        // 改变当前页码触发
        handleCurrentChange() {
            this.load();
        },
        //上传图片(人脸)成功后
        handleAvatarSuccess(res) {
            //传入图片，先标记为变化，点击取消或确定后再改变
            this.faceUrl_Origin = this.form.faceurl
            this.changeUrl = true
            this.form.faceurl = res
        },

        change() {
            if(this.radio == '1') {
                this.form.sex = '男'
            }
            else if(this.radio == '2') {
                this.form.sex = '女'
            }
            console.log(this.form.sex)
        }
    }
}
</script>

<style>
.avatar-uploader {
    text-align: center;
    padding-bottom: 10px;
}
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}
.avatar-uploader .el-upload:hover {
    border-color: #409eff;
}
.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 130px;
    height: 130px;
    line-height: 130px;
    text-align: center;
}
.avatar {
    width: 130px;
    height: 130px;
    display: block;
}
</style>