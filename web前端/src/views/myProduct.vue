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
          
          <div class="filter-item">
            <span class="filter-label">状态：</span>
            <el-select 
              v-model="state" 
              placeholder="选择商品状态" 
              clearable
              style="width: 240px"
            >
              <el-option label="在售" value="在售" />
              <el-option label="未上架" value="未上架" />
            </el-select>
          </div>
        </div>
  
  <el-button type="primary" :icon="Search" @click="getMyProducts">搜索</el-button>
  <el-button type="primary" @click="dialogTableVisible=true">添加商品</el-button>
  
  
  
  <!-- 添加商品对话框 - 添加商品描述字段 -->
  <el-dialog v-model="dialogTableVisible" title="添加商品信息" width="400">
    <div>
      商品名称：
      <el-input
        v-model="pname"
        style="width: 240px"
        placeholder="请输入"
        clearable
      /><br />
      商品描述：
      <el-input
        v-model="pdescription"
        style="width: 240px"
        placeholder="请输入商品描述"
        clearable
        type="textarea"
        :rows="2"
      /><br />
      商品种类：
      <el-input
        v-model="ptype"
        style="width: 240px"
        placeholder="请输入"
        clearable
      /><br />  
      商品价格：
      <el-input
        v-model="pvalue"
        style="width: 240px"
        placeholder="请输入"
        clearable
      /><br /> 
      商品图片：
      <el-upload
        action="http://localhost:8080/upload"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
        accept="image/*"
        class="mb-2"
      >
        <el-button type="primary">上传图片</el-button>
      </el-upload>
      <el-input
        v-model="pimage"
        style="width: 240px"
        placeholder="图片URL"
        clearable
        disabled
      /><br />
      <el-button type="primary" @click="addpro">添加商品</el-button>
    </div>
  </el-dialog>
  
  <!-- 修改商品对话框 - 添加商品描述字段 -->
  <el-dialog v-model="updialogTableVisible" title="修改我的产品信息" width="400">
    <div>
      商品名称：
      <el-input
        v-model="uppname"
        style="width: 240px"
        placeholder="请输入"
        clearable
      /><br />
      
      商品描述：
      <el-input
        v-model="uppdescription"
        style="width: 240px"
        placeholder="请输入商品描述"
        clearable
        type="textarea"
        :rows="2"
      /><br />
      
      商品类型：
      <el-input
        v-model="upptype"
        style="width: 240px"
        placeholder="请输入"
        clearable
      /><br />
      
      商品价格：
      <el-input
        v-model="uppvalue"
        style="width: 240px"
        placeholder="请输入"
        clearable
      /><br />
      
      商品图片：
      <el-upload
        action="http://localhost:8080/upload"
        :on-success="handleUpdateUploadSuccess"
        :show-file-list="false"
        accept="image/*"
        class="mb-2"
      >
        <el-button type="primary">上传新图片</el-button>
      </el-upload>
      <el-input
        v-model="uppimag"
        style="width: 240px"
        placeholder="图片URL"
        clearable
        disabled
      /><br />
      
      <el-button type="primary" @click="uppro">修改商品</el-button>
    </div>
  </el-dialog>
  
  <!-- 商品表格 - 添加商品描述列 -->
  <el-table :data="myTableData" style="width: 100%">
    <el-table-column prop="pname" label="商品名字" width="150" />
    <el-table-column prop="pdescription" label="商品描述" width="200" />
    <el-table-column prop="ptype" label="商品种类" width="120" />
    <el-table-column prop="pvalue" label="商品价格" width="120" />
    <el-table-column prop="pstate" label="商品状态" width="100" />
    <el-table-column label="商品图片" width="200">
        <template #default="scope">
          <!-- 使用后端提供的图片URL -->
          <img 
            v-if="scope.row.pimage"
            :src="getImageUrl(scope.row.pimage)" 
            style="width: 100px; height: 100px; object-fit: cover;"
          />
          <span v-else>无图片</span>
        </template>
      </el-table-column>
    <el-table-column label="操作" min-width="150">
      <template #default="scope">
        <el-button 
              link 
              type="primary" 
              size="small" 
              @click="toggleShelf(scope.row)"
            >
              {{ scope.row.pstate === '在售' ? '下架' : '上架' }}
            </el-button>
            
        <el-button link type="primary" size="small" @click="deleteProduct(scope.row.pid)">
          删除
        </el-button>
        <el-button link type="primary" size="small" @click="updt(scope.row)" >
          修改
        </el-button>
      </template>
    </el-table-column>
  </el-table>
  <!-- 分页组件 -->
 <div class="pagination-container">
     <el-pagination
       background
       layout="prev, pager, next"
       :total="myTotalItems"
       :page-size="pageSize"
       v-model:current-page="currentPage"
       @current-change="handlePageChange"
     />
   </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../stores/my-user';
