<template>
    <div style="width: 100%">
        <!--    操作区域-->
        <div style="margin: 10px 5px">
            <el-button type="primary" @click="addRobot">新增</el-button>
            <el-popconfirm title="确定删除这些数据?" @confirm="delBatch()">
              <template #reference>
                  <el-button  type="danger">批量删除</el-button>
              </template>
          </el-popconfirm>
        </div>
        <!--    搜索区域-->
        <div style="margin: 10px 5px">
            <el-input v-model="robot_uuid" clearable type="text" placeholder="请输入机器人id" style="width: 200px"></el-input>
            <el-input v-model="name" clearable type="text" placeholder="请输入机器人名称" style="width: 200px"></el-input>
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
            <el-table-column prop="robot_uuid" label="robot_uuid"/>
            <el-table-column prop="name" label="名称"/>
            <el-table-column prop="group_uuid" label="组"/>
            <el-table-column prop="mac" label="mac"/>
            <el-table-column prop="state" label="状态"></el-table-column>
            <el-table-column prop="ip" label="ip"></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button size="mini" @click="updateRobot(scope.row)">编辑</el-button>
                    <el-popconfirm title="确定删除该数据?" @confirm="deleteRobot(scope.row.id)">
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
                    :page-sizes="[2, 5, 10, 20]"
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
                    title="机器人信息"
                    width="40%">
                <el-form :model="form" :rules="rules" ref="ruleForm" label-width="80px">
                    <el-form-item label="robot_uuid:" prop="robot_uuid">
                      <el-input v-model.trim="form.robot_uuid" style="width: 70%"/>
                    </el-form-item>
                    <el-form-item label="机器人名称:" prop="name">
                      <el-input v-model.trim="form.name" style="width: 70%"/>
                    </el-form-item>
                    <el-form-item label="分组:" prop="group_uuid">
                      <el-input v-model.trim="form.group_uuid" style="width: 70%"/>
                    </el-form-item>
                    <el-form-item label="mac:" prop="mac">
                      <el-input v-model.trim="form.mac" style="width: 70%"/>
                    </el-form-item>
                    <el-form-item label="ip:" prop="ip">
                      <el-input v-model.trim="form.ip" style="width: 70%"/>
                    </el-form-item>
                    <el-form-item label="状态:" prop="state">
                      <el-input v-model.trim="form.state" style="width: 70%"/>
                    </el-form-item>
                   
                </el-form>
                <template #footer>
                  <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
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
      name: 'RobotView',
      components: {
          Search
      },
      data() {
          return {
              search: '',
              totals: 0,
              currentPage: '',
              pageSize: 10,
              dialogVisible: false,
              tableData: [],
              pageNum: 1,
              name: "",
              robot_uuid: "",
              mutipleSelection: [],
  
              form: {
                  robot_uuid: '',
                  name: '',
                  ip: '',
                  group_uuid: '',
                  mac: '',
                  state: '',
              },
              // 表单校验
              rules: {
                  // state: [
                  //     { required: true, message: '请输入状态', trigger: 'blur' },
                  //     { min: 0, max: 2, message: '长度在 0 到 2 个字符', trigger: 'blur' }
                  // ],
                  // location: [
                  //     { required: true, message: '请输入位置', trigger: 'blur' },
                  //     { min: 0, max: 15, message: '长度在 0 到 15 个字符', trigger: 'blur' }
                  // ],
                  
              }
          }
      },
  
        
  
      created() {
          //请求分页查询数据
          this.load()
      },

    //   mounted() {
    //     this.timer = setInterval(() => {
    //         this.load()
    //     }, 1000)
    //   },

    //   beforeUnmount() {
    //     clearInterval(this.timer)
    //   },
        
      methods: {
          // 加载初始数据
          load(){
          this.request.get("/robot/page", {
              params: {
                  pageNum: this.pageNum,
                  pageSize: this.pageSize,
                  name: this.name,
                  robot_uuid: this.robot_uuid
              }
          }).then(res => {
              console.log(res);
              this.tableData = res.records
              this.totals = res.total
              })
          },
  
          handleSizeChange(pageSize) {
              this.pageSize = pageSize
              console.log(pageSize)
              this.load()
          },
  
          handleCurrentChange(pageNum) {
              this.pageNum = pageNum
              console.log(pageNum)
              this.load()
          },
  
          reset() {
              this.name = ""
              this.robot_uuid = ""
              this.load()
          },
  
          //新增数据按钮
          addRobot() {
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
              this.$refs[formName].validate((valid) => {
                  if (valid) {
                      this.request.post("/robot",this.form).then(res=>{
                          if (res){
                              this.$message({
                                  message: '保存成功o(*￣▽￣*)ブ',
                                  type: 'success'
                              });
                              this.dialogVisible = false;
                              this.load();
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
          },
          //删除数据按钮
          deleteRobot(robotId) {
              this.request.delete("/robot/"+robotId,).then(res =>{
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
              this.request.delete("/robot/del/batch", {data: ids}).then(res =>{
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
          updateRobot(row) {
              this.form = JSON.parse(JSON.stringify(row));
              this.dialogVisible = true;
              this.load();
          },
          //导出数据按钮
          exportRobot() {
              this.load();
          },
          //导入数据按钮
          importRobot() {
  
          },
          // // 改变当前每页数据个数触发
          // handleSizeChange() {
          //     this.load();
          // },
          // // 改变当前页码触发
          // handleCurrentChange() {
          //     this.load();
          // }
      }
  }
  </script>