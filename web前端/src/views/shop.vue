<template>
  <div class="flex gap-4 mb-4 flex-wrap">
    <div class="filter-item">
      <span class="filter-label">日期：</span>
      <el-date-picker
        v-model="day"
        type="date"
        placeholder="请选择商品上架日期"
        value-format="YYYY-MM-DD"
        style="width: 240px"
      />
    </div>
    
    <div class="filter-item">
      <span class="filter-label">种类：</span>
      <el-input
        v-model="type"
        style="width: 240px"
        placeholder="请输入产品种类"
         :prefix-icon="Search"
		 clearable
      />
    </div>
    
    <div class="filter-item">
      <span class="filter-label">名称：</span>
      <el-input
        v-model="name"
        style="width: 240px"
        placeholder="请输入产品名称"
         :prefix-icon="Search"
		 clearable
      />
    </div>
   
  </div>
  
  <el-button type="primary" :icon="Search" @click="search">搜索</el-button>
  
  <el-table :data="tableData" style="width: 100%">
    <el-table-column prop="pname" label="商品名字" width="150" />
	 <el-table-column prop="pdescription" label="商品描述" width="200" />
    <el-table-column prop="ptype" label="商品种类" width="120" />
    <el-table-column prop="pvalue" label="商品价格" width="120" />
   <el-table-column label="商品图片" width="200">
       <template #default="scope">
         <img 
           v-if="scope.row.pimage"
           :src="getImageUrl(scope.row.pimage)" 
           style="width: 100px; height: 100px; object-fit: cover;"
         />
         <span v-else>无图片</span>
       </template>
     </el-table-column>
   <el-table-column label="商品所属人" width="120">
       <template #default="scope">
         {{ getOwnerName(scope.row.puid) }}
       </template>
     </el-table-column>
	 
	 <el-table-column label="所属人电话" width="120">
	     <template #default="scope">
	       {{ getOwnerPhone(scope.row.puid) }}
	     </template>
	   </el-table-column>
	 
   <el-table-column label="Operations" min-width="120">
         <template #default="scope">
			 <el-button
			   link 
			   type="primary" 
			   size="small" 
			   @click="talk(scope.row.puid)"
			 			:disabled="isButtonDisabled(scope.row)"
			 >
			   联系卖家
			 </el-button>
           <el-button 
             link 
             type="primary" 
             size="small" 
             @click="buy(scope.row)"
             :disabled="isButtonDisabled(scope.row)"
           >
             购买
           </el-button>
           <span v-if="isButtonDisabled(scope.row)" class="disabled-hint">
             {{ getDisabledReason(scope.row) }}
           </span>
		  
		 
				   
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
  
  <el-dialog v-model="dialogTableVisible" title="您是否要购买本产品？" width="400">
    <div>
      密码：<el-input
        v-model="inputPassword"
        style="width: 240px"
        placeholder="请输入密码"
        clearable
        type="password"
        show-password
      /><br />
      <el-button type="primary" @click="determin">确认购买</el-button>
    </div>
  </el-dialog>
  
  <!-- 新增联系卖家对话框 -->
    <el-dialog v-model="talkDialogVisible" title="联系卖家" width="500">
      <el-input
        v-model="messageContent"
        type="textarea"
        :rows="4"
        placeholder="请输入消息内容"
      ></el-input>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="talkDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="sendMessageToSeller">发送</el-button>
        </span>
      </template>
    </el-dialog>
  
</template>

<script setup lang="ts">
import { ref } from 'vue';
import axios from'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../stores/my-user';
import { onMounted } from 'vue'; 
import { Search } from '@element-plus/icons-vue'

const store = useUserStore();
const pdescription = ref("");

// 表格数据和搜索条件
const tableData = ref([]);
const day = ref('');
const type = ref('');
const name = ref('');
// 删除状态变量
// 分页相关
const currentPage = ref(1);
const pageSize = ref(4);
const totalItems = ref(0);

// 图片上传
const pimage = ref('');

// 购买相关
const dialogTableVisible = ref(false);
const inputPassword = ref('');
const selectedProduct = ref({
  pid: 0,
  pname: ""
});

 

const ownerNameCache = ref({});
const ownerPhoneCache = ref({});


// 新增变量
const talkDialogVisible = ref(false);
const messageContent = ref('');
const currentSellerId = ref(0);