import { Search } from '@element-plus/icons-vue';


// 新增变量 - 我的产品数据
const myTableData = ref([]);
const myTotalItems = ref(0);
const store = useUserStore();

// 表格数据
const tableData = ref([]);

// 商品字段 - 添加商品描述
const pname = ref("");
const pdescription = ref("");
const pvalue = ref("");
const ptype = ref("");
const pimage = ref("");

// 修改商品字段 - 添加商品描述
const uppname = ref("");
const uppdescription = ref("");
const upptype = ref("");
const uppvalue = ref("");
const uppimag = ref("");
const uppid = ref("");

const uid = ref("");
const page = ref("");

// 对话框状态
const dialogTableVisible = ref(false);
const updialogTableVisible = ref(false);

// 搜索条件
const state = ref('');
const type = ref('');
const name = ref('');
const value = ref('');
const day = ref('');

// 分页相关
const currentPage = ref(1);
const pageSize = ref(4);
const totalItems = ref(0);

// 页面加载时获取所有商品
onMounted(() => {
  getMyProducts();

});

// URL验证函数
function isValidUrl(url) {
  try {
    new URL(url);
    return true;
  } catch (e) {
    return false;
  }
}

// 图片URL处理方法
const getImageUrl = (relativePath: string) => {
  // 将后端返回的相对路径转换为完整URL
  return `http://localhost:8080/images/${relativePath}`;
};


function getMyProducts() {
  const params = {
    uid: store.uid,
    day: day.value,
    type: type.value,
    name: name.value,
    state: state.value,
    page: currentPage.value,
    pageSize: pageSize.value
  };

  axios.get("http://localhost:8080/myProducts", { params })
    .then(res => {
   
      
      if (res.data && res.data.list && res.data.total !== undefined) {
        myTableData.value = res.data.list;
        myTotalItems.value = res.data.total;
      } else {
        console.error("API返回数据格式不正确:", res.data);
        ElMessage.error("服务器返回的数据格式不正确");
      }
    })
    .catch(error => {
      console.error("加载商品失败:", {
        url: error.config.url,
        params: error.config.params,
        response: error.response?.data,
        message: error.message
      });
      ElMessage.error("加载商品失败，请稍后重试");
    });}

// 修改上架/下架后的回调
function toggleShelf(row) {
  const newState = row.pstate === '在售' ? '未上架' : '在售';
  const action = newState === '在售' ? '上架' : '下架';
  
  // 上架时调用 onShelf，下架时调用 updateState
  const endpoint = newState === '在售' ? 
        "http://localhost:8080/onShelf" : 
        "http://localhost:8080/updateState";
  
  axios.post(endpoint, {
    pid: row.pid,
    state: newState
  })
  .then(res => {
    if (res.data && res.data.success) {
      ElMessage.success(`商品${action}成功`);
      getMyProducts();
    } else {
      const errorMsg = res.data?.message || `${action}失败`;
      ElMessage.error(errorMsg);
    }
  })
  .catch(error => {
    console.error(`${action}出错:`, error);
    ElMessage.error(`${action}失败，请稍后重试`);
  });
}

