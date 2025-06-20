<template>
  <div class="chat-container">
    <div class="contacts-panel">
      <h3>聊天对象</h3>
      <div 
        v-for="contact in contacts" 
        :key="contact.uid"
        class="contact-item"
        :class="{ 'active': activeContactId === contact.uid }"
        @click="selectContact(contact)"
      >
        {{ contact.uname }}
      </div>
    </div>
    
    <div class="separator"></div>
   
    <div class="chat-panel">
      <div v-if="activeContactId">
        <div class="chat-header">
          <h3>{{ activeContactName }}</h3>
        </div>
        
        <div ref="messagesContainer" class="chat-messages">
          <div 
            v-for="msg in messages" 
            :key="msg.id"
            :class="['message', msg.senderId == store.uid ? 'sent' : 'received']"
          >
            <!-- 发送者消息 -->
            <template v-if="msg.senderId == store.uid">
              <div class="avatar avatar-sent">
                <span class="initials">我</span>
              </div>
              <div class="message-content-wrapper">
                <div class="message-content">{{ msg.content }}</div>
                <div class="message-time">{{ formatTime(msg.sendTime) }}</div>
              </div>
            </template>
            
            <!-- 接收者消息 -->
            <template v-else>
              <div class="avatar avatar-received">
                <span class="initials">{{ activeContactName.charAt(0) }}</span>
              </div>
              <div class="message-content-wrapper">
                <div class="message-content">{{ msg.content }}</div>
                <div class="message-time">{{ formatTime(msg.sendTime) }}</div>
              </div>
            </template>
          </div>
        </div>
        
        <div class="input-area">
          <el-input
            v-model="newMessage"
            type="textarea"
            :rows="3"
            placeholder="输入消息..."
            @keydown.ctrl.enter="sendMessage"
            ref="messageInput"
          ></el-input>
          <el-button 
            type="primary" 
            @click="sendMessage"
            :loading="sending"
            :disabled="sending"
          >
            发送
          </el-button>
        </div>
      </div>
      
      <div v-else class="chat-placeholder">
        请选择聊天对象
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../stores/my-user';

const store = useUserStore();

// 聊天对象列表
const contacts = ref([]);
// 当前选中的聊天对象ID
const activeContactId = ref(0);
// 当前聊天对象名称
const activeContactName = ref('');
// 聊天记录
const messages = ref([]);
// 新消息内容
const newMessage = ref('');
// 发送状态
const sending = ref(false);
// 消息容器引用
const messagesContainer = ref<HTMLElement | null>(null);
// 输入框引用
const messageInput = ref<any>(null);

// 加载聊天对象
function loadContacts() {
  axios.get("http://localhost:8080/chat/contacts", {
    params: { userId: store.uid }
  }).then(res => {
    contacts.value = res.data;
  }).catch(error => {
    console.error("加载聊天对象失败:", error);
    ElMessage.error("加载聊天对象失败");
  });
}

// 选择聊天对象
function selectContact(contact: any) {
  activeContactId.value = contact.uid;
  activeContactName.value = contact.uname;
  loadMessages(contact.uid);
}

// 加载聊天记录
function loadMessages(contactId: number) {
  axios.get("http://localhost:8080/chat/history", {
    params: {
      userId: store.uid,
      contactId: contactId
    }
  }).then(res => {
    messages.value = res.data;
    // 打印消息数据结构
    console.log('加载的消息数据:', res.data);
    nextTick(() => {
      scrollToBottom();
    });
  }).catch(error => {
    console.error("加载聊天记录失败:", error);
    ElMessage.error("加载聊天记录失败");
  });
}

// 发送消息
function sendMessage() {
  if (!newMessage.value.trim()) {
    ElMessage.warning("消息不能为空");
    return;
  }
  
  if (sending.value) return;
  
  sending.value = true;
  
  axios.post("http://localhost:8080/chat/send", {
    senderId: store.uid,
    receiverId: activeContactId.value,
    content: newMessage.value.trim()
  }).then(res => {
    if (res.data.success) {
      // 清空输入框
      newMessage.value = '';
      // 重新加载聊天记录
      loadMessages(activeContactId.value);
      // 聚焦输入框
      nextTick(() => {
        if (messageInput.value) {
          messageInput.value.focus();
        }
      });
    } else {
      ElMessage.error(res.data.message || "发送失败");
    }
  }).catch(error => {
    ElMessage.error("发送失败: " + error.message);
  }).finally(() => {
    sending.value = false;
  });
}

// 格式化时间
function formatTime(timestamp: number) {
  const date = new Date(timestamp);
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  return `${date}`;
}

// 滚动到底部
function scrollToBottom() {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
}

onMounted(() => {
  loadContacts();
});
</script>

<style scoped>
.chat-container {
  display: flex;
  height: calc(100vh - 60px);
  background-color: #f5f7fa;
}

.contacts-panel {
  width: 250px;
  overflow-y: auto;
  padding: 15px;
  position: relative;
  background-color: #ffffff;
  border-right: 1px solid #e9ecef;
}

.separator {
  width: 1px;
  background: linear-gradient(to bottom, #e0e0e0, #c8c8c8, #e0e0e0);
  margin: 0;
  padding: 0;
  flex-shrink: 0;
  box-shadow: 0 0 5px rgba(0,0,0,0.05);
}

.contact-item {
  padding: 10px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 10px;
}

.contact-item:hover {
  background-color: #f0f2f5;
}

.contact-item.active {
  background-color: #e6f7ff;
  font-weight: 500;
  border-radius: 4px;
}

.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border-left: 1px solid #e9ecef;
}

.chat-header {
  padding: 15px;
  border-bottom: 1px solid #e9ecef;
  background-color: #ffffff;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  z-index: 1;
}

.chat-messages {
  flex: 1;
  max-height: 400px; /* 固定高度 */
  overflow-y: auto; /* 添加滚动条 */
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: #ffffff;
}

.message {
  display: flex;
  align-items: flex-end;
  margin-bottom: 10px;
  max-width: 85%;
  animation: fadeIn 0.3s ease-out;
}

.message.sent {
  align-self: flex-end;
  flex-direction: row;
}

.message.received {
  align-self: flex-start;
  flex-direction: row;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #d9d9d9;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #333;
  flex-shrink: 0;
}

.avatar-sent {
  margin-right: 10px;
  background-color: #a0d911;
}

.avatar-received {
  margin-left: 10px;
  background-color: #1890ff;
}

.message-content-wrapper {
  display: flex;
  flex-direction: column;
  max-width: calc(100% - 42px);
}

.message.sent .message-content {
  background-color: #a0d911;
  color: white;
  border-radius: 18px 18px 0 18px;
  padding: 10px 15px;
  font-size: 14px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  word-wrap: break-word;
}

.message.received .message-content {
  background-color: #ffffff;
  color: #333;
  border-radius: 18px 18px 18px 0;
  padding: 10px 15px;
  font-size: 14px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  word-wrap: break-word;
}

.message-time {
  font-size: 10px;
  color: #888;
  margin-top: 3px;
  padding: 0 5px;
}

.message.sent .message-time {
  text-align: right;
}

.message.received .message-time {
  text-align: left;
}

.input-area {
  padding: 15px;
  background-color: #ffffff;
  border-top: 1px solid #e9ecef;
  display: flex;
  gap: 10px;
}

.input-area .el-textarea {
  flex: 1;
}

.chat-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #999;
  font-size: 14px;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>



