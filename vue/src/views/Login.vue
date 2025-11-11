<template>
  <div class="auth-container login-form">
    <!-- 头部装饰元素 -->
    <div class="form-header">
      <h2 class="form-title">流浪动物系统</h2>
      <div class="deco-circle"></div>
    </div>

    <!-- 登录表单 -->
    <el-form
        :model="user"
        :rules="rules"
        ref="userForm"
        class="auth-form"
        id="loginForm">

      <!-- 用户名输入 -->
      <div class="input-group animate-slide">
        <i class="fas fa-user"></i>
        <el-input
            size="medium"
            v-model="user.username"
            placeholder="请输入账号"
            :prefix-icon="''">
        </el-input>
      </div>

      <!-- 密码输入 -->
      <div class="input-group animate-slide delay-1">
        <i class="fas fa-lock"></i>
        <el-input
            size="medium"
            show-password
            v-model="user.password"
            placeholder="请输入密码"
            :prefix-icon="''">
        </el-input>
      </div>

      <!-- 操作按钮组 -->
      <div class="action-group">
        <el-button
            type="primary"
            class="submit-btn"
            @click="login">
          立即登录 <i class="fas fa-arrow-right"></i>
        </el-button>

        <div class="extra-options">
          <el-button
              type="text"
              class="pwd-btn"
              @click="handlePass">
            <i class="fas fa-key"></i>
            忘记密码
          </el-button>
          <el-button
              type="text"
              class="reg-btn"
              @click="$router.push('/register')">
            <i class="fas fa-user-plus"></i>
            立即注册
          </el-button>
        </div>
      </div>
    </el-form>

    <!-- 密码找回弹窗 -->
    <el-dialog
        title="重置密码"
        :visible.sync="dialogFormVisible"
        custom-class="password-dialog"
        width="380px">

      <!-- 用户名 -->
      <div class="input-group animate-slide">
        <i class="fas fa-envelope"></i>
        <el-input
            v-model="pass.username"
            placeholder="用户名">
        </el-input>
      </div>

      <!-- 手机号 -->
      <div class="input-group animate-slide delay-1">
        <i class="fas fa-phone"></i>
        <el-input
            v-model="pass.phone"
            placeholder="手机号">
        </el-input>
      </div>

      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="passwordBack">立即重置</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {resetRouter, setRoutes} from "@/router";

export default {
  name: "Login",
  data() {
    return {
      user: {},
      pass: {},
      dialogFormVisible: false,
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ],
      }
    }
  },
  created() {
    // 重置路由
    resetRouter()
  },
  methods: {
    login() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {  // 表单校验合法
          this.request.post("/user/login", this.user).then(res => {
            if(res.code === '200') {
              localStorage.setItem("user", JSON.stringify(res.data))  // 存储用户信息到浏览器
              localStorage.setItem("menus", JSON.stringify(res.data.menus))  // 存储用户信息到浏览器

              // 动态设置当前用户的路由
              setRoutes()
              this.$router.push("/front/home")
              this.$message.success("登录成功")
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    },
    handlePass() {
      this.dialogFormVisible = true
      this.pass = {}
    },
    passwordBack() {
      this.request.put("/user/reset", this.pass).then(res => {
        if (res.code === '200') {
          this.$message.success("重置密码成功，新密码为：123，请尽快修改密码")
          this.dialogFormVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
/* 保持功能的核心样式修改 */
.auth-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #6B73FF 0%, #000DFF 100%);
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
  position: relative;
}

.form-title {
  font-size: 2.2rem;
  color: #fff;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.deco-circle {
  position: absolute;
  width: 60px;
  height: 60px;
  border: 3px solid rgba(255,255,255,0.2);
  border-radius: 50%;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
}

.auth-form {
  background: white;
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  width: 380px;
}

.input-group {
  position: relative;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
}

.input-group i {
  position: absolute;
  left: 15px;
  color: #999;
  z-index: 2;
}

::v-deep .el-input__inner {
  padding-left: 40px !important;
  height: 48px !important;
  border-radius: 8px !important;
  border: 1px solid #eee !important;
  transition: all 0.3s ease;
}

::v-deep .el-input__inner:focus {
  border-color: #409EFF !important;
  box-shadow: 0 2px 8px rgba(64,158,255,0.2);
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px !important;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.extra-options {
  margin-top: 1.5rem;
  display: flex;
  justify-content: space-between;
}

::v-deep .password-dialog {
  border-radius: 15px !important;
  overflow: hidden;
}

/* 动画效果 */
.animate-slide {
  opacity: 0;
  transform: translateY(20px);
  animation: slideIn 0.6s forwards;
}

.delay-1 { animation-delay: 0.2s; }
.delay-2 { animation-delay: 0.4s; }

@keyframes slideIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