function getOwnerName(uid) {
  if (!uid) return "";
  console.log("...")
  if (ownerNameCache.value[uid]) {
	
    return ownerNameCache.value[uid];
  }
  
  axios.get(`http://localhost:8080/user/name/${uid}`)
    .then(res => {
		
      ownerNameCache.value[uid] = res.data.name;
	  
    })
    .catch(() => {
      ownerNameCache.value[uid] = "未知用户";
    });
    
  return "加载中...";
}
function getOwnerPhone(uid) {
  if (!uid) return "";
  
  if (ownerPhoneCache.value[uid]) {
	  console.log(ownerPhoneCache.value[uid])
    return ownerPhoneCache.value[uid];
  }
  
  axios.get(`http://localhost:8080/user/phone/${uid}`)
    .then(res => {
		
      ownerPhoneCache.value[uid] = res.data.phone;
    })
    .catch(() => {
      ownerPhoneCache.value[uid] = "未知用户";
    });
    
  return "加载中...";
}

onMounted(() => {
  search();
});

// 搜索函数
function search() {
  const params = {
    day: day.value,
    type: type.value,
    name: name.value,
    page: currentPage.value,
    pageSize: pageSize.value
  };

  axios.get("http://localhost:8080/search", { params })
    .then(res => {
      tableData.value = res.data.list;
      totalItems.value = res.data.total;
      
      // 添加分页边界检查
      if (tableData.value.length === 0 && currentPage.value > 1) {
        currentPage.value = 1;
        search(); // 自动跳回第一页
      }
      
      res.data.list.forEach(item => {console.log(res.data.list)
        if (item.puid && !ownerNameCache.value[item.puid]) {
          ownerNameCache.value[item.puid] = item.ownerName;
		  console.log(item.ownerName)
		  
        }
		 // if (item.puid && !ownerPhoneCache.value[item.puid]) {
		 //   ownerPhoneCache.value[item.puid] = item.ownerPhone;
		   
		 // }
		
      });
    })
    .catch(error => {
      console.error("搜索失败:", error);
      ElMessage.error("搜索失败，请重试");
    });
}

// 处理分页变化
function handlePageChange(newPage) {
  currentPage.value = newPage;
  search();
  
  // 添加滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

const getImageUrl = (relativePath: string) => {
  return `http://localhost:8080/images/${relativePath}`;
};

// 购买按钮点击事件
function buy(row: any) {
  selectedProduct.value = {
    pid: row.pid,
    pname: row.pname
  };
  inputPassword.value = '';
  dialogTableVisible.value = true;
}

// 确认购买函数
function determin() {
  if (inputPassword.value !== store.upwd) {
    ElMessage.error("密码错误，购买失败");
    dialogTableVisible.value = false;
    return;
  }
  axios.post("http://localhost:8080/buy", {
     pid: selectedProduct.value.pid,
     uid: store.uid
   })
   .then(res => {
     if (res.data.success) {
       ElMessage.success(`成功购买 ${selectedProduct.value.pname}`);
       
       const index = tableData.value.findIndex(p => p.pid === selectedProduct.value.pid);
       if (index !== -1) {
         tableData.value[index] = {
           ...tableData.value[index],
           puid: res.data.product.puid,
           pstate: "未上架",
           ownerName: store.uname,
		   ownerPhone:store.uphone,
         };
         
         ownerNameCache.value[res.data.product.puid] = store.uname;
		 ownerPhoneCache.value[res.data.product.puid] = store.uphone;
		 search()
		 
       }
     } else {
       ElMessage.error(res.data.message || "购买失败");
     }
     dialogTableVisible.value = false;
   })
 }

// 判断按钮是否禁用
function isButtonDisabled(row) {
  return row.puid == store.uid ;
}
// 获取禁用原因
function getDisabledReason(row) {
  if (row.puid == store.uid) {
    return "(我的商品)";
  }
  return "";
}


// 联系卖家按钮的点击事件
function talk(sellerId) {
  currentSellerId.value = sellerId;
  talkDialogVisible.value = true;
}

// 发送消息给卖家

function sendMessageToSeller() {
  if ( !messageContent.value) {
      ElMessage.warning("消息不能为空");
      return;
  }
  
  axios.post("http://localhost:8080/chat/send", {
    senderId: store.uid,
    receiverId: currentSellerId.value,
    content: messageContent.value
  }).then(res => {
    if (res.data.success) {
      ElMessage.success("消息发送成功");
      messageContent.value = '';
      talkDialogVisible.value = false;
    } else {
      ElMessage.error(res.data.message || "发送失败");
    }
  }).catch(error => {
    ElMessage.error("发送失败: " + error.message);
  });
}

</script>



<style scoped>
.flex {
  display: flex;
  align-items: center;
}

.filter-item {
  display: flex;
  align-items: center;
  margin-right: 15px;
  margin-bottom: 10px;
}

.disabled-hint {
  color: #999;
  font-size: 12px;
  margin-left: 8px;
}

.mt-4 {
  margin-top: 1rem;
}

.pagination-container {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px;
  width: 100%;
}
</style>


