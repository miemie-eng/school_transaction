import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('mysuer', () => {
  const uname = ref(localStorage.getItem('uname') || "")
  const uid = ref(localStorage.getItem('uid') || "")
  const upwd = ref(localStorage.getItem('upwd') || "")
  const uphone = ref(localStorage.getItem('uphone') || "")
  

  const setUserInfo = ( newUid,newUpwd,newUname,newUphone) => {
    uid.value = newUid;
    upwd.value = newUpwd;
	uname.value= newUname;
	uphone.value= newUphone;
    // 同时更新 localStorage
    localStorage.setItem('uid', newUid);
    localStorage.setItem('upwd', newUpwd);
	localStorage.setItem('uname', newUname);
	localStorage.setItem('uphone', newUphone);
	
  }

  return {  uid, upwd, uname,uphone,setUserInfo }
})
