<template>
  <div class="qa-page">
    <!-- 顶部搜索和操作区 -->
    <div class="filter-item">
      <el-input
        v-model="searchName"
        class="search-input"
        placeholder="搜索问题..."
        :prefix-icon="Search"
        clearable
        @keyup.enter="searchQuestions"
      />
      <el-button type="primary" :icon="Search" @click="searchQuestions">搜索</el-button>
      <el-button type="success" :icon="Plus" @click="showQuestionDialog">提问</el-button>
    </div>

    <!-- 主要内容区 -->
    <div class="qa-container">
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>

      <!-- 问题列表 -->
      <div class="questions-list">
        <el-collapse 
          v-for="question in currentQuestions" 
          :key="question.id" 
          class="question-card"
          @change="() => handleCollapseChange(question.id)"
        >
          <el-collapse-item>
            <template #title>
              <div class="question-header">
                <div class="question-title-section">
                  <el-tag size="small" type="info" class="question-tag">问题</el-tag>
                  <span class="question-title">{{ question.title }}</span>
                </div>
                <div class="question-meta">
                  <el-avatar :size="24" class="user-avatar">
                    {{ question.userName?.charAt(0) }}
                  </el-avatar>
                  <span class="question-author">{{ question.userName || '未知用户' }}</span>
                  <span class="question-time">{{ formatTime(question.time) }}</span>
                </div>
              </div>
            </template>
            
            <div class="question-content">
              {{ question.content }}
            </div>
            
            <el-collapse-item title="评论" class="comments-section">
              <div class="comments-list">
                <div v-for="comment in question.comments" :key="comment.id" class="comment-item">
                  <div class="comment-header">
                    <div class="comment-author-info">
                      <el-avatar :size="20" class="user-avatar">
                        {{ comment.userName?.charAt(0) }}
                      </el-avatar>
                      <span class="comment-author">{{ comment.userName || '未知用户' }}</span>
                    </div>
                    <span class="comment-time">{{ formatTime(comment.time) }}</span>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                </div>
                
                <div class="new-comment">
                  <el-input 
                    v-model="question.newComment" 
                    placeholder="写下你的评论..." 
                    type="textarea" 
                    :rows="2"
                    resize="none"
                  />
                  <el-button 
                    type="primary" 
                    @click="submitComment(question)"
                    :loading="question.submitting"
                  >
                    发布评论
                  </el-button>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>

    <!-- 提问对话框 -->
    <el-dialog 
      v-model="questionDialogVisible" 
      title="发布新问题" 
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="newQuestionForm" :rules="questionRules" ref="questionFormRef">
        <el-form-item label="问题标题" prop="title">
          <el-input v-model="newQuestionForm.title" placeholder="请输入问题标题" />
        </el-form-item>
        <el-form-item label="问题内容" prop="content">
          <el-input 
            v-model="newQuestionForm.content" 
            type="textarea" 
            placeholder="请详细描述你的问题..."
            :rows="6"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="questionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitQuestion" :loading="submitting">
          发布问题
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Search, Plus } from '@element-plus/icons-vue';
import { useUserStore } from '../stores/my-user';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const store = useUserStore();
const router = useRouter();

// 分页相关
const currentPage = ref(1);
const pageSize = ref(4);
const total = ref(0);
const totalPages = ref(0);

// 搜索相关
const searchName = ref('');
const searchTimeout = ref<number | null>(null);

// 提问对话框相关
const questionDialogVisible = ref(false);
const questionFormRef = ref();
const submitting = ref(false);
const newQuestionForm = ref({
  title: '',
  content: ''
});

// 表单验证规则
const questionRules = {
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 10, max: 2000, message: '内容长度在 10 到 2000 个字符', trigger: 'blur' }
  ]
};

// 问题数据
const questions = ref<Array<any>>([]);
const currentQuestions = computed(() => questions.value);

// 检查用户是否登录
const checkUserLogin = () => {
  if (!store.uid) {
    ElMessage.warning('请先登录');
    router.push('/login');
    return false;
  }
  return true;
};

// 初始化加载问题
onMounted(() => {
  loadQuestions();
  // 添加搜索防抖
  searchName.value && debounceSearch();
});

// 搜索防抖
const debounceSearch = () => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = window.setTimeout(() => {
    searchQuestions();
  }, 300);
};

// 加载问题列表
const loadQuestions = async () => {
  try {
    const response = await axios.get('/api/qa/questions', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    });
    
    if (!response.data?.questions || !Array.isArray(response.data.questions)) {
      ElMessage.error('问题数据格式错误');
      return;
    }
    
    questions.value = response.data.questions.map((q: any) => ({
      ...q,
      comments: [],
      newComment: '',
      submitting: false
    }));
    
    total.value = response.data.totalItems || 0;
    totalPages.value = response.data.totalPages || 0;
  } catch (error) {
    ElMessage.error('加载问题失败');
    console.error('加载问题失败:', error);
  }
};


