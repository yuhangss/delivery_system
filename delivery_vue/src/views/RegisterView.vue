<template>
  <div class="contanier">
    <div class="loginArea">
      <h2 style="text-align: center;padding: 10px 0">用户注册</h2>
      <el-form :model="registerForm" :rules="rules" ref="ruleForm">
        <el-form-item label="用户名:" prop="username">
          <el-input v-model.trim="registerForm.username" clearable></el-input>
        </el-form-item>
        <el-form-item label="密 码:" prop="password">
          <el-input type="password" v-model.trim="registerForm.password" show-password clearable></el-input>
        </el-form-item>
        <el-form-item label="确认密码:" prop="againPassword">
          <el-input type="password" v-model.trim="registerForm.againPassword" show-password
                    clearable></el-input>
        </el-form-item>
        <el-form-item label="昵 称:" prop="nickname">
          <el-input type="text" v-model.trim="registerForm.nickname" clearable></el-input>
        </el-form-item>
        <el-form-item label="年 龄:" prop="age">
          <el-input type="number" v-model.trim="registerForm.age" clearable></el-input>
        </el-form-item>
        <el-form-item label="性 别:" prop="sex">
          <el-checkbox-group v-model="registerForm.sex">
            <el-radio label="男" v-model="registerForm.sex"/>男
            <el-radio label="女" v-model="registerForm.sex"/>女
            <el-radio label="未知" v-model="registerForm.sex"/>未知
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="地 址:" prop="address">
          <el-input v-model.lazy="registerForm.address" type="textarea"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="registerSure('ruleForm')" style="width: 100%" type="primary">确 认</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import {User, Key, Lock} from '@element-plus/icons-vue'
import request from "../utils/request";

export default {
  name: "RegisterView",
  components: {
    User, Key, Lock
  },
  data() {
    return {
      registerForm: {
        username: '',
        password: '',
        againPassword: '',
        nickname: '',
        age: '',
        sex: '',
        address: ''
      },
      // 表单校验
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
          {min: 2, max: 11, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '请输入密码', trigger: 'blur'},
          {min: 3, max: 11, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        againPassword: [
          {required: true, message: '请再次输入密码', trigger: 'blur'},
          {min: 3, max: 11, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        nickname: [
          {required: true, message: '请输入昵称', trigger: 'blur'},
          {min: 2, max: 11, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        age: [
          {required: true, message: '请输入年龄', trigger: 'blur'},
          {min: 1, max: 3, message: '长度在 3 到 5 个字符', trigger: 'blur'}
        ],
        sex: [
          {required: true, message: '请选择性别', trigger: 'change'}
        ],
        address: [
          {required: true, message: '请输入地址', trigger: 'blur'},
          {min: 2, message: '长度要大于2个字符', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    //登录按钮
    registerSure(formName) {
      if (this.registerForm.password != this.registerForm.againPassword) {
        this.$message({
          message: '两次密码不一致(ˉ▽ˉ；)..',
          type: 'warning'
        });
        return;
      }
      /**
       * 配置axios请求,请求的url和请求参数
       * .then（）链式操作，前一步完成后，结果传入
       */
      //表单校验
      this.$refs[formName].validate((valid) => {
        if (valid) {
          request.post("/user/register", this.registerForm).then(res => {
            if (res.code == "0") {
              this.$message({
                message: '注册成功o(*￣▽￣*)ブ',
                type: 'success'
              });
              //注册成功，跳转到登陆界面
              this.$router.push("/login")
            } else {
              this.$message({
                message: "注册失败,,ԾㅂԾ,," + res.msg,
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
  }
}
</script>

<style scoped>
.contanier {
  width: 100%;
  height: 100%;
  overflow: hidden;
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