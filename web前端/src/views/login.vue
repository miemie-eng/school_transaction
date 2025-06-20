<template>
<div class="login-container">
  <div class="box">
    <h2>Login in</h2>
    账号：<el-input
            v-model="uid"
            class="transparent-input"
            placeholder="请输入学号"
            clearable
          /><br /><br />
    密码： <el-input
             v-model="upwd"
             class="transparent-input"
             type="password"
             placeholder="请输入密码"
             show-password
            /><br /><br />
      <el-button 
        type="primary" 
        class="transparent-btn"
        @click="login">登录</el-button>
      <el-button 
        type="success" 
        class="transparent-btn register-btn"
        @click="showRegisterDialog = true">注册</el-button>
  </div>
  
  <!-- 注册对话框 - 使用普通输入框 -->
  <el-dialog 
    v-model="showRegisterDialog" 
    title="用户注册"
    width="500px"
    :close-on-click-modal="false">
    <el-form 
      ref="registerFormRef" 
      :model="registerForm" 
      :rules="registerRules"
      label-width="80px">
      
      <el-form-item label="用户名" prop="uname">
        <el-input v-model="registerForm.uname" placeholder="请输入用户名"/>
      </el-form-item>
      
      <el-form-item label="学号" prop="uid">
        <el-input v-model.number="registerForm.uid" placeholder="请输入学号" type="number"/>
      </el-form-item>
      
      <el-form-item label="密码" prop="upwd">
        <el-input 
          v-model="registerForm.upwd" 
          type="password" 
          placeholder="请输入密码"
          show-password/>
      </el-form-item>
      
      <el-form-item label="确认密码" prop="confirmUpwd">
        <el-input 
          v-model="registerForm.confirmUpwd" 
          type="password" 
          placeholder="请再次输入密码"
          show-password/>
      </el-form-item>
      
      <el-form-item label="手机号" prop="uphone">
        <el-input v-model="registerForm.uphone" placeholder="请输入手机号"/>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="showRegisterDialog = false">取消</el-button>
      <el-button type="primary" @click="register">注册</el-button>
    </template>
  </el-dialog>
</div>
</template>

<script setup lang="ts">
import { useUserStore } from '../stores/my-user';
let store=useUserStore()
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';

const router = useRouter()
const uid = ref("")
const upwd = ref("")
const showRegisterDialog = ref(false)
const registerForm = reactive({
  uname: '',
  uid: null as number | null,
  upwd: '',
  confirmUpwd: '',
  uphone: ''
})

const registerFormRef = ref<FormInstance>()

// 注册表单验证规则
const registerRules = reactive<FormRules>({
  uname: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  uid: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { type: 'number', message: '学号必须为数字值', trigger: 'blur' }
  ],
  upwd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmUpwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.upwd) {
          callback(new Error('两次输入密码不一致!'))
        } else {
          callback()
        }
      },
      trigger: 'blur' 
    }
  ],
  uphone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
})
    
function login(){
  let user = {
    uid: uid.value,
    upwd: upwd.value,//数据传输
  }
  axios.post("http://localhost:8080/login", user)//接口连接
  .then(res => {
    if(res.data){
      store.setUserInfo( res.data.uid,res.data.upwd,res.data.uname,res.data.uphone);
	  console.log(store.uid)
      ElMessage.success("登录成功")
      router.push("/home")
    }else{
      ElMessage.error("登录失败")
    }
  })
}

// 注册函数
function register() {
  registerFormRef.value?.validate((valid) => {
    if (valid) {
      const userData = {
        uid: registerForm.uid,
        uname: registerForm.uname,
        upwd: registerForm.upwd,
        uphone: registerForm.uphone
      }
      
      axios.post("http://localhost:8080/register", userData)
        .then(res => {
          if (res.data.success) {
            ElMessage.success("注册成功！")
            showRegisterDialog.value = false
            // 重置表单
            registerFormRef.value?.resetFields()
          } else {
            ElMessage.error(res.data.message)
          }
        })
        .catch(error => {
          ElMessage.error("注册失败: " + (error.response?.data?.message || error.message))
        })
    }
  })
}
</script>

<style scoped>
/* 原有样式保持不变 */
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('o.jpg') no-repeat center center fixed;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
}
.box {
  width: 350px;
  padding: 30px;
  border-radius: 15px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  margin-bottom: 30px;
}

/* 输入框优化 */
.transparent-input :deep(.el-input__inner) {
  background: transparent !important;
  border: 1px solid rgba(255, 255, 255, 0.4) !important;
  color: #000000 !important;
  height: 40px;
  transition: all 0.3s;
}

/* 输入框聚焦效果 */
.transparent-input :deep(.el-input__inner:focus) {
  border-color: rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.2) !important;
}

/* 按钮样式修改 */
.transparent-btn {
  width: 80% !important;
  display: block !important;
  margin: 0 auto !important;
  background: rgba(86, 140, 255, 0.7) !important;
  border: 1px solid rgba(255, 255, 255, 0.4) !important;
  height: 42px;
  font-size: 16px;
  letter-spacing: 2px;
  transition: all 0.3s !important;
  margin-top: 10px !important;
}

.transparent-btn:hover {
  background: rgba(86, 140, 255, 0.9) !important;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 注册按钮特殊样式 */
.register-btn {
  background: rgba(103, 194, 58, 0.7) !important;
}

.register-btn:hover {
  background: rgba(103, 194, 58, 0.9) !important;
}

</style>