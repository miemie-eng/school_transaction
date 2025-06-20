<template>
  <div class="personal-center">
    <h2>个人中心</h2>
    
    <el-tabs type="border-card" v-model="activeTab">
		
		
		
      <!-- 修改个人信息 -->
      <el-tab-pane label="个人信息" name="profile">
        <el-form :model="userForm" label-width="100px" class="profile-form">
          <el-form-item label="用户名">
            <el-input v-model="userForm.uname"></el-input>
          </el-form-item>
          
          <el-form-item label="密码">
            <el-input 
              v-model="userForm.upwd" 
              type="password"
              show-password
              disabled
            ></el-input>
          </el-form-item>
          
          <el-form-item label="电话 ">
            <el-input v-model="userForm.uphone"></el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="showPasswordDialog">更新信息</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
	  
	  <!-- 密码验证对话框 -->
	  <el-dialog v-model="passwordDialogVisible" title="验证身份" width="400">
	    <div>
	      当前密码：
	      <el-input 
	        v-model="currentPassword"
	        type="password"
	        show-password
	        clearable
	        placeholder="请输入当前密码"
	        style="width: 240px"
	      /><br />
	      
	      <el-button type="primary" @click="verifyPassword">验证</el-button>
	    </div>
	  </el-dialog>
	  
	  <!-- 修改信息对话框 -->
	  <el-dialog v-model="updialogTableVisible" title="修改你的信息" width="500">
	    <el-form 
	      :model="upForm" 
	      :rules="registerRules" 
	      ref="updateFormRef"
	      label-width="80px"
	    >
	      <el-form-item label="用户名" prop="upuname">
	        <el-input v-model="upForm.upuname" />
	      </el-form-item>
	      
	      <el-form-item label="新密码" prop="upupwd">
	        <el-input
	          v-model="upForm.upupwd"
	          placeholder="请输入新密码"
	          type="password"
	          show-password
	        />
	      </el-form-item>
	      
	      <el-form-item label="电话" prop="upuphone">
	        <el-input v-model="upForm.upuphone" />
	      </el-form-item>
	      
	      <el-form-item>
	        <el-button type="primary" @click="upuser">修改信息</el-button>
	      </el-form-item>
	    </el-form>
	  </el-dialog>
      
      <!-- 购买记录 -->
      <el-tab-pane label="交易记录" name="purchaseHistory">
        <el-table :data="transactionHistory" style="width: 100%" v-loading="loading">
          <el-table-column prop="product_name" label="商品名称" width="180"></el-table-column>
          <el-table-column prop="price" label="价格" width="120"></el-table-column>
          <el-table-column prop="seller_name" label="卖家" width="150"></el-table-column>
          <el-table-column prop="buyer_name" label="买家" width="150"></el-table-column>
          <el-table-column prop="purchase_time" label="交易时间" width="200">
            <template #default="scope">
              {{ formatDate(scope.row.purchase_time) }}
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="totalItems"
            :page-size="pageSize"
            v-model:current-page="currentPage"
            @current-change="handlePageChange"
          />
        </div>
        
        <div v-if="transactionHistory.length === 0 && !loading" class="empty-history">
          暂无交易记录
        </div>
      </el-tab-pane>
	  <!-- 交易统计标签页 -->
	        <el-tab-pane label="交易统计" name="transactionChart">
	          <!-- 图表容器 -->
	          <div v-if="activeTab === 'transactionChart'">
	            <div id="transactionChart" style="height: 500px;"></div>
	            
	            <div v-if="transactionData.length === 0" class="empty-chart">
	              暂无交易统计数据
	            </div>
	          </div>
	        </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from 'vue';
import axios from 'axios';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { useUserStore } from '../stores/my-user';
import * as echarts from 'echarts';
import { nextTick } from 'vue';


const transactionData = ref<{ date: string; count: number }[]>([]);
const store = useUserStore();
const activeTab = ref('profile');
const loading = ref(false);

let passwordDialogVisible = ref(false);
let updialogTableVisible = ref(false);
let currentPassword = ref('');
const updateFormRef = ref<FormInstance>();

// 交易记录数据
const transactionHistory = ref([]);
const totalItems = ref(0);
const pageSize = ref(4);
const currentPage = ref(1);

// 用户表单数据
const userForm = ref({
  uid: store.uid,
  uname: store.uname,
  upwd: store.upwd,
  uphone: store.uphone,
});

// 修改表单数据
const upForm = reactive({
  upuname: store.uname, // 初始化为当前用户名
  upupwd: '', // 密码默认为空
  upuphone: store.uphone // 初始化为当前电话
});


