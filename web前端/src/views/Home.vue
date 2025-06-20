<template>
  <div class="common-layout">
    <el-container>
      <el-header>校园二手交易平台
	  <div class="user-menu">
	      <el-dropdown @command="handleCommand">
	        <span class="el-dropdown-link">
	          <el-icon :size="24" color="#333"><UserFilled /></el-icon>
	        </span>
	        <template #dropdown>
	          <el-dropdown-menu>
	            <el-dropdown-item command="logout">
	              <el-icon><SwitchButton /></el-icon>
	              退出登录
	            </el-dropdown-item>
	          </el-dropdown-menu>
	        </template>
	      </el-dropdown>
	    </div>
		</el-header>
      <el-container>
		  <el-aside width="200px">
		  	      <el-menu
		  	        default-active="2"
		  	        class="el-menu-vertical-demo"
		  			router>
		  			<el-menu-item index="/shop">
		  			  <el-icon><Shop /></el-icon>
		  			  <span>校园集市</span>
		  			</el-menu-item>
		  			<el-menu-item index="/myProduct" >
		  			  <el-icon><Box /></el-icon>
		  			  <span>我的商品</span>
		  			</el-menu-item>
					<el-menu-item index="/QA" >
					  <el-icon><MagicStick /></el-icon>
					  <span>校集咨询</span>
					</el-menu-item>
					<el-menu-item index="/talk" >
					  <el-icon><ChatLineRound /></el-icon>
					  <span>我的聊天</span>
					</el-menu-item>
		  			<el-menu-item index="/me">
		  			  <el-icon><UserFilled /></el-icon>
		  			  <span>个人中心</span>
		  			</el-menu-item>
		  			</el-menu>
		  	
		  </el-aside>
        
        <el-main>
			<router-view></router-view>
			</el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script setup lang="ts">
	import { useRouter } from 'vue-router'
	import { UserFilled, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
	
	const router = useRouter()
	
	// 处理下拉菜单命令
	const handleCommand = (command: string) => {
	  if (command === 'logout') {
		  localStorage.removeItem('token')
		  sessionStorage.removeItem('userInfo')
	    logout()
	  }
	}
const logout = () => {
	// 跳转到登录页面
	  router.push('/')
	    ElMessage.success('已成功退出登录')
	  }
</script>

<style scoped>
	      .el-header{
	        background-color: rgb(197.7, 225.9, 255);
	        color: #333;
	        text-align: center;
	        line-height: 60px;
	        font-size: 30px;
	      }
	      
	      .el-aside {
	        background-color: rgb(216.8, 235.6, 255);
	        color: #333;
	        text-align: center;
	        line-height: 200px;
	        height: 900px;
	      }
	      
	      .el-main {
	        background-color: rgb(235.9, 245.3, 255);
	        color: #333;
	        text-align: center;
	        line-height: 60px;
	      }
	      
	      body > .el-container {
	        margin-bottom: 40px;
			
	      }
		  .user-menu {
		    position: absolute;
		    top: 15px;
		    right: 20px;
		    cursor: pointer;
		  }
		  
		  .el-dropdown-link {
		    display: flex;
		    align-items: center;
		  }
		  
		  /* 调整头部布局 */
		  .el-header {
		    position: relative; /* 确保用户菜单相对于header定位 */
		  }
</style>