// 搜索函数
function search() {
  const params = {
    day: day.value,
    type: type.value,
    name: name.value,
    state: state.value,
    page: currentPage.value,
    pageSize: pageSize.value,
    loadAll: currentPage.value === 1 && !day.value && !type.value && !name.value && !state.value
  };

  axios.get("http://localhost:8080/search", { params })
    .then(res => {
      tableData.value = res.data.list;
      totalItems.value = res.data.total;
    })
    .catch(error => {
      ElMessage.error("加载商品失败，请稍后重试");
      console.error(error);
    });
}

// 处理分页变化
function handlePageChange(newPage) {
  currentPage.value = newPage;
    getMyProducts(); 
}

// 修改上传成功处理函数
function handleUploadSuccess(response) {
  if (response && response.path) { 
    pimage.value = response.path; // 存储相对路径
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败');
  }
}

function handleUpdateUploadSuccess(response) {
  if (response && response.path) { 
    uppimag.value = response.path; // 存储相对路径
    ElMessage.success('图片上传成功');
  } else {
    ElMessage.error('图片上传失败');
  }
}
// 删除商品
function deleteProduct(no) {
  axios.get("http://localhost:8080/delete?pid="+no)
    .then(res => {
      if(res.data > 0) {
        ElMessage.success("删除成功");
        getMyProducts()
      } else {
        ElMessage.error("删除失败");
      }
    });
}

// 打开修改对话框 - 添加商品描述
function updt(row) {
  updialogTableVisible.value = true;
  uppname.value = row.pname;
  uppdescription.value = row.pdescription;
  upptype.value = row.ptype;
  uppvalue.value = row.pvalue;
  uppimag.value = row.pimage;
  uppid.value = row.pid;
}

// 修改商品 - 添加商品描述
function uppro() {
  let obj = {
    pname: uppname.value,
    pdescription: uppdescription.value,
    ptype: upptype.value,
    pvalue: uppvalue.value,
    pimage: uppimag.value,
    pid: uppid.value,
  };
  
  axios.post("http://localhost:8080/update", obj)
    .then(res => {
      if(res.data > 0) {
        ElMessage.success("修改成功");
        updialogTableVisible.value = false;
        getMyProducts()
      }
    });
}

// 添加商品 - 添加商品描述
function addpro() {
  let obj = {
    pname: pname.value,
    pdescription: pdescription.value,
    pvalue: pvalue.value,
    ptype: ptype.value,
    pstate: "未上架",
    pimage: pimage.value,
    ptime: "",
    puid: store.uid,
    puname: store.uname,
  };
  
  axios.post("http://localhost:8080/addpro", obj)
    .then(res => {
      if(res.data > 0) {
        ElMessage.success("添加成功");
        dialogTableVisible.value = false;
        getMyProducts()
        
        // 清空表单
        pname.value = "";
        pdescription.value = "";
        pvalue.value = "";
        ptype.value = "";
        pimage.value = "";
      }
    });
}

// // 商品上架功能
// function onShelf(row) {
//   // 检查商品状态
//   if (row.pstate === '在售') {
//     ElMessage.warning('商品已经在售');
//     return;
//   }
  
//   axios.post("http://localhost:8080/onShelf", { pid: row.pid })
//     .then(res => {
//       if (res.data.success) {
//         ElMessage.success('商品上架成功');
        
//         // 更新本地状态
//         const index = tableData.value.findIndex(p => p.pid === row.pid);
//         if (index !== -1) {
//           tableData.value[index].pstate = '在售';
//         }
//       } else {
//         ElMessage.error(res.data.message || '上架失败');
//       }
//     })
//     .catch(error => {
//       console.error('上架出错:', error);
//       ElMessage.error('上架失败，请稍后重试');
//     });
// }


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

.pagination-container {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px;
  width: 100%;
}
</style>