// 搜索问题
const searchQuestions = async () => {
  try {
    const url = searchName.value 
      ? `/api/qa/questions/search`
      : '/api/qa/questions';
    
    const response = await axios.get(url, {
      params: {
        keyword: searchName.value,
        page: currentPage.value,
        size: pageSize.value
      }
    });

    if (!response.data?.questions || !Array.isArray(response.data.questions)) {
      ElMessage.error('问题数据格式错误');
      return;
    }

    questions.value = response.data.questions.map((q: any) => ({
      ...q,
      comments: [],
      newComment: '',
      submitting: false
    }));
    total.value = response.data.totalItems || 0;
    totalPages.value = response.data.totalPages || 0;
  } catch (error) {
    ElMessage.error('搜索问题失败');
    console.error('搜索问题失败:', error);
  }
};

// 展开问题时加载评论
const handleCollapseChange = async (questionId: number) => {
  const question = questions.value.find(q => q.id === questionId);
  if (question && (!question.comments || question.comments.length === 0)) {
    try {
      const response = await axios.get(`/api/qa/comments?questionId=${questionId}`);
      question.comments = response.data;
    } catch (error) {
      ElMessage.error('加载评论失败');
      console.error('加载评论失败:', error);
    }
  }
};

// 提交新问题
const submitQuestion = async () => {
  if (!checkUserLogin()) return;
  if (!questionFormRef.value) return;
  
  try {
    await questionFormRef.value.validate();
    submitting.value = true;
    
    const questionData = {
      title: newQuestionForm.value.title,
      content: newQuestionForm.value.content,
      user_id: Number(store.uid)
    };
    
    await axios.post('/api/qa/questions', questionData);
    
    newQuestionForm.value = { title: '', content: '' };
    questionDialogVisible.value = false;
    ElMessage.success('问题发布成功');
    
    // 重新加载第一页数据
    currentPage.value = 1;
    await loadQuestions();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error(error.message);
    } else {
      ElMessage.error('提交问题失败');
    }
    console.error('提交问题失败:', error);
  } finally {
    submitting.value = false;
  }
};

// 提交评论
const submitComment = async (question: any) => {
  if (!checkUserLogin()) return;
  if (!question.newComment) {
    ElMessage.warning('评论内容不能为空');
    return;
  }
  
  try {
    question.submitting = true;
    const commentData = {
      question_id: question.id,
      content: question.newComment,
      user_id: Number(store.uid)
    };
    
    await axios.post('/api/qa/comments', commentData);
    
    question.newComment = '';
    ElMessage.success('评论发布成功');
    
    // 重新加载当前问题的评论
    const response = await axios.get(`/api/qa/comments?questionId=${question.id}`);
    question.comments = response.data;
  } catch (error) {
    ElMessage.error('提交评论失败');
    console.error('提交评论失败:', error);
  } finally {
    question.submitting = false;
  }
};

// 格式化时间
const formatTime = (timeStr: string) => {
  try {
    const date = new Date(timeStr);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    // 小于1分钟
    if (diff < 60000) {
      return '刚刚';
    }
    // 小于1小时
    if (diff < 3600000) {
      return `${Math.floor(diff / 60000)}分钟前`;
    }
    // 小于24小时
    if (diff < 86400000) {
      return `${Math.floor(diff / 3600000)}小时前`;
    }
    // 小于30天
    if (diff < 2592000000) {
      return `${Math.floor(diff / 86400000)}天前`;
    }

    return date.toLocaleDateString();
  } catch (e) {
    return timeStr;
  }
};

// 分页变化处理
const handlePageChange = (page: number) => {
  currentPage.value = page;
  if (searchName.value) {
    searchQuestions();
  } else {
    loadQuestions();
  }
};

// 显示提问对话框
const showQuestionDialog = () => {
  if (!checkUserLogin()) return;
  questionDialogVisible.value = true;
};
</script>

<style scoped>
.qa-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.filter-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.search-input {
  width: 300px;
}

.qa-container {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.pagination-container {
  margin-bottom: 24px;
  display: flex;
  justify-content: center;
}

.questions-list {
  margin-top: 24px;
}

.question-card {
  margin-bottom: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.question-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 12px 20px;
}

.question-title-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.question-tag {
  margin-right: 8px;
}

.question-title {
  font-weight: 600;
  font-size: 16px;
  color: #2c3e50;
}

.question-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.user-avatar {
  background-color: #409EFF;
  color: #fff;
  font-size: 12px;
}

.question-time {
  color: #999;
  font-size: 12px;
}

.question-content {
  padding: 16px 20px;
  line-height: 1.6;
  color: #2c3e50;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin: 0 20px 16px;
}

.comments-section {
  margin: 0 20px;
}

.comments-list {
  padding: 16px 0;
}

.comment-item {
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-author {
  font-weight: 500;
  color: #2c3e50;
}

.comment-time {
  color: #999;
  font-size: 12px;
}

.comment-content {
  line-height: 1.5;
  color: #2c3e50;
  padding-left: 28px;
}

.new-comment {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
}

.new-comment .el-button {
  margin-top: 12px;
}

:deep(.el-collapse-item__header) {
  font-size: 16px;
  padding: 0;
}

:deep(.el-collapse-item__content) {
  padding: 0;
}

:deep(.el-collapse-item__wrap) {
  border: none;
}
</style>