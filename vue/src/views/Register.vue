<template>
  <div class="auth-container register-form">
    <!-- 头部装饰元素 -->
    <div class="form-header">
      <h2 class="form-title">宠物领养系统</h2>
      <div class="deco-circle"></div>
    </div>

    <!-- 注册表单 -->
    <el-form
        :model="user"
        :rules="rules"
        ref="userForm"
        class="auth-form"
        id="registerForm">

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

      <!-- 确认密码 -->
      <div class="input-group animate-slide delay-2">
        <i class="fas fa-lock"></i>
        <el-input
            size="medium"
            show-password
            v-model="user.confirmPassword"
            placeholder="请确认密码"
            :prefix-icon="''">
        </el-input>
      </div>

      <!-- 操作按钮组 -->
      <div class="action-group">
        <el-button
            type="primary"
            class="submit-btn"
            @click="register">
          立即注册 <i class="fas fa-arrow-right"></i>
        </el-button>

        <div class="extra-options">
          <el-button
              type="text"
              class="login-btn"
              @click="$router.push('/login')">
            <i class="fas fa-sign-in-alt"></i>
            返回登录
          </el-button>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    return {
      user: {},
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 10, message: '长度在 3 到 10 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: this.validatePass, trigger: 'blur' }
        ],
      }
    }
  },
  methods: {
    validatePass(rule, value, callback) {
      if (value !== this.user.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    },
    register() {
      this.$refs['userForm'].validate((valid) => {
        if (valid) {
          this.request.post("/user/register", this.user).then(res => {
            if(res.code === '200') {
              this.$message.success("注册成功")
              setTimeout(() => {
                this.$router.push('/login')
              }, 1500)
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      });
    }
  }
}
</script>

<style scoped>
/* 共用登录样式 */
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

/* 输入框组样式 */
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

/* 按钮样式 */
.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px !important;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(64,158,255,0.3);
}

/* 其他选项 */
.extra-options {
  margin-top: 1.5rem;
  display: flex;
  justify-content: flex-end;
}

.login-btn {
  font-size: 14px;
  padding: 0 8px;
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