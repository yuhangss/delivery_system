<template>
  <div class="contanier">
      <div class="loginArea" style=" margin:200px auto; width: 350px; height: 300px;">
          <h2 style="text-align: center;padding: 10px 0;">管理系统登录</h2>
          <el-form :model="loginForm" :rules="rules" ref="ruleForm" >
              <el-form-item label="用户名:" prop="admin" style="margin: 30px 40px">
                  <el-input style="height: 30px; width:200px;" v-model.trim="loginForm.admin" clearable ></el-input>
              </el-form-item>
              <el-form-item label="密 码:" prop="password" style="margin: 30px 50px">
                  <el-input style="height: 30px; width:200px;" type="password" v-model.trim="loginForm.password" show-password clearable ></el-input>
              </el-form-item>
              <el-form-item>
                  <el-button @click="login('ruleForm')" style="width: 80px; margin:auto 150px" type="primary">登 录</el-button>
                  <!-- <el-button @click="register" style="width: 80px;margin-left: 0px" type="primary">注 册</el-button> -->
              </el-form-item>
          </el-form>
      </div>
  </div>
</template>

<script>
  import {User, Key, Lock} from '@element-plus/icons-vue'

  export default {
      name: "LoginView",
      components: {
          User, Key, Lock
      },
      data() {
          return {
              loginForm: {
                //   admin: '',
                //   password: '',
              },
              // 表单校验
              rules: {
                  admin: [
                      {required: true, message: '请输入账号', trigger: 'blur'},
                      {min: 3, max: 11, message: '长度在 3 到 5 个字符', trigger: 'blur'}
                  ],
                  password: [
                      {required: true, message: '请输入密码', trigger: 'blur'},
                      {min: 3, max: 11, message: '长度在 3 到 5 个字符', trigger: 'blur'}
                  ]
              }
          }
      },
      methods: {
          //登录按钮
          login(formName) {
              /**
               * 配置axios请求,请求的url和请求参数
               * .then（）链式操作，前一步完成后，结果传入
               */
              //表单校验
              this.$refs[formName].validate((valid) => {
                  if (valid) {
                      this.request.post("/admin/login", this.loginForm).then(res => {
                          if (res.code === "200") {
                              this.$message({
                                  message: '登录成功o(*￣▽￣*)ブ',
                                  type: 'success'
                              });
                              //缓存用户信息，将登录的用户信息存入到localStorage
                              localStorage.setItem("admin",JSON.stringify(res.data));
                              //登陆成功页面跳转,跳转到到根目录
                              this.$router.push("/user")
                          } else {
                              this.$message({
                                  message: "登录失败,,ԾㅂԾ,,"+res.msg,
                                  type: 'error'
                              });
                          }
                      });
                  } else {
                      this.$message({
                          message: '请确认信息(ˉ▽ˉ；)...',
                          type: 'warning'
                      });
                      return false;
                  }
              });
          },
          //注册按钮
          register() {
              this.$router.push("/register")
          },
      }
  }
</script>

<style scoped>
  .contanier {
      height: 100vh;
      overflow: hidden;
      background-image: linear-gradient(to bottom right, #FC466B, #3F5EFB);
  }

  .loginArea {
      width: 400px;
      border-radius: 30px;
      background-clip: border-box;
      margin: 200px auto;
      padding: 15px 35px 15px 35px;
      background: #d6e8ee;
      border: 3px solid #e6fffc;
      box-shadow: 0 0 30px #c5c7ff;
  }
</style>