// 表单验证规则 - 修改为可选
const registerRules = reactive<FormRules>({
  upuname: [
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  upupwd: [
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  upuphone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
});

// 在onMounted中调用
onMounted(() => {
  loadTransactionHistory();
});

// 格式化日期
function formatDate(dateStr: string) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
}

// 打开密码验证对话框
function showPasswordDialog() {
  passwordDialogVisible.value = true;
  currentPassword.value = '';
}

// 验证密码
function verifyPassword() {
  if (currentPassword.value === store.upwd) {
    passwordDialogVisible.value = false;
    openUpdateDialog();
  } else {
    ElMessage.error("密码不正确");
  }
}

// 打开修改对话框
function openUpdateDialog() {
  updialogTableVisible.value = true;
  
  // 重置表单数据为当前值
  upForm.upuname = store.uname;
  upForm.upupwd = '';
  upForm.upuphone = store.uphone;
}

// 修改用户信息
function upuser() {
  updateFormRef.value?.validate((valid) => {
    if (valid) {
      // 创建更新对象，只包含修改的字段
      const updateData: any = {
        uid: store.uid
      };
      
      // 检查并添加修改的字段
      if (upForm.upuname !== store.uname) {
        updateData.uname = upForm.upuname;
      }
      if (upForm.upupwd && upForm.upupwd !== store.upwd) {
        updateData.upwd = upForm.upupwd;
      }
      if (upForm.upuphone !== store.uphone) {
        updateData.uphone = upForm.upuphone;
      }
      
      // 如果没有修改任何字段
      if (Object.keys(updateData).length === 1) {
        ElMessage.warning("没有修改任何信息");
        return;
      }
      
      axios.post("http://localhost:8080/updater", updateData)
        .then(res => {
          if (res.data.success) {
            ElMessage.success(res.data.message);
            updialogTableVisible.value = false;
            
            // 更新store中的用户信息 - 只更新实际修改的字段
            store.setUserInfo(
              store.uid,
              updateData.upwd || store.upwd, // 使用新密码或保留原密码
              updateData.uname || store.uname, // 使用新用户名或保留原用户名
              updateData.uphone || store.uphone // 使用新电话或保留原电话
            );
            
            // 更新个人中心界面的显示
            userForm.value.uname = updateData.uname || store.uname;
            userForm.value.upwd = updateData.upwd || store.upwd;
            userForm.value.uphone = updateData.uphone || store.uphone;
          } else {
            ElMessage.error(res.data.message);
          }
        })
        .catch(error => {
          console.error("更新用户信息失败:", error);
          ElMessage.error("更新失败，请稍后重试");
        });
    }
  });
}

// 加载交易记录
async function loadTransactionHistory() {
  try {
    loading.value = true;
    const response = await axios.get("http://localhost:8080/getUserTransactionHistory", {
      params: { 
        uid: store.uid,
        page: currentPage.value,
        pageSize: pageSize.value
      }
    });
	
	// 添加调试日志
	    console.log("交易记录响应:", response.data);
    
    if (response.data.success) {
      transactionHistory.value = response.data.data;
      totalItems.value = response.data.total;
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    console.error("加载交易记录失败:", error);
    ElMessage.error("加载交易记录失败，请稍后重试");
  } finally {
    loading.value = false;
  }
}
// 处理分页变化
function handlePageChange(newPage: number) {
  currentPage.value = newPage;
  loadTransactionHistory();
}

// 监听activeTab变化，当切换到交易统计时加载数据
watch(activeTab, (newTab) => {
  if (newTab === 'transactionChart' && transactionData.value.length === 0) {
    loadTransactionChartData();
  } else if (newTab === 'transactionChart') {
    // 如果已有数据，确保图表重新渲染
    nextTick(() => renderChart());
  }
});

// 新增方法：加载交易图表数据
async function loadTransactionChartData() {
  try {
    loading.value = true;
    const response = await axios.get(
      "http://localhost:8080/getTransactionCountLastFourDays",
      { params: { uid: store.uid } }
    );
    
    if (response.data.success) {
      transactionData.value = response.data.data;
      nextTick(() => renderChart()); // 确保DOM更新后渲染图表
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    console.error("加载交易图表数据失败:", error);
    ElMessage.error("加载交易统计数据失败");
  } finally {
    loading.value = false;
  }
}

// 新增方法：渲染图表
function renderChart() {
  const chartDom = document.getElementById('transactionChart');
  if (!chartDom) return;
  
  // 先销毁已有图表实例
  const existingChart = echarts.getInstanceByDom(chartDom);
  if (existingChart) {
    echarts.dispose(existingChart);
  }
  
  const myChart = echarts.init(chartDom);
  const option = {
    title: {
      text: '最近四天交易统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>交易数量: {c}'
    },
    xAxis: {
      type: 'category',
      data: transactionData.value.map(item => item.date),
      axisLabel: {
        interval: 0,
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '交易数量',
      minInterval: 1 // 确保Y轴显示整数
    },
    series: [
      {
        name: '交易量',
        type: 'bar',
        data: transactionData.value.map(item => item.count),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        },
        label: {
          show: true,
          position: 'top'
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    animation: true // 添加动画效果
  };
  
  myChart.setOption(option);
  
  // 响应窗口大小变化
  window.addEventListener('resize', () => myChart.resize());
}

</script>



<style scoped>
	/* 新增空图表提示样式 */
	.empty-chart {
	  text-align: center;
	  padding: 40px;
	  color: #999;
	  font-size: 16px;
	}
.personal-center {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-form {
  max-width: 500px;
  margin: 20px auto;
}

.empty-history {
  text-align: center;
  padding: 40px;
  color: #999;
  font-size: 16px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